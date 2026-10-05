package org.hardcore_wither.wither.powerups;

import java.util.function.Supplier;

import org.hardcore_wither.api.IPowerUpEffect;
import org.hardcore_wither.config.Config;
import org.hardcore_wither.config.Configurable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;

@Configurable
abstract class AbstractPowerUpMinionSpawner extends BasePowerUp
{
   protected class Data extends BasePowerUpEffect
   {
      final BaseSpawner spawner = new BaseSpawner()
      {
         @Override
         public void broadcastEvent(Level level, BlockPos pos, int id)
         {}
      };

      Data(WitherBoss wither)
      {
         super(wither, AbstractPowerUpMinionSpawner.this);
      }

      protected void resetSpawnerToData()
      {
         CompoundTag nbt = new CompoundTag();
         nbt.putShort("Delay", (short)Math.round(defaultDelay + spawnDelayModifier * strength));
         nbt.putShort("RequiredPlayerRange", (short)defaultPlayerRange);
         nbt.putShort("MaxNearbyEntities", (short)Math.round(defaultMaxEntities + maxEntitiesModifier * strength));
         nbt.putShort("MinSpawnDelay", (short)Math.round(defaultMinDelay + spawnDelayModifier * strength));
         nbt.putShort("MaxSpawnDelay", (short)Math.round(defaultMaxDelay + spawnDelayModifier * strength));
         nbt.putShort("SpawnCount", (short)Math.round(defaultSpawnCount + spawnCountModifier * strength));
         nbt.putShort("SpawnRange", (short)defaultSpawnRange);
         spawner.load(wither.level(), wither.blockPosition(), nbt);
         spawner.setEntityId(entityType.get(), wither.level(), wither.level().random, wither.blockPosition());
      }

      @Override
      public CompoundTag serializeNBT()
      {
         CompoundTag nbt = super.serializeNBT();
         CompoundTag spawnerNbt = new CompoundTag();
         spawner.save(spawnerNbt);
         nbt.put("spawnerLogic", spawnerNbt);
         return nbt;
      }

      @Override
      public void deserializeNBT(CompoundTag nbt)
      {
         CompoundTag spawnerNbt = nbt.getCompound("spawnerLogic");
         super.deserializeNBT(nbt);
         spawner.load(wither.level(), wither.blockPosition(), spawnerNbt);
      }

      @Override
      public void setStrength(int strength)
      {
         super.setStrength(strength);
         resetSpawnerToData();
      }
   }

   final protected Supplier<EntityType<?>> entityType;

   @Config(maxInt = 65535)
   protected int defaultDelay = 20;

   @Config(maxInt = 65535)
   protected int defaultPlayerRange = 48;

   @Config(maxInt = 65535)
   protected int defaultMaxEntities = 6;

   @Config(maxInt = 65535)
   protected int defaultMinDelay = 600;

   @Config(maxInt = 65535)
   protected int defaultMaxDelay = 800;

   @Config(maxInt = 65535)
   protected int defaultSpawnCount = 4;

   @Config(maxInt = 65535)
   protected int defaultSpawnRange = 4;

   @Config(minFloat = 1f, maxFloat = 10f, comment = "Amount to increase Spawn Count by. 1.0 to never increase")
   static protected float spawnCountModifier = 1.1f;

   @Config(minFloat = 0f, maxFloat = 1f, comment = "The smaller it is, the faster the delay decrease. 1.0 to never decrease")
   static protected float spawnDelayModifier = 0.8f;

   @Config(minFloat = 1f, maxFloat = 10f, comment = "Amount to increase Max Entities by. 1.0 to never increase")
   static protected float maxEntitiesModifier = 1.1f;

   public AbstractPowerUpMinionSpawner(int minLevel, int maxStrength, Supplier<EntityType<?>> entityType)
   {
      super(minLevel, maxStrength);
      this.entityType = entityType;
   }

   @Override
   public void updateWither(IPowerUpEffect data)
   {
      Data spawnerData = (Data) data;
      if (spawnerData.wither.level() instanceof ServerLevel serverLevel)
      {
         spawnerData.spawner.serverTick(serverLevel, spawnerData.wither.blockPosition());
      }
   }

   @Override
   public void witherDied(IPowerUpEffect data)
   {}

   @Override
   public IPowerUpEffect getPowerUpEffect(WitherBoss wither, int strength)
   {
      Data data = new Data(wither);
      data.setStrength(strength);
      data.resetSpawnerToData();
      return data;
   }

   @Override
   public IPowerUpEffect restorePowerUp(WitherBoss wither, CompoundTag stateNbt)
   {
      Data data = new Data(wither);
      data.deserializeNBT(stateNbt);
      return data;
   }
}
