package org.hardcore_wither.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.hardcore_wither.HardcoreWither;

public class ItemRegistry
{
   public static final DeferredRegister<Item> ITEMS =
         DeferredRegister.create(ForgeRegistries.ITEMS, HardcoreWither.MODID);

   public static final RegistryObject<Item> STARRY_APPLE   = ITEMS.register("starry_apple", ItemStarryApple::new);
   public static final RegistryObject<Item> STARRY_AXE     = ITEMS.register("starry_axe", ItemStarryAxe::new);
   public static final RegistryObject<Item> STARRY_PICKAXE = ITEMS.register("starry_pickaxe", ItemStarryPickaxe::new);
   public static final RegistryObject<Item> STARRY_SHOVEL  = ITEMS.register("starry_shovel", ItemStarryShovel::new);
   public static final RegistryObject<Item> STARRY_SWORD   = ITEMS.register("starry_sword", ItemStarrySword::new);
   public static final RegistryObject<Item> STARRY_EMERALD = ITEMS.register("starry_emerald", ItemStarryEmerald::new);
   public static final RegistryObject<Item> STARRY_STICK   = ITEMS.register("starry_stick", ItemStarryStick::new);
   public static final RegistryObject<Item> DEATH_STICK    = ITEMS.register("death_stick", ItemDeathStick::new);

   public static void registerItemConfigs()
   {
      HardcoreWither.CONFIG.register(MaterialHelper.class);
      HardcoreWither.CONFIG.register(ItemStarryApple.class);
      HardcoreWither.CONFIG.register(ItemStarryAxe.class);
      HardcoreWither.CONFIG.register(ItemStarryPickaxe.class);
      HardcoreWither.CONFIG.register(ItemStarryShovel.class);
      HardcoreWither.CONFIG.register(ItemStarrySword.class);
      HardcoreWither.CONFIG.register(ItemDeathStick.class);
   }
}
