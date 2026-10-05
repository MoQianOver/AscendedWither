package org.ascended_wither.client;

import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.ascended_wither.AscendedWither;
import org.ascended_wither.entity.EntityRegistry;

@Mod.EventBusSubscriber(modid = AscendedWither.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
   @SuppressWarnings({"unchecked", "rawtypes"})
   @SubscribeEvent
   public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
   {
      event.registerEntityRenderer((EntityType)EntityRegistry.BLAZE_MINION.get(), (EntityRendererProvider)BlazeRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.GHAST_MINION.get(), (EntityRendererProvider)GhastRenderer::new);
      event.registerEntityRenderer((EntityType)EntityRegistry.SKELETON_MINION.get(), (EntityRendererProvider)WitherSkeletonRenderer::new);
   }

   public static void registerConfigScreen()
   {
      ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
            () -> new ConfigScreenHandler.ConfigScreenFactory(
                  (minecraft, parent) -> new AscendedWitherConfigScreen(parent)));
   }
}
