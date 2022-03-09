package at.petrak.minerslung.common.blocks;

import at.petrak.minerslung.MinersLungMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
        MinersLungMod.MOD_ID);

    private static final BlockBehaviour.Properties torchProps = BlockBehaviour.Properties.of(Material.DECORATION)
        .noCollission()
        .instabreak()
        .lightLevel((p_50886_) -> 14)
        .sound(SoundType.WOOD);
    public static final RegistryObject<SignalTorchBlock> SIGNAL_TORCH = BLOCKS.register("signal_torch",
        () -> new SignalTorchBlock(torchProps));
    public static final RegistryObject<WallSignalTorchBlock> WALL_SIGNAL_TORCH = BLOCKS.register("wall_signal_torch",
        () -> new WallSignalTorchBlock(torchProps));
    public static final RegistryObject<SafetyLanternBlock> SAFETY_LANTERN = BLOCKS.register("safety_lantern",
        () -> new SafetyLanternBlock(BlockBehaviour.Properties.of(Material.METAL)
            .requiresCorrectToolForDrops()
            .strength(3.5F)
            .sound(SoundType.LANTERN)
            .lightLevel(bs -> switch (bs.getValue(SafetyLanternBlock.AIR_QUALITY)) {
                case GREEN -> 15;
                case YELLOW -> 12;
                case RED -> 9;
            })
            .randomTicks()
            .noOcclusion()));
}
