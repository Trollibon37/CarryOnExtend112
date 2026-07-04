package com.lirxowo.carryonextend112;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {

    public static boolean playThrowSound = true;
    public static float scrollStep = Constants.POWER_STEP;

    public static void load(File file) {
        Configuration cfg = new Configuration(file);
        try {
            cfg.load();
            playThrowSound = cfg.getBoolean("playThrowSound", "general", true,
                    "Fırlatma sırasında ses çalınsın mı?");
            scrollStep = (float) cfg.getFloat("scrollStep", "general", Constants.POWER_STEP,
                    0.01f, 1.0f, "Shift+Scroll ile fırlatma gücü her seferinde ne kadar değişsin (0-1 arası oran).");
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }
}
