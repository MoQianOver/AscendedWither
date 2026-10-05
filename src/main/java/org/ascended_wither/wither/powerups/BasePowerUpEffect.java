package org.ascended_wither.wither.powerups;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.ascended_wither.api.IPowerUp;
import org.ascended_wither.api.IPowerUpEffect;

public class BasePowerUpEffect implements IPowerUpEffect
{
   protected final IPowerUp powerUp;
   protected final WitherBoss wither;
   protected int strength;

   public BasePowerUpEffect(WitherBoss wither, IPowerUp powerUp)
   {
      this.wither = wither;
      this.powerUp = powerUp;
   }

   @Override
   public void setStrength(int strength)
   {
      this.strength = strength;
   }

   @Override
   public int getStrength()
   {
      return strength;
   }

   @Override
   public void updateEffect()
   {
      powerUp.updateWither(this);
   }

   @Override
   public void onDied()
   {
      powerUp.witherDied(this);
   }

   @Override
   public CompoundTag serializeNBT()
   {
      CompoundTag tag = new CompoundTag();
      tag.putString("powerUpId", powerUp.getRegistryName().toString());
      tag.putInt("strength", strength);
      return tag;
   }

   @Override
   public void deserializeNBT(CompoundTag nbt)
   {
      setStrength(nbt.getInt("strength"));
   }

   @Override
   public IPowerUp getCreator()
   {
      return powerUp;
   }

   @Override
   public WitherBoss getWither()
   {
      return wither;
   }
}
