package at.petrak.minerslung;

import at.petrak.minerslung.client.ModClientRenderingAndModelStuff;
import at.petrak.minerslung.common.advancement.ModAdvancementTriggers;
import at.petrak.minerslung.common.blocks.ModBlocks;
import at.petrak.minerslung.common.blocks.SignalTorchBlock;
import at.petrak.minerslung.common.breath.AirBubbleTracker;
import at.petrak.minerslung.common.breath.DrownedOxygent;
import at.petrak.minerslung.common.breath.TickAirChecker;
import at.petrak.minerslung.common.capability.ModCapabilities;
import at.petrak.minerslung.common.items.ModItems;
import at.petrak.minerslung.common.network.ModMessages;
import at.petrak.minerslung.datagen.ModDataGenerators;
import at.petrak.minerslung.datagen.lootmods.ModLootMods;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MinersLungMod.MOD_ID)
public class MinersLungMod {
    public static final String MOD_ID = "minerslung";

    public static Logger LOGGER = LogManager.getLogger();

    private static final MinersLungConfig CONFIG;
    private static final ForgeConfigSpec CONFIG_SPEC;

    static {
        var specPair = new ForgeConfigSpec.Builder().configure(MinersLungConfig::new);
        CONFIG = specPair.getLeft();
        CONFIG_SPEC = specPair.getRight();
    }

    public MinersLungMod() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        var evbus = MinecraftForge.EVENT_BUS;

        ModItems.ITEMS.register(modbus);
        ModBlocks.BLOCKS.register(modbus);
        ModLootMods.LOOT_MODS.register(modbus);
        modbus.register(ModDataGenerators.class);

        evbus.register(TickAirChecker.class);
        evbus.register(SignalTorchBlock.class);
        evbus.register(ModCapabilities.class);
        evbus.register(DrownedOxygent.class);
        evbus.register(AirBubbleTracker.class);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modbus.register(ModClientRenderingAndModelStuff.class);
        });

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC);
        ModAdvancementTriggers.registerTriggers();
        ModMessages.register();
    }
}
