package org.hardcore_wither.wither;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.hardcore_wither.ModInformation;

public class WitherWorldData extends SavedData
{
   private static final int      NBT_FORMAT  = 2;
   private static final String   NAME        = ModInformation.ID + "_wither_data";

   public static WitherWorldData getWitherWorldSavedData(MinecraftServer server)
   {
      return server.overworld().getDataStorage().computeIfAbsent(WitherWorldData::load, WitherWorldData::new, NAME);
   }

   public static WitherWorldData load(CompoundTag nbt)
   {
      WitherWorldData data = new WitherWorldData();
      data.readFromNBT(nbt);
      return data;
   }

   private int largestPowerUp = 0;

   public WitherWorldData()
   {
   }

   @Override
   public CompoundTag save(CompoundTag nbt)
   {
      nbt.putInt("formatVersion", NBT_FORMAT);
      nbt.putInt("largestPowerUp", largestPowerUp);
      return nbt;
   }

   public void readFromNBT(CompoundTag nbt)
   {
      int formatVersion = nbt.getInt("formatVersion");
      if (formatVersion < NBT_FORMAT)
      {
         org.hardcore_wither.HardcoreWither.LOGGER.warn("Detected old version of saved data. Withers' abilities will not convert to ", ModInformation.VERSION, " from older versions");
      }
      largestPowerUp = nbt.getInt("largestPowerUp");
   }

   int getLargestPowerUp()
   {
      return largestPowerUp;
   }

   void setLargestPowerUp(final int size)
   {
      largestPowerUp = size;
      setDirty();
   }
}
