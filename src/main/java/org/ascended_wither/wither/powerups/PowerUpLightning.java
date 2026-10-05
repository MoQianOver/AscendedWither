package org.ascended_wither.wither.powerups;

import org.ascended_wither.AscendedWither;
import org.ascended_wither.api.IPowerUpEffect;
import org.ascended_wither.config.Config;
import org.ascended_wither.config.Configurable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.boss.wither.WitherBoss;

@Configurable
public class PowerUpLightning extends BasePowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 1;

   @Config(minFloat = 1f, maxFloat = 5f)
   private static float lightningFrequencyMultiplier  = 1.1f;

   @Config(minFloat = 1f, maxFloat = 5f, comment = "0 is not random, 1 is more random")
   private static float lightningRandomness = 0.5f;

   @Config(minFloat = 1f, maxFloat = 5f, comment = "Avg number of ticks between lightning")
   private static int   lightningFequencyBase         = 100;

   @Config(minFloat = 0f, maxFloat = 5f, comment = "0 is prefect")
   private static float lightningInaccuracy = 0.5f;

   protected class Data extends BasePowerUpEffect
   {
      long nextTick;

      Data(WitherBoss wither)
      {
         super(wither, PowerUpLightning.this);
      }

      public long getNextTick()
      {
         return nextTick;
      }

      void setNextRandomTick()
      {
         final long currentTick = wither.level().getGameTime();
         final long strengthBasedTick = (long) (lightningFequencyBase / (strength * lightningFrequencyMultiplier));
         final long modifier = (long) ((AscendedWither.RAND.nextGaussian() * lightningRandomness) * strengthBasedTick);
         nextTick =  currentTick + strengthBasedTick + modifier;
      }

      @Override
      public CompoundTag serializeNBT()
      {
         CompoundTag nbt = super.serializeNBT();
         nbt.putLong("nextTick", nextTick);
         return nbt;
      }

      @Override
      public void deserializeNBT(CompoundTag nbt)
      {
         super.deserializeNBT(nbt);
         nextTick = nbt.getLong("nextTick");
      }
   }

   public PowerUpLightning()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      AscendedWither.CONFIG.register(this);
   }

   @Override
   public void updateWither(IPowerUpEffect effect)
   {
      Data lightningData = (Data) effect;
      if (lightningData.getWither().level().getGameTime() > lightningData.nextTick)
      {
         int targetId = lightningData.getWither().getAlternativeTarget(0);
         if (targetId != -1)
         {
            Entity target = lightningData.getWither().level().getEntity(targetId);
            if (target != null)
            {
               double lightningXPos = target.xOld + (8 * AscendedWither.RAND.nextGaussian() * lightningInaccuracy);
               double lightningYPos = target.yOld + (8 * AscendedWither.RAND.nextGaussian() * lightningInaccuracy);
               double lightningZPos = target.zOld + (8 * AscendedWither.RAND.nextGaussian() * lightningInaccuracy);
               LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, lightningData.getWither().level());
               lightning.moveTo(lightningXPos, lightningYPos, lightningZPos);
               lightningData.getWither().level().addFreshEntity(lightning);
               lightningData.setNextRandomTick();
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
      final Data data = new Data(wither);
      data.setStrength(strength);
      data.setNextRandomTick();
      return data;
   }

   @Override
   public IPowerUpEffect restorePowerUp(WitherBoss wither, CompoundTag stateNbt)
   {
      final Data data = new Data(wither);
      data.deserializeNBT(stateNbt);
      return data;
   }
}
