package com.lirxowo.carryonextend112;

public class Constants {
    public static final String MOD_ID = "carryonextend112";
    public static final String MOD_NAME = "CarryOnExtend";
    public static final String VERSION = "1.0";

    // Orijinal 1.20.1 modundan decompile edilerek doğrulanan fizik formülü:
    // powerFactor (P) = 1 + power * MAX_POWER_MULTIPLIER
    // hız = (bakisYonu.x * BASE_THROW_POWER * P, BASE_THROW_UPWARD * P, bakisYonu.z * BASE_THROW_POWER * P)
    public static final float BASE_THROW_POWER = 0.8f;
    public static final float BASE_THROW_UPWARD = 0.3f;
    public static final float MAX_POWER_MULTIPLIER = 1.5f;

    // Shift+Scroll ile güç ayarı: 0.0 (varsayılan) - 1.0 (maksimum) arası, adım 0.1
    public static final float POWER_STEP = 0.1f;
    public static final float POWER_MIN = 0.0f;
    public static final float POWER_MAX = 1.0f;
}
