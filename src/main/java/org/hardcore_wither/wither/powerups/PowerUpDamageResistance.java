package org.hardcore_wither.wither.powerups;

import org.hardcore_wither.HardcoreWither;
import org.hardcore_wither.api.IPowerUpEffect;
import org.hardcore_wither.config.Configurable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.boss.wither.WitherBoss;

@Configurable
public class PowerUpDamageResistance extends BasePowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 3;
   private final static int DEFAULT_MIN_LEVEL = 1;

   static protected void setResistancePotionEffect(WitherBoss wither, int strength)
   {
      final MobEffectInstance resistanceEffect = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, strength, false, false, true);
      if (!wither.level().isClientSide())
      {
         wither.removeEffect(MobEffects.DAMAGE_RESISTANCE);
         wither.addEffect(resistanceEffect);
      }
   }

   private class Data extends BasePowerUpEffect
   {
      public Data(WitherBoss wither)
      {
         super(wither, PowerUpDamageResistance.this);
      }

      @Override
      public void setStrength(int strength)
      {
         super.setStrength(strength);
         setResistancePotionEffect(wither, strength);
      }
   }

   public PowerUpDamageResistance()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      HardcoreWither.CONFIG.register(this);
   }

   @Override
   public void updateWither(IPowerUpEffect data)
   {}

   @Override
   public void witherDied(IPowerUpEffect data)
   {}

   @Override
   public IPowerUpEffect getPowerUpEffect(WitherBoss wither, int strength)
   {
      final BasePowerUpEffect data = new Data(wither);
      data.setStrength(strength);
      return data;
   }

   @Override
   public IPowerUpEffect restorePowerUp(WitherBoss wither, CompoundTag stateNbt)
   {
      final BasePowerUpEffect data = new Data(wither);
      data.deserializeNBT(stateNbt);
      return data;
   }
}
