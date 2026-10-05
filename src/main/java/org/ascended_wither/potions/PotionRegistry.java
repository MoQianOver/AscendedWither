package org.ascended_wither.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.ascended_wither.AscendedWither;

public class PotionRegistry
{
   public static final DeferredRegister<MobEffect> POTIONS =
         DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AscendedWither.MODID);

   public static final RegistryObject<MobEffect> POTION_ANTI_WITHER =
         POTIONS.register(PotionAntiWither.NAME, PotionAntiWither::new);
}
