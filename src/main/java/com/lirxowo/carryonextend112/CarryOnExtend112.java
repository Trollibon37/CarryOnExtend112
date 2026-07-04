package com.lirxowo.carryonextend112;

import com.lirxowo.carryonextend112.network.PacketHandler;
import com.lirxowo.carryonextend112.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CarryOnExtend - 1.12.2 portu.
 * Orijinal mod (1.20.1, LirxOwO) modern CarryOn'un "capability" tabanlı
 * carry sistemine bağlıydı. 1.12.2'deki CarryOn (Tschipp) taşınan blok/varlığı
 * capability yerine oyuncunun elindeki ItemStack (ItemTile / ItemEntity)
 * içinde NBT olarak tutuyor. Bu port o farka göre uyarlanmıştır; mantık ve
 * "his" (fırlatma gücü formülü, güç ayarı) orijinaliyle aynı tutulmuştur.
 */
@Mod(
        modid = Constants.MOD_ID,
        name = Constants.MOD_NAME,
        version = Constants.VERSION,
        dependencies = "required-after:carryon@[1.12.0,);required-after:forge@[14.23.5.2838,)"
)
public class CarryOnExtend112 {

    @Mod.Instance(Constants.MOD_ID)
    public static CarryOnExtend112 instance;

    public static final Logger LOG = LogManager.getLogger(Constants.MOD_NAME);

    @SidedProxy(
            clientSide = "com.lirxowo.carryonextend112.proxy.ClientProxy",
            serverSide = "com.lirxowo.carryonextend112.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Config.load(event.getSuggestedConfigurationFile());
        PacketHandler.registerMessages();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}
