package org.ascended_wither.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.ascended_wither.AscendedWither;

public class ItemRegistry
{
   public static final DeferredRegister<Item> ITEMS =
         DeferredRegister.create(ForgeRegistries.ITEMS, AscendedWither.MODID);

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
      AscendedWither.CONFIG.register(MaterialHelper.class);
      AscendedWither.CONFIG.register(ItemStarryApple.class);
      AscendedWither.CONFIG.register(ItemStarryAxe.class);
      AscendedWither.CONFIG.register(ItemStarryPickaxe.class);
      AscendedWither.CONFIG.register(ItemStarryShovel.class);
      AscendedWither.CONFIG.register(ItemStarrySword.class);
      AscendedWither.CONFIG.register(ItemDeathStick.class);
   }
}
