package at.petrak.minerslung.common.breath;

import at.petrak.minerslung.MinersLungMod;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPointsOfInterest {
    public static final DeferredRegister<PoiType> POIS = DeferredRegister.create(ForgeRegistries.POI_TYPES,
        MinersLungMod.MOD_ID);

    public static final int MAX_BUBBLE_SIZE = 32;

    public static final RegistryObject<PoiType> SOUL_FIRE = POIS.register("soul_fire",
        () -> new PoiType("soul_fire", PoiType.getBlockStates(Blocks.SOUL_FIRE), 0, MAX_BUBBLE_SIZE));
    public static final RegistryObject<PoiType> SOUL_TORCH = POIS.register("soul_torch",
        () -> new PoiType("soul_torch", PoiType.getBlockStates(Blocks.SOUL_TORCH), 0, MAX_BUBBLE_SIZE));
    public static final RegistryObject<PoiType> SOUL_CAMPFIRE = POIS.register("soul_campfire",
        () -> new PoiType("soul_campfire", PoiType.getBlockStates(Blocks.SOUL_CAMPFIRE), 0, MAX_BUBBLE_SIZE));
}
