package com.lirxowo.carryonextend112.handler;

import com.lirxowo.carryonextend112.Config;
import com.lirxowo.carryonextend112.Constants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tschipp.carryon.common.item.ItemEntity;
import tschipp.carryon.common.item.ItemTile;

/**
 * CarryOn 1.12.2 taşınan bloğu/varlığı oyuncunun ANA ELİNDE bir ItemStack
 * (ItemTile ya da ItemEntity) olarak tutar (capability DEĞİL). Bu yüzden
 * "fırlatma" burada: elindeki stack'i oku -> gerçek bloğu/varlığı NBT'den
 * geri kur -> hız ver -> eli boşalt, şeklinde çalışır.
 *
 * Fizik formülü orijinal 1.20.1 modundan (decompile edilerek) birebir alınmıştır:
 *   P = 1 + power * MAX_POWER_MULTIPLIER
 *   vx = lookX * BASE_THROW_POWER * P
 *   vy = BASE_THROW_UPWARD * P
 *   vz = lookZ * BASE_THROW_POWER * P
 */
public class ThrowHandler {

    public static void throwCarried(EntityPlayerMP player) {
        throwCarriedWithPower(player, 0f);
    }

    public static void throwCarriedWithPower(EntityPlayerMP player, float power) {
        World world = player.getServerWorld();
        EnumHand hand = EnumHand.MAIN_HAND;
        ItemStack held = player.getHeldItem(hand);

        if (held.isEmpty()) {
            return;
        }

        boolean handled = false;

        if (held.getItem() instanceof ItemTile) {
            handled = throwBlock(player, world, held, power);
        } else if (held.getItem() instanceof ItemEntity) {
            handled = throwEntity(player, world, held, power);
        }

        if (!handled) {
            return;
        }

        // Eli boşalt (blok/varlık artık dünyada kendi başına).
        player.setHeldItem(hand, ItemStack.EMPTY);

        if (Config.playThrowSound) {
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.4f, 1.6f);
        }

        player.swingArm(hand);
    }

    private static boolean throwBlock(EntityPlayerMP player, World world, ItemStack stack, float power) {
        IBlockState state = ItemTile.getBlockState(stack);
        if (state == null || state.getBlock() == net.minecraft.init.Blocks.AIR) {
            return false;
        }

        Vec3d spawnPos = player.getPositionEyes(1.0f).add(player.getLookVec().scale(0.5));
        Vec3d velocity = computeVelocity(player, power);

        EntityFallingBlock fallingBlock = new EntityFallingBlock(world, spawnPos.x, spawnPos.y, spawnPos.z, state);
        fallingBlock.motionX = velocity.x;
        fallingBlock.motionY = velocity.y;
        fallingBlock.motionZ = velocity.z;
        fallingBlock.fallTime = 1;

        if (ItemTile.hasTileData(stack)) {
            NBTTagCompound tileData = ItemTile.getTileData(stack);
            if (tileData != null) {
                fallingBlock.tileEntityData = tileData.copy();
            }
        }

        world.spawnEntity(fallingBlock);
        return true;
    }

    private static boolean throwEntity(EntityPlayerMP player, World world, ItemStack stack, float power) {
        if (!ItemEntity.hasEntityData(stack)) {
            return false;
        }

        Entity entity = ItemEntity.getEntity(stack, world);
        if (entity == null) {
            return false;
        }

        Vec3d spawnPos = player.getPositionEyes(1.0f).add(player.getLookVec().scale(0.5));
        Vec3d velocity = computeVelocity(player, power);

        entity.setLocationAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, player.rotationYaw, 0f);
        entity.motionX = velocity.x;
        entity.motionY = velocity.y;
        entity.motionZ = velocity.z;

        world.spawnEntity(entity);
        return true;
    }

    private static Vec3d computeVelocity(EntityPlayerMP player, float power) {
        float p = 1f + power * Constants.MAX_POWER_MULTIPLIER;
        Vec3d look = player.getLookVec();
        double vx = look.x * Constants.BASE_THROW_POWER * p;
        double vy = Constants.BASE_THROW_UPWARD * p;
        double vz = look.z * Constants.BASE_THROW_POWER * p;
        return new Vec3d(vx, vy, vz);
    }

}
