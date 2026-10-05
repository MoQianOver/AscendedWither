package org.hardcore_wither.potions;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.google.common.collect.Sets;

import org.hardcore_wither.HardcoreWither;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class PotionAntiWither extends MobEffect
{
   public static final String NAME = "anti_wither";

   private static final Map<LivingEntity, Integer> AFFECTED_ENTITIES = new WeakHashMap<>();
   private static final Set<LivingEntity> AFFECTED_WITHERED_ENTITIES = Sets.newSetFromMap(new WeakHashMap<>());

   public PotionAntiWither()
   {
      super(MobEffectCategory.BENEFICIAL, 1310740);
   }

   public static boolean HasEntityBeenWithered(LivingEntity entity)
   {
      return AFFECTED_WITHERED_ENTITIES.contains(entity);
   }

   @Override
   public void addAttributeModifiers(LivingEntity entityLiving, AttributeMap map, int amplifier)
   {
      AFFECTED_ENTITIES.put(entityLiving, -1);
   }

   @Override
   public void removeAttributeModifiers(LivingEntity entityLiving, AttributeMap map, int amplifier)
   {
      AFFECTED_ENTITIES.remove(entityLiving);
      AFFECTED_WITHERED_ENTITIES.remove(entityLiving);
   }

   @Override
   public boolean isDurationEffectTick(int duration, int amplifier)
   {
      return true;
   }

   @Override
   public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier)
   {
      MobEffectInstance witherEffect = entityLivingBaseIn.getEffect(MobEffects.WITHER);
      if (witherEffect != null)
      {
         int lastDuration = AFFECTED_ENTITIES.getOrDefault(entityLivingBaseIn, -1);
         if (witherEffect.getDuration() > lastDuration)
         {
            int newDuration = (witherEffect.getDuration() - lastDuration) / (2 * (amplifier + 1));
            HardcoreWither.LOGGER.debug("Anti-Wither reducing Wither effect to " + newDuration / 20 + " seconds for " + entityLivingBaseIn.getDisplayName().getString());
            entityLivingBaseIn.removeEffect(MobEffects.WITHER);
            entityLivingBaseIn.addEffect(new MobEffectInstance(MobEffects.WITHER, newDuration, witherEffect.getAmplifier()));
            AFFECTED_ENTITIES.put(entityLivingBaseIn, newDuration);
            AFFECTED_WITHERED_ENTITIES.add(entityLivingBaseIn);
         }
         else
         {
            AFFECTED_ENTITIES.put(entityLivingBaseIn, witherEffect.getDuration());
         }
      }
   }
}
