package org.ascended_wither.wither;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.ascended_wither.AscendedWither;
import org.ascended_wither.ModInformation;
import org.ascended_wither.api.IPowerUp;
import org.ascended_wither.api.IPowerUpEffect;
import org.jetbrains.annotations.Nullable;

@AutoRegisterCapability
public class WitherData implements INBTSerializable<CompoundTag>
{
   static final ResourceLocation NBT_TAG  =  ResourceLocation.fromNamespaceAndPath(ModInformation.ID, "wither_data");

   private static final String NBT_EFFECT_NAME_TAG = "name";
   private static final String NBT_EFFECT_TAG      = "effect";

   public static final Capability<WitherData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

   private final Set<IPowerUpEffect>   activePowerUps       =  new HashSet<>();
   private boolean                     hasDied              =  false;
   private boolean                     isPendingLootDrops   =  false;
   private boolean                     isPoweredUp          =  false;
   private final WitherBoss                ownerWither;

   static ICapabilitySerializable<CompoundTag> getWitherDataProvider(WitherBoss wither)
   {
      return new ICapabilitySerializable<CompoundTag>()
      {
         final WitherData cap = new WitherData(wither);

         @Override
         public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable net.minecraft.core.Direction facing)
         {
            return CAPABILITY.orEmpty(capability, LazyOptional.of(() -> cap));
         }

         @Override
         public CompoundTag serializeNBT()
         {
            return cap.serializeNBT();
         }

         @Override
         public void deserializeNBT(CompoundTag nbt)
         {
            cap.deserializeNBT(nbt);
         }
      };
   }

   public static WitherData getWitherData(WitherBoss wither)
   {
      return wither.getCapability(CAPABILITY).resolve().orElse(null);
   }

   WitherData(@Nullable WitherBoss wither)
   {
      ownerWither = wither;
   }

   int getActivePowerUpEffectsNum()
   {
      return activePowerUps.size();
   }

   IPowerUpEffect getRandomActivePowerUpEffect()
   {
      return (IPowerUpEffect)activePowerUps.toArray()[AscendedWither.RAND.nextInt(activePowerUps.size())];
   }

   IPowerUpEffect getActivePowerUpEffect(IPowerUp powerUp)
   {
      for (IPowerUpEffect effect : activePowerUps)
      {
         if (effect.getCreator() == powerUp)
         {
            return effect;
         }
      }
      return null;
   }

   boolean addPowerUpEffect(IPowerUp powerUp, int powerUpStrength)
   {
      if (getActivePowerUpEffect(powerUp) == null)
      {
         IPowerUpEffect powerUpEffect =  powerUp.getPowerUpEffect(ownerWither, powerUpStrength);
         if (powerUpEffect != null)
         {
            activePowerUps.add(powerUpEffect);
            return true;
         }
      }
      return false;
   }

   boolean removePowerUpEffect(IPowerUpEffect powerUpEffect)
   {
      return activePowerUps.remove(powerUpEffect);
   }

   void removeAllPowerUpEffects()
   {
      activePowerUps.clear();
   }

   boolean isPendingLootDrops()
   {
      return isPendingLootDrops;
   }

   void setPendingLootDrops(boolean isPendingLootDrops)
   {
      this.isPendingLootDrops = isPendingLootDrops;
   }

   int getStrength()
   {
      final AtomicInteger strength  = new AtomicInteger(0);
      activePowerUps.forEach(effect -> strength.getAndAdd(effect.getStrength()));
      return strength.get();
   }

   public boolean isPoweredUp()
   {
      return isPoweredUp;
   }

   public void setPoweredUp(boolean isPoweredUp)
   {
      this.isPoweredUp = isPoweredUp;
   }

   public WitherBoss getWither()
   {
      return ownerWither;
   }

   void update()
   {
      for (IPowerUpEffect powerUp : activePowerUps)
      {
         try
         {
            powerUp.updateEffect();
         }

         catch (Exception e)
         {
            AscendedWither.LOGGER.error("Ascended Wither: {}", e);
            AscendedWither.LOGGER.error("Error Occured while updating PowerUp " + powerUp.getCreator().getRegistryName() + ", removing.");
            activePowerUps.remove(powerUp);

            break;
         }
      }
   }

   void died()
   {
      for (IPowerUpEffect powerUp : activePowerUps)
      {
         powerUp.onDied();
      }
   }

   @Override
   public CompoundTag serializeNBT()
   {
      CompoundTag witherDataTag = new CompoundTag();

      ListTag powerUpEffectsTagList = new ListTag();

      for (IPowerUpEffect effect : activePowerUps)
      {
         CompoundTag effectTag = new CompoundTag();
         effectTag.putString(NBT_EFFECT_NAME_TAG, effect.getCreator().getRegistryName().toString());
         effectTag.put(NBT_EFFECT_TAG, effect.serializeNBT());
         powerUpEffectsTagList.add(effectTag);
      }
      witherDataTag.put("powerUpEffectsList", powerUpEffectsTagList);
      witherDataTag.putBoolean("isPoweredUp", isPoweredUp);
      witherDataTag.putBoolean("hasDied", hasDied);
      witherDataTag.putBoolean("isPendingLootDrops", isPendingLootDrops);
      return witherDataTag;
   }

   @Override
   public void deserializeNBT(CompoundTag compound)
   {
      ListTag powerUpEffectsTagList = compound.getList("powerUpEffectsList", Tag.TAG_COMPOUND);
      isPoweredUp = compound.getBoolean("isPoweredUp");
      hasDied = compound.getBoolean("hasDied");
      isPendingLootDrops = compound.getBoolean("isPendingLootDrops");
      for (int i = 0; i < powerUpEffectsTagList.size(); ++i)
      {
         try
         {
            CompoundTag powerUpEffectTag = powerUpEffectsTagList.getCompound(i);
            IPowerUp powerUp = AscendedWither.API.getPowerUpRegistry().getValue(ResourceLocation.parse(powerUpEffectTag.getString(NBT_EFFECT_NAME_TAG)));
            IPowerUpEffect effect = powerUp.restorePowerUp(ownerWither, powerUpEffectTag.getCompound(NBT_EFFECT_TAG));
            activePowerUps.add(effect);
         }
         catch (NullPointerException e)
         {
            AscendedWither.LOGGER.warn("Ascended Wither: {}", e);
            AscendedWither.LOGGER.warn("Problem loading " + powerUpEffectsTagList.getCompound(i) + " NBT for Wither, " + (ownerWither != null ? ownerWither.getUUID() : "unknown"));
         }
      }
   }
}
