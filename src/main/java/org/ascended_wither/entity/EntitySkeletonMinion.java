package org.ascended_wither.entity;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class EntitySkeletonMinion extends WitherSkeleton
{
   public EntitySkeletonMinion(EntityType<? extends WitherSkeleton> type, Level level)
   {
      super(type, level);
   }

   @Override
   public boolean hurt(DamageSource source, float amount)
   {
      if (source.getEntity() != null && source.getEntity().getClass() == WitherBoss.class)
      {
         return false;
      }
      return super.hurt(source, amount);
   }

   @Override
   protected boolean shouldDropLoot()
   {
      return false;
   }

   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag nbt)
   {

      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
      this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 0.0F;
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
      this.setCanPickUpLoot(false);
      return groupData;
   }
}
