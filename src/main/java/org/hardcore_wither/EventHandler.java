package org.hardcore_wither;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = HardcoreWither.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler
{
   @SubscribeEvent
   public static void onConfigLoad(ModConfigEvent.Loading event)
   {
      if (event.getConfig().getModId().equals(HardcoreWither.MODID))
      {

         HardcoreWither.CONFIG.setActiveConfig(event.getConfig());
         HardcoreWither.CONFIG.sync();
      }
   }

   @SubscribeEvent
   public static void onConfigReload(ModConfigEvent.Reloading event)
   {
      if (event.getConfig().getModId().equals(HardcoreWither.MODID))
      {
         HardcoreWither.CONFIG.setActiveConfig(event.getConfig());
         HardcoreWither.CONFIG.sync();
      }
   }
}
