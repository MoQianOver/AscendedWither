package org.ascended_wither.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.ascended_wither.ModInformation;
import org.jetbrains.annotations.Nullable;

@AutoRegisterCapability
public class PlayerData
{
   static final ResourceLocation NBT_TAG  =  ResourceLocation.fromNamespaceAndPath(ModInformation.ID, "player_data");

   private static final String NBT_PLAYER_XP = "playerXp";

   public static final Capability<PlayerData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

   static ICapabilitySerializable<CompoundTag> getPlayerDataProvider()
   {
      return new ICapabilitySerializable<CompoundTag>()
      {
         final PlayerData cap = new PlayerData();

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

   public static PlayerData getPlayerData(Player player)
   {
      return player.getCapability(CAPABILITY).resolve().orElse(null);
   }

   double playerXp;

   public double wasAtWitherSpawn()
   {
      return addWitherExperience(1.0);
   }

   private double addWitherExperience(double wxp)
   {
      playerXp += wxp;
      return playerXp;
   }

   public CompoundTag serializeNBT()
   {
      CompoundTag nbt = new CompoundTag();
      nbt.putDouble(NBT_PLAYER_XP, playerXp);
      return nbt;
   }

   public void deserializeNBT(CompoundTag nbt)
   {
      playerXp = nbt.getDouble(NBT_PLAYER_XP);
   }

   void clone(PlayerData source)
   {
      playerXp = source.playerXp;
   }
}
