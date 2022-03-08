package at.petrak.minerslung.client;

import at.petrak.minerslung.common.blocks.ModBlocks;
import at.petrak.minerslung.common.breath.AirHelper;
import at.petrak.minerslung.common.items.ModItems;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ModClientRenderingAndModelStuff {
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent evt) {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SIGNAL_TORCH.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.WALL_SIGNAL_TORCH.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SAFETY_LANTERN.get(), RenderType.cutout());
    }

    @SubscribeEvent
    public static void registerItemProperties(FMLClientSetupEvent evt) {
        ItemProperties.register(ModItems.SAFETY_LANTERN.get(), ModItems.SAFETY_LANTERN_AIR_QUALITY_PRED,
            (stack, level, maybeEntity, seed) -> {
                var entity = maybeEntity != null ? maybeEntity : stack.getEntityRepresentation();
                if (entity != null) {
                    return switch (AirHelper.getO2LevelFromLocation(entity.getEyePosition(), entity.getLevel())) {
                        case RED -> 0;
                        case YELLOW -> 1;
                        case GREEN -> 2;
                    };
                } else {
                    return 2; // just do green?
                }
            });
    }
}
