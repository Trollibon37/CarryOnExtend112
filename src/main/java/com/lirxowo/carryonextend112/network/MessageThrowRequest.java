package com.lirxowo.carryonextend112.network;

import com.lirxowo.carryonextend112.handler.ThrowHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * İstemciden sunucuya: "elimdekini şu güçle fırlat" isteği.
 * Güç değeri istemcide Shift+Scroll ile ayarlanan, 0.0-1.0 aralığındaki değerdir.
 */
public class MessageThrowRequest implements IMessage {

    private float power;

    public MessageThrowRequest() {
    }

    public MessageThrowRequest(float power) {
        this.power = power;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.power = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(power);
    }

    public static class Handler implements IMessageHandler<MessageThrowRequest, IMessage> {
        @Override
        public IMessage onMessage(final MessageThrowRequest message, final MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> ThrowHandler.throwCarriedWithPower(player, message.power));
            return null;
        }
    }
}
