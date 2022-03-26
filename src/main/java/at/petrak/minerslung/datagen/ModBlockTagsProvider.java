package at.petrak.minerslung.datagen;

import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, MinersLungMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        // presumably, this method is "add tag"
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.SAFETY_LANTERN.get());
        this.tag(BlockTags.WALL_POST_OVERRIDE).add(ModBlocks.SIGNAL_TORCH.get());
    }
}
