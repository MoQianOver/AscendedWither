package org.ascended_wither.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.ascended_wither.AscendedWither;

public class EnchantmentRegistry
{
   public static final DeferredRegister<Enchantment> ENCHANTMENTS =
         DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AscendedWither.MODID);

   public static final RegistryObject<Enchantment> WITHER_AFFINITY =
         ENCHANTMENTS.register(EnchantmentWitherAffinity.NAME, EnchantmentWitherAffinity::new);
}
