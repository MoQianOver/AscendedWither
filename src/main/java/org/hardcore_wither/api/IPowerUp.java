package org.hardcore_wither.api;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public interface IPowerUp
{

   void updateWither(IPowerUpEffect data);

   void witherDied(IPowerUpEffect data);

   @Nonnull IPowerUpEffect getPowerUpEffect(WitherBoss wither, int strength);

   @Nonnull IPowerUpEffect restorePowerUp(WitherBoss wither, CompoundTag stateNbt);

   boolean canApply(WitherBoss wither);

   int minWitherLevel();

   int maxStrength();

   ResourceLocation getRegistryName();

   IPowerUp setRegistryName(ResourceLocation registryName);

   public static abstract class Impl implements IPowerUp
   {
      private ResourceLocation registryName;

      @Override
      public ResourceLocation getRegistryName()
      {
         return registryName;
      }

      @Override
      public IPowerUp setRegistryName(ResourceLocation registryName)
      {
         this.registryName = registryName;
         return this;
      }
   }
}
