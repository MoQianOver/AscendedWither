package org.ascended_wither.wither.powerups;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.ascended_wither.AscendedWither;
import org.ascended_wither.api.IPowerUpEffect;
import org.ascended_wither.config.Config;
import org.ascended_wither.config.Configurable;

@Configurable
public class PowerUpHealthBoost extends BasePowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 128;
   private final static int DEFAULT_MIN_LEVEL = 1;

   @Config(minFloat = 1f, maxFloat = 10f)
   private static float healthBoostMultiplier = 1.1f;

   static protected void updateWitherHealth(HealthBoostPowerUpEffect effect)
   {
      double newHealth = effect.initialHealth * healthBoostMultiplier * effect.strength;
      AttributeInstance maxHealth = effect.wither.getAttribute(Attributes.MAX_HEALTH);
      if (maxHealth != null)
      {
         maxHealth.setBaseValue(newHealth);
      }
   }

   private class HealthBoostPowerUpEffect extends BasePowerUpEffect
   {

      final double initialHealth;

      public HealthBoostPowerUpEffect(WitherBoss wither)
      {
         super(wither, PowerUpHealthBoost.this);
         initialHealth = wither.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
      }

      @Override
      public void setStrength(int strength)
      {
         super.setStrength(strength);
         updateWitherHealth(this);
      }
   }

   public PowerUpHealthBoost()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      AscendedWither.CONFIG.register(this);
   }

   @Override
   public void updateWither(IPowerUpEffect data)
   {
      if (!data.getWither().level().isClientSide())
      {
         if (data.getWither().getInvulnerableTicks() >= 20)
         {
            AttributeInstance maxHealth = data.getWither().getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null)
            {
               float baseHealth = (float) maxHealth.getBaseValue();

               data.getWither().heal((((baseHealth * (2f / 3f)) - 200) / 200));
            }
         }
      }
   }

   @Override
   public void witherDied(IPowerUpEffect data)
   {}

   @Override
   public IPowerUpEffect getPowerUpEffect(WitherBoss wither, int strength)
   {
      final BasePowerUpEffect data = new HealthBoostPowerUpEffect(wither);
      data.setStrength(strength);
      return data;
   }

   @Override
   public IPowerUpEffect restorePowerUp(WitherBoss wither, CompoundTag stateNbt)
   {
      final BasePowerUpEffect data = new HealthBoostPowerUpEffect(wither);
      data.deserializeNBT(stateNbt);
      return data;
   }
}
