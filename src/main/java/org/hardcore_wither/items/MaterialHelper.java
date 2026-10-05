package org.hardcore_wither.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.hardcore_wither.config.Config;
import org.hardcore_wither.config.Configurable;

@Configurable
public class MaterialHelper
{
   @Config(minInt = 0)
   private static int witheredEmeraldHarvestLevel = 4;

   @Config(minInt = 0)
   private static int witheredEmeraldMaxUses = 0;

   @Config(minFloat = 0f)
   private static float witheredEmeraldEfficiency = 15.0F;

   @Config(minFloat = 0f)
   private static float witheredEmeraldAttackDamage = 9.5F;

   @Config(minInt = 0)
   private static int witheredEmeraldEnchantability = 42;

   public static final Tier WITHER_EMERALD = new Tier()
   {
      @Override
      public int getUses()
      {
         return witheredEmeraldMaxUses;
      }

      @Override
      public int getLevel()
      {
         return witheredEmeraldHarvestLevel;
      }

      @Override
      public float getSpeed()
      {
         return witheredEmeraldEfficiency;
      }

      @Override
      public float getAttackDamageBonus()
      {
         return witheredEmeraldAttackDamage;
      }

      @Override
      @SuppressWarnings("deprecation")
      public TagKey<Block> getTag()
      {
         return BlockTags.NEEDS_IRON_TOOL;
      }

      @Override
      public int getEnchantmentValue()
      {
         return witheredEmeraldEnchantability;
      }

      @Override
      public Ingredient getRepairIngredient()
      {
         return Ingredient.EMPTY;
      }
   };
}
