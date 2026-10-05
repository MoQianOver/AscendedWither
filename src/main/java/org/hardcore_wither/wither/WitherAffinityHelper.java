package org.hardcore_wither.wither;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.hardcore_wither.ModInformation;
import org.hardcore_wither.config.Config;
import org.hardcore_wither.config.Configurable;

@Configurable(sectionName = "WitherAffinity")
class WitherAffinityHelper
{
   private static final String NBT_WITHER_AFFINITY = "witherAffinity";

   @Config
   private static boolean isEnabled = true;

   @Config(minInt = 1)
   private static int levelingBase = 300;

   @Config(minFloat = 1f)
   private static float levelingMultiplier = 2.5f;

   @Config(comment = "is s, the enchant level is l, and the chance is c: where s/(-1*l-s) = c")
   private static float powerUpRemovalChanceScale = 12f;

   static int getWitherAffinityXp(ItemStack stack)
   {
      final CompoundTag tag = stack.getOrCreateTagElement(ModInformation.ID);
      return tag.getInt(NBT_WITHER_AFFINITY);
   }

   static void setWitherAffinityXp(ItemStack stack, int xp)
   {
      final CompoundTag tag = stack.getOrCreateTagElement(ModInformation.ID);
      tag.putInt(NBT_WITHER_AFFINITY, xp);
   }

   static float chanceCalc(int currentLevel)
   {
      return powerUpRemovalChanceScale / (-1 * currentLevel - powerUpRemovalChanceScale);
   }

   static int xpCalc(int currentLevel)
   {
      return currentLevel * (levelingBase + (int) ((levelingBase - 1) * levelingMultiplier));
   }

   static boolean shouldWitherAffinityLevelUp(ItemStack stack, int currentLevel)
   {
      return getWitherAffinityXp(stack) >= xpCalc(currentLevel);
   }
}
