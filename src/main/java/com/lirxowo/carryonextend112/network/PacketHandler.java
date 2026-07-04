package com.lirxowo.carryonextend112.network;

import com.lirxowo.carryonextend112.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static SimpleNetworkWrapper INSTANCE;

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MOD_ID);
        int id = 0;
        // İstemci -> Sunucu: oyuncu Shift+Q ile fırlatmak istiyor, mevcut güç değeriyle birlikte.
        INSTANCE.registerMessage(MessageThrowRequest.Handler.class, MessageThrowRequest.class, id++, Side.SERVER);
    }
}
