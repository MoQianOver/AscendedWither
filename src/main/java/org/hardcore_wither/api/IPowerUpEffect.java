package org.hardcore_wither.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPowerUpEffect extends INBTSerializable<CompoundTag>
{
   int getStrength();

   void setStrength(int strength);

   void updateEffect();

   void onDied();

   IPowerUp getCreator();

   WitherBoss getWither();
}
