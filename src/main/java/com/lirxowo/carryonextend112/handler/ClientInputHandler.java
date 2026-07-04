package com.lirxowo.carryonextend112.handler;

import com.lirxowo.carryonextend112.Config;
import com.lirxowo.carryonextend112.Constants;
import com.lirxowo.carryonextend112.network.MessageThrowRequest;
import com.lirxowo.carryonextend112.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tschipp.carryon.common.item.ItemEntity;
import tschipp.carryon.common.item.ItemTile;

/**
 * Sadece istemci tarafında çalışır. İki iş yapar:
 *  1) Shift basılıyken Q'ya basılınca -> sunucuya fırlatma isteği gönder.
 *  2) Shift basılıyken scroll -> yerel güç değerini ayarla (0-1 arası).
 *
 * Not: 1.12.2'de "InputEvent.KeyInputEvent" tuş basımını bildirir ama hangi
 * tuşun basıldığını söylemez; bu yüzden tick içinde Keyboard.isKeyDown ile
 * "Bırak" (drop) tuşunun bu tick'te YENİ basıldığını (önceki tick'te basılı
 * değildi) kontrol ediyoruz. Bu, orijinal moddaki davranışla birebir aynıdır.
 */
@SideOnly(Side.CLIENT)
public class ClientInputHandler {

    private float localPower = 0f;
    private boolean dropWasDown = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (player == null || mc.currentScreen != null) {
            dropWasDown = false;
            return;
        }

        if (!isCarrying(player)) {
            dropWasDown = false;
            return;
        }

        boolean dropDown = mc.gameSettings.keyBindDropItem.isKeyDown();
        boolean sneaking = player.isSneaking();

        if (dropDown && !dropWasDown && sneaking) {
            PacketHandler.INSTANCE.sendToServer(new MessageThrowRequest(localPower));
        }
        dropWasDown = dropDown;
    }

    @SubscribeEvent
    public void onMouseScroll(InputEvent.MouseScrollEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (player == null || !player.isSneaking() || !isCarrying(player)) {
            return;
        }

        int delta = Mouse.getEventDWheel();
        if (delta == 0) {
            return;
        }

        float step = Config.scrollStep;
        if (delta > 0) {
            localPower = Math.min(Constants.POWER_MAX, localPower + step);
        } else {
            localPower = Math.max(Constants.POWER_MIN, localPower - step);
        }

        // Fare tekerleğinin varsayılan işlevini (hotbar değiştirme) burada engelle,
        // çünkü sneak+scroll güç ayarı için kullanılıyor.
        event.setCanceled(true);

        if (player.world.getTotalWorldTime() % 4 == 0) {
            player.sendStatusMessage(new net.minecraft.util.text.TextComponentString(
                    net.minecraft.util.text.TextFormatting.GRAY + "Fırlatma gücü: "
                            + net.minecraft.util.text.TextFormatting.YELLOW
                            + Math.round(localPower * 100) + "%"), true);
        }
    }

    private boolean isCarrying(EntityPlayerSP player) {
        net.minecraft.item.ItemStack stack = player.getHeldItemMainhand();
        return stack.getItem() instanceof ItemTile || stack.getItem() instanceof ItemEntity;
    }
}
