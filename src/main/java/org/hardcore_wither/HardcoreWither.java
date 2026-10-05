package org.hardcore_wither;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.hardcore_wither.api.HardcoreWitherApiInternal;
import org.hardcore_wither.api.IHardcoreWitherApi;
import org.hardcore_wither.api.exceptions.HardcoreWitherException;
import org.hardcore_wither.client.ClientSetup;
import org.hardcore_wither.config.ConfigManager;
import org.hardcore_wither.core.HardcoreWitherApi;
import org.hardcore_wither.core.PowerUpRegistry;
import org.hardcore_wither.enchantment.EnchantmentRegistry;
import org.hardcore_wither.entity.EntityRegistry;
import org.hardcore_wither.items.IItem;
import org.hardcore_wither.items.ItemRegistry;
import org.hardcore_wither.player.PlayerHandler;
import org.hardcore_wither.potions.PotionRegistry;
import org.hardcore_wither.wither.WitherHandler;
import org.slf4j.Logger;

@Mod(HardcoreWither.MODID)
public class HardcoreWither
{
   public static final String MODID = ModInformation.ID;

   public static final Logger LOGGER = LogUtils.getLogger();
   public static final java.util.Random RAND = new java.util.Random();

   public static final PowerUpRegistry POWER_UP_REGISTRY = new PowerUpRegistry();

   public static final IHardcoreWitherApi API = new HardcoreWitherApi(POWER_UP_REGISTRY);

   public static final ConfigManager CONFIG = new ConfigManager();

   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
         DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

   public static final RegistryObject<CreativeModeTab> CREATIVE_TAB =
         CREATIVE_MODE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
               .title(Component.translatable("itemGroup." + MODID + ".creative_tab"))
               .icon(() -> new ItemStack(Items.WITHER_SKELETON_SKULL))
               .displayItems((parameters, output) ->
               {
                  acceptIfEnabled(output, ItemRegistry.STARRY_APPLE.get());
                  acceptIfEnabled(output, ItemRegistry.STARRY_SWORD.get());
                  acceptIfEnabled(output, ItemRegistry.STARRY_AXE.get());
                  acceptIfEnabled(output, ItemRegistry.STARRY_PICKAXE.get());
                  acceptIfEnabled(output, ItemRegistry.STARRY_SHOVEL.get());
                  acceptIfEnabled(output, ItemRegistry.STARRY_EMERALD.get());
                  acceptIfEnabled(output, ItemRegistry.STARRY_STICK.get());
                  acceptIfEnabled(output, ItemRegistry.DEATH_STICK.get());
               })
               .build());

   private static void acceptIfEnabled(CreativeModeTab.Output output, Item item)
   {
      if (item instanceof IItem iItem && iItem.isEnabled())
      {
         output.accept(item);
      }
   }

   public HardcoreWither()
   {
      try
      {
         HardcoreWitherApiInternal.setApi(API);
      }
      catch (HardcoreWitherException e)
      {
         LOGGER.error("Hardcore Wither: {}", e);
         LOGGER.error("Alight, here's the deal, I'm just minding my own business trying to Construct, but apprently I can't?!");
      }

      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

      EntityRegistry.ENTITY_TYPES.register(modEventBus);
      PotionRegistry.POTIONS.register(modEventBus);
      EnchantmentRegistry.ENCHANTMENTS.register(modEventBus);
      ItemRegistry.ITEMS.register(modEventBus);
      CREATIVE_MODE_TABS.register(modEventBus);

      ItemRegistry.registerItemConfigs();

      new WitherHandler();

      new PlayerHandler();

      CONFIG.buildAndRegister();

      MinecraftForge.EVENT_BUS.register(this);

      if (FMLEnvironment.dist.isClient())
      {
         ClientSetup.registerConfigScreen();
      }
   }
}
