package org.hardcore_wither.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.hardcore_wither.HardcoreWither;

public class EnchantmentRegistry
{
   public static final DeferredRegister<Enchantment> ENCHANTMENTS =
         DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, HardcoreWither.MODID);

   public static final RegistryObject<Enchantment> WITHER_AFFINITY =
         ENCHANTMENTS.register(EnchantmentWitherAffinity.NAME, EnchantmentWitherAffinity::new);
}
