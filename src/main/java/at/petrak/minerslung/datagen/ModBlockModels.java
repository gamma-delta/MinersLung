package at.petrak.minerslung.datagen;

import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockModels extends BlockStateProvider {
    public ModBlockModels(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, MinersLungMod.MOD_ID, efh);
    }

    @Override
    protected void registerStatesAndModels() {
        var torchTex = modLoc("block/signal_torch");
        simpleBlock(ModBlocks.SIGNAL_TORCH.get(), models().torch("signal_torch", torchTex));
        horizontalBlock(ModBlocks.WALL_SIGNAL_TORCH.get(), models().torchWall("wall_signal_torch", torchTex), 90);
    }
}
