package org.ascended_wither.wither;

import java.util.ArrayList;
import java.util.Map;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.ascended_wither.AscendedWither;
import org.ascended_wither.ModInformation;
import org.ascended_wither.config.Config;
import org.ascended_wither.config.Configurable;
import org.ascended_wither.enchantment.EnchantmentRegistry;
import org.ascended_wither.player.PlayerData;

@Configurable(sectionName="Wither")
public class WitherHandler
{
   @Config(comment="Will enable the Looting enchant on weapons to affect the Wither's drops")
   private boolean witherLooting = true;

   @Config
   private boolean netherStarLooting = true;

   @Config(minFloat = 0f, comment="Scales the amount of reward in relation to Wither strength (non-linear)")
   private float lootingLevelMultiplier = 2f;

   @Config(minFloat=0f)
   private float netherStarLootingMultiplier = .5f;

   private WitherWorldData saveData = null;

   public WitherHandler()
   {
      AscendedWither.CONFIG.register(this);
      AscendedWither.CONFIG.register(WitherAffinityHelper.class);
      MinecraftForge.EVENT_BUS.register(this);

      PowerUpHelper.initialize();
   }

   @SubscribeEvent
   public void onWorldLoad(LevelEvent.Load event)
   {
      if (saveData == null && !event.getLevel().isClientSide() && event.getLevel().getServer() != null)
      {
         saveData = WitherWorldData.getWitherWorldSavedData(event.getLevel().getServer());
      }
   }

   @SubscribeEvent
   public void onWorldUnload(@SuppressWarnings("unused") LevelEvent.Unload event)
   {
      if (saveData != null)
      {
         saveData.setDirty();
         saveData = null;
      }
   }

   @SubscribeEvent
   public void onMobConstructing(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event)
   {
      if (event.getObject() != null && event.getObject().getClass() == WitherBoss.class)
      {
         event.addCapability(WitherData.NBT_TAG, WitherData.getWitherDataProvider((WitherBoss)event.getObject()));
      }
   }

   @SubscribeEvent
   public void onSpawnMob(EntityJoinLevelEvent event)
   {
      if (!event.getLevel().isClientSide())
      {
         if (event.getEntity() != null && event.getEntity().getClass() == WitherBoss.class)
         {
            WitherBoss theWither = (WitherBoss)event.getEntity();
            WitherData witherData = WitherData.getWitherData(theWither);
            if (witherData != null && !witherData.isPoweredUp())
            {
               java.util.List<Player> nearbyPlayers = theWither.level().getEntitiesOfClass(Player.class, theWither.getBoundingBox().inflate(64.0D, 64.0D, 64.0D));
               double powerUpSize = 0.0;
               for (Player player : nearbyPlayers)
               {
                  final PlayerData data = PlayerData.getPlayerData(player);
                  if (data != null)
                  {
                     powerUpSize += data.wasAtWitherSpawn();
                  }
               }
               if (saveData != null)
               {
                  if (powerUpSize > saveData.getLargestPowerUp())
                  {
                     saveData.setLargestPowerUp((int) Math.round(powerUpSize));
                  }
                  if (nearbyPlayers.size() == 0)
                  {
                     saveData.setLargestPowerUp(saveData.getLargestPowerUp() + 1);
                     powerUpSize = saveData.getLargestPowerUp();
                  }
               }
               else
               {
                  AscendedWither.LOGGER.error("Cannot access WitherWorldData, did not load properly. Not all data may save.");
               }
               PowerUpHelper.powerUpWither(witherData, (int)Math.round(powerUpSize));
            }
         }
      }
   }

   @SubscribeEvent
   public void onLivingUpdate(LivingTickEvent event)
   {
      if (!event.getEntity().level().isClientSide())
      {
         if (event.getEntity() != null && event.getEntity().getClass() == WitherBoss.class)
         {
            WitherData data = WitherData.getWitherData((WitherBoss) event.getEntity());
            if (data != null)
            {
               data.update();
            }
         }
      }
   }

   @SubscribeEvent
   public void onLivingHurtEvent(LivingHurtEvent event)
   {
      if (event.getEntity() instanceof WitherBoss &&
          event.getSource().getEntity() instanceof Player)
      {
         Player player = (Player) event.getSource().getEntity();
         ItemStack weaponStack = player.getMainHandItem();
         if (!weaponStack.isEmpty() && AscendedWither.API.getWitherAffinityRegistry().isRegistered(weaponStack.getItem()))
         {
            Map<Enchantment, Integer> enchantMap = EnchantmentHelper.getEnchantments(weaponStack);
            Integer witherAffinityLevel = enchantMap.get(EnchantmentRegistry.WITHER_AFFINITY.get());

            if (event.getEntity().getHealth() <= event.getAmount())
            {
               if (witherAffinityLevel == null)
               {
                  enchantMap.put(EnchantmentRegistry.WITHER_AFFINITY.get(), 1);
                  EnchantmentHelper.setEnchantments(enchantMap, weaponStack);
               }
            }
            else if (witherAffinityLevel != null)
            {
               WitherAffinityHelper.setWitherAffinityXp(weaponStack, WitherAffinityHelper.getWitherAffinityXp(weaponStack) + Math.round(event.getAmount()));

               if (WitherAffinityHelper.shouldWitherAffinityLevelUp(weaponStack, witherAffinityLevel))
               {
                  enchantMap.put(EnchantmentRegistry.WITHER_AFFINITY.get(), witherAffinityLevel + 1);
                  EnchantmentHelper.setEnchantments(enchantMap, weaponStack);
               }

               if (AscendedWither.RAND.nextFloat() < WitherAffinityHelper.chanceCalc(witherAffinityLevel))
               {
                  WitherData witherData = WitherData.getWitherData((WitherBoss) event.getEntity());
                  if (witherData != null)
                  {
                     PowerUpHelper.reduceWitherPowerUp(witherData);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void witherLootDrops(LivingDropsEvent event)
   {
      if (!event.getEntity().level().isClientSide() && event.getEntity() != null && event.getEntity().getClass() == WitherBoss.class)
      {
         WitherBoss wither = (WitherBoss) event.getEntity();
         WitherData witherData = WitherData.getWitherData(wither);

         if (witherData != null && !witherData.isPendingLootDrops())
         {
            witherData.setPendingLootDrops(true);

            int lootingLevel = (witherLooting ? event.getLootingLevel() : 0) + (int)Math.round( Math.log10(witherData.getStrength() + 1) * lootingLevelMultiplier);

            if (!ForgeHooks.onLivingDrops(wither, event.getSource(), new ArrayList<>(event.getDrops()), lootingLevel, event.isRecentlyHit()))
            {

               for (ItemEntity item : event.getDrops())
               {
                  wither.level().addFreshEntity(item);
               }

            }

            event.setCanceled(true);
         }

         else if (witherData != null)
         {
            witherData.setPendingLootDrops(false);

            if (netherStarLooting && event.getLootingLevel() > 0)
            {
                int j = 0;
                j += Math.round(Math.abs(AscendedWither.RAND.nextGaussian() * event.getLootingLevel() * netherStarLootingMultiplier));

                if (j > 0)
                {
                   ItemEntity entityItem = new ItemEntity(wither.level(), wither.getX(), wither.getY(), wither.getZ(), new ItemStack(Items.NETHER_STAR, j));
                   entityItem.setPickUpDelay(10);
                   event.getDrops().add(entityItem);
                }
            }
         }
      }
   }

   @SubscribeEvent
   public void onEntityDieing(LivingDeathEvent event)
   {
      if (!event.getEntity().level().isClientSide())
      {
         if (event.getEntity() != null && event.getEntity().getClass() == WitherBoss.class)
         {
            WitherData witherData = WitherData.getWitherData((WitherBoss) event.getEntity());
            if (witherData != null)
            {
               witherData.died();

               java.util.List<Player> nearbyPlayers = event.getEntity().level().getEntitiesOfClass(Player.class, event.getEntity().getBoundingBox().inflate(64.0D, 64.0D, 64.0D));
               for (Player player : nearbyPlayers)
               {
                  final PlayerData data = PlayerData.getPlayerData(player);
                  if (data != null)
                  {
                     data.wasAtWitherSpawn();
                  }
                  player.displayClientMessage(Component.translatable("info." + ModInformation.ID + ".chat.wither-experience"), false);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public void onTooltipEvent(ItemTooltipEvent event)
   {
      int witherAffinityLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.WITHER_AFFINITY.get(), event.getItemStack());
      if (witherAffinityLevel > 0)
      {
         int witherAffinityXp = WitherAffinityHelper.getWitherAffinityXp(event.getItemStack());

         event.getToolTip().add(Component.translatable("tooltip." + ModInformation.ID + ".witherAffinity").append(": " + witherAffinityXp + "/" + WitherAffinityHelper.xpCalc(witherAffinityLevel)));
         event.getToolTip().add(Component.empty());
      }
   }
}
