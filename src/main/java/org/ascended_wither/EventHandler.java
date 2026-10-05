package org.ascended_wither;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = AscendedWither.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler
{
   @SubscribeEvent
   public static void onConfigLoad(ModConfigEvent.Loading event)
   {
      if (event.getConfig().getModId().equals(AscendedWither.MODID))
      {

         AscendedWither.CONFIG.setActiveConfig(event.getConfig());
         AscendedWither.CONFIG.sync();
      }
   }

   @SubscribeEvent
   public static void onConfigReload(ModConfigEvent.Reloading event)
   {
      if (event.getConfig().getModId().equals(AscendedWither.MODID))
      {
         AscendedWither.CONFIG.setActiveConfig(event.getConfig());
         AscendedWither.CONFIG.sync();
      }
   }
}
