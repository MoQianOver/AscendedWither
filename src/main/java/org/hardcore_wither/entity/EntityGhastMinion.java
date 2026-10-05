package org.hardcore_wither.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;

public class EntityGhastMinion extends Ghast
{
   public EntityGhastMinion(EntityType<? extends Ghast> type, Level level)
   {
      super(type, level);
   }

   @Override
   public MobType getMobType()
   {
       return MobType.UNDEAD;
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
}
