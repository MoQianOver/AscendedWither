package org.ascended_wither.items;

import org.ascended_wither.AscendedWither;
import org.ascended_wither.config.Config;
import org.ascended_wither.config.Configurable;
import org.ascended_wither.potions.PotionRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Configurable
public class ItemStarryApple extends Item implements IItem
{
   @Config(minInt = 0)
   private static int antiWitherDurationTicks = 2400;

   @Config(minInt = 0)
   private static int absorptionDurationTicks = 1200;

   @Config(minInt = 0)
   private static int regenDurationTicks = 900;

   @Config(minInt = 0)
   private static int resistanceDurationTicks = 900;

   @Config(minInt = 0)
   private static int strengthDurationTicks = 900;

   @Config(minInt = 0)
   private static int antiWitherLevel = 1;

   @Config(minInt = 0)
   private static int absorptionLevel = 1;

   @Config(minInt = 0)
   private static int regenLevel = 2;

   @Config(minInt = 0)
   private static int resistanceLevel = 2;

   @Config(minInt = 0)
   private static int strengthLevel = 2;

   @Config(minInt = 0)
   private static int instaHealLevel = 1;

   @Config
   private static boolean enableStarryApple = true;

   public ItemStarryApple()
   {
      super(new Item.Properties().food(new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(1.2F)
            .alwaysEat()
            .build()));
   }

   @Override
   public boolean isFoil(ItemStack stack)
   {
       return true;
   }

   @Override
   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity player)
   {
       if (!world.isClientSide())
       {
          player.addEffect(new MobEffectInstance(PotionRegistry.POTION_ANTI_WITHER.get(), antiWitherDurationTicks, antiWitherLevel - 1));
          player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, absorptionDurationTicks, absorptionLevel - 1));
          player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenDurationTicks, regenLevel - 1));
          player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, resistanceDurationTicks, resistanceLevel - 1));
          player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, strengthDurationTicks, strengthLevel - 1));
          player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 0, instaHealLevel - 1));
       }
       return super.finishUsingItem(stack, world, player);
   }

   @Override
   public boolean isEnabled()
   {
      return enableStarryApple;
   }
}
