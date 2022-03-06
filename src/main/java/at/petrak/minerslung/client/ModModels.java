package at.petrak.minerslung.client;

import at.petrak.minerslung.common.blocks.ModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModModels {
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent evt) {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SIGNAL_TORCH.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.WALL_SIGNAL_TORCH.get(), RenderType.cutout());
    }
}
