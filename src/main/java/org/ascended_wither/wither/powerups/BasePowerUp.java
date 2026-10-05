package org.ascended_wither.wither.powerups;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.ascended_wither.ModInformation;
import org.ascended_wither.api.IPowerUp;
import org.ascended_wither.config.Config;
import org.ascended_wither.config.Configurable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.wither.WitherBoss;

@Configurable (syncNotification = "configChangeNotification")
abstract class BasePowerUp extends IPowerUp.Impl
{
   @Config
   protected boolean powerUpEnabled = true;

   @Config(minInt = 2, comment = "The Maximum Strength this Power Up can reach")
   private int maxStrength = 20;

   @Config(minInt = 1, comment = "The Minimum Wither Level for which this Power Up is available")
   private int minLevel = 1;

   @Config(comment = "Dimension ids where this Power Up will not be used, e.g. \"minecraft:the_nether,minecraft:the_end\"")
   private String blacklistDims = "";

   private Set<String> blacklistDimsSet = new HashSet<>();

   protected BasePowerUp(int minLevel, int maxStrength)
   {
      this.maxStrength = maxStrength;
      this.minLevel = minLevel;
      setRegistryName(ResourceLocation.fromNamespaceAndPath(ModInformation.ID, getClass().getSimpleName().toLowerCase(Locale.ROOT)));
   }

   @Override
   public boolean canApply(WitherBoss theOwnerWither)
   {
      return powerUpEnabled &&
             ( (blacklistDimsSet != null) &&
               !blacklistDimsSet.contains(theOwnerWither.level().dimension().toString()));
   }

   @SuppressWarnings("unused")
   private void configChangeNotification()
   {
      blacklistDimsSet = blacklistDims.isEmpty() ? new HashSet<>() :
            new HashSet<>(Arrays.asList(blacklistDims.toLowerCase(Locale.ROOT).split(",")));
   }

   @Override
   public int maxStrength()
   {
      return maxStrength;
   }

   @Override
   public int minWitherLevel()
   {
      return powerUpEnabled ? minLevel : Integer.MAX_VALUE;
   }
}
