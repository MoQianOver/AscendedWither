package org.ascended_wither.wither.powerups;

import org.ascended_wither.AscendedWither;
import org.ascended_wither.api.IPowerUpEffect;
import org.ascended_wither.config.Config;
import org.ascended_wither.config.Configurable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@Configurable
public class PowerUpTeleport extends BasePowerUp
{
   private final static int DEFAULT_MAX_STRENGTH = 20;
   private final static int DEFAULT_MIN_LEVEL = 4;

   @Config(minFloat = 1f, maxFloat = 10f)
   private static float teleportFrequencyMultiplier = 1.1f;

   @Config(minFloat = 0f, maxFloat = 10f, comment = "0 is not random, 1 is more random")
   private static float teleportRandomness = 0.5f;

   @Config(minInt = 20, comment = "Avg number of ticks between teleport attempt")
   private static int   teleportFequencyBase = 100;

   @Config(minInt = 0, maxInt = 10, comment = "Number of ticks in a row to retry a teleport attempt")
   private static int   teleportRetryTicks = 5;

   @Config(minFloat = 0f, maxFloat = 10f, comment = "0 is prefect")
   private static float teleportInaccuracy = 4f;

   @Config(comment="Less likely that the Wither may get stuck, but may affect performance")
   private static boolean requireLineOfSightToTarget = false;

   protected class Data extends BasePowerUpEffect
   {
      long nextTick;
      int retryNumber = 0;

      Data(WitherBoss wither)
      {
         super(wither, PowerUpTeleport.this);
      }

      void setNextRandomTick()
      {
         setNextRandomTick(true);
      }

      void setNextRandomTick(boolean successfulAttempt)
      {
         if (successfulAttempt || retryNumber >= teleportRetryTicks)
         {
            retryNumber = 0;
            final long strengthBasedTick = (long) (teleportFequencyBase / (strength * teleportFrequencyMultiplier));
            final long modifier = (long) ((AscendedWither.RAND.nextGaussian() * teleportRandomness) * strengthBasedTick);
            nextTick = wither.level().getGameTime() + strengthBasedTick + modifier;
         }
         else if (retryNumber < teleportRetryTicks)
         {
            retryNumber++;
            nextTick = wither.level().getGameTime() + 1;
         }
      }

      @Override
      public CompoundTag serializeNBT()
      {
         CompoundTag nbt = super.serializeNBT();
         nbt.putLong("nextTick", nextTick);
         nbt.putInt("retryNumber", retryNumber);
         return nbt;
      }

      @Override
      public void deserializeNBT(CompoundTag nbt)
      {
         super.deserializeNBT(nbt);
         nextTick = nbt.getLong("nextTick");
         retryNumber = nbt.getInt("retryNumber");
      }
   }

   public PowerUpTeleport()
   {
      super(DEFAULT_MIN_LEVEL, DEFAULT_MAX_STRENGTH);
      AscendedWither.CONFIG.register(this);
   }

   @Override
   public void updateWither(IPowerUpEffect data)
   {
      final Data stateData = (Data) data;
      if (stateData.wither.getInvulnerableTicks() <= 0 && stateData.wither.level().getGameTime() > stateData.nextTick)
      {
         final int targetId = stateData.wither.getAlternativeTarget(0);
         if (targetId != -1)
         {
            Entity target = stateData.wither.level().getEntity(targetId);
            if (target != null)
            {
               final int meanDistance = stateData.wither.distanceToSqr(target) >= 256D ? 16 : 8;
               final double teleportXPos = target.getX() + (meanDistance * (AscendedWither.RAND.nextBoolean() ? 1 : -1)) + (teleportInaccuracy * ((AscendedWither.RAND.nextFloat() * 2f) - 1));
               double teleportYPos = target.getY() + (meanDistance * (AscendedWither.RAND.nextBoolean() ? 1 : -1)) + (teleportInaccuracy * ((AscendedWither.RAND.nextFloat() * 2f) - 1));
               final double teleportZPos = target.getZ() + (meanDistance * (AscendedWither.RAND.nextBoolean() ? 1 : -1)) + (teleportInaccuracy * ((AscendedWither.RAND.nextFloat() * 2f) - 1));
               if (teleportYPos < stateData.wither.level().getMinBuildHeight())
               {
                  teleportYPos = stateData.wither.level().getMinBuildHeight();
               }

               boolean successfulTeleport = true;
               if (requireLineOfSightToTarget && stateData.getWither().getTarget() != null)
               {
                  final Vec3 vecWither = new Vec3(stateData.getWither().getX(), stateData.getWither().getY() + stateData.getWither().getEyeHeight(), stateData.getWither().getZ());
                  final Vec3 vecTarget = new Vec3(stateData.getWither().getTarget().getX(), stateData.getWither().getTarget().getY() + stateData.getWither().getTarget().getEyeHeight(), stateData.getWither().getTarget().getZ());
                  final HitResult clip = stateData.getWither().level().clip(new ClipContext(vecWither, vecTarget, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, stateData.getWither()));
                  successfulTeleport = clip.getType() == HitResult.Type.MISS;
               }
               if (successfulTeleport)
               {
                  successfulTeleport = stateData.wither.randomTeleport(teleportXPos, teleportYPos, teleportZPos, false);
                  if (successfulTeleport)
                  {
                     stateData.getWither().level().playSound(null, stateData.getWither().xo, stateData.getWither().yo, stateData.getWither().zo, SoundEvents.ENDERMAN_TELEPORT, stateData.getWither().getSoundSource(), 1.0F, 1.0F);
                     stateData.getWither().playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                  }
               }
               stateData.setNextRandomTick(successfulTeleport);
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
