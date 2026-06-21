package org.user.newmode;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = SeaRedstoneTorch.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientInit {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(
                    SeaRedstoneTorch.SEA_REDSTONE_TORCH.get(),
                    RenderType.cutout()
            );
        });

    }
}