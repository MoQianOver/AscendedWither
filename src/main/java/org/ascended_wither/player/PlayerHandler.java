package org.ascended_wither.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerHandler
{
   public PlayerHandler()
   {
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onMobConstructing(AttachCapabilitiesEvent<Entity> event)
   {
      if (event.getObject() instanceof Player)
      {
         event.addCapability(PlayerData.NBT_TAG, PlayerData.getPlayerDataProvider());
      }
   }

   @SubscribeEvent
   public void onEntityDieing(PlayerEvent.Clone event)
   {
      if (event.isWasDeath())
      {
         PlayerData target = PlayerData.getPlayerData(event.getEntity());
         PlayerData source = PlayerData.getPlayerData(event.getOriginal());
         if (target != null && source != null)
         {
            target.clone(source);
         }
      }
   }
}
