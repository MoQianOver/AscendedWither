package org.hardcore_wither.wither.powerups;

import org.hardcore_wither.HardcoreWither;
import org.hardcore_wither.api.IPowerUpEffect;
import org.hardcore_wither.config.Config;
import org.hardcore_wither.config.Configurable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;

@Configurable
public class PowerUpDeathKnell extends BasePowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 10;
   private final static int DEFAULT_MIN_LEVEL = 1;

   @Config(minFloat = 0f, maxFloat = 10f)
   private static float knellStrengthMultiplier = 0.6666667f;

   public PowerUpDeathKnell()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      HardcoreWither.CONFIG.register(this);
   }

   @Override
   public void updateWither(IPowerUpEffect data)
   {}

   @Override
   public void witherDied(IPowerUpEffect data)
   {
      final WitherBoss wither = data.getWither();
      wither.level().explode(wither,
            wither.getX(), wither.getY() + wither.getEyeHeight(), wither.getZ(),
            7.0F * knellStrengthMultiplier * data.getStrength(), false, Level.ExplosionInteraction.MOB);
   }

   @Override
   public IPowerUpEffect getPowerUpEffect(WitherBoss wither, int strength)
   {
      final BasePowerUpEffect data = new BasePowerUpEffect(wither, this);
      data.setStrength(strength);
      return data;
   }

   @Override
   public IPowerUpEffect restorePowerUp(WitherBoss wither, CompoundTag stateNbt)
   {
      final BasePowerUpEffect data = new BasePowerUpEffect(wither, this);
      data.deserializeNBT(stateNbt);
      return data;
   }
}
