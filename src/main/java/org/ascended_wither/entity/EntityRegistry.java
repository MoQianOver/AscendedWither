package org.ascended_wither.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.ascended_wither.AscendedWither;

public class EntityRegistry
{
   public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
         DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AscendedWither.MODID);

   public static final RegistryObject<EntityType<EntityBlazeMinion>> BLAZE_MINION =
         ENTITY_TYPES.register("blaze_minion", () ->
            EntityType.Builder.of(EntityBlazeMinion::new, MobCategory.MONSTER)
                  .sized(0.6F, 1.8F)
                  .clientTrackingRange(64)
                  .updateInterval(10)
                  .build("blaze_minion"));

   public static final RegistryObject<EntityType<EntityGhastMinion>> GHAST_MINION =
         ENTITY_TYPES.register("ghast_minion", () ->
            EntityType.Builder.of(EntityGhastMinion::new, MobCategory.MONSTER)
                  .sized(4.0F, 4.0F)
                  .fireImmune()
                  .clientTrackingRange(64)
                  .updateInterval(10)
                  .build("ghast_minion"));

   public static final RegistryObject<EntityType<EntitySkeletonMinion>> SKELETON_MINION =
         ENTITY_TYPES.register("skeleton_minion", () ->
            EntityType.Builder.of(EntitySkeletonMinion::new, MobCategory.MONSTER)
                  .sized(0.7F, 2.4F)
                  .clientTrackingRange(64)
                  .updateInterval(10)
                  .build("skeleton_minion"));

   @Mod.EventBusSubscriber(modid = AscendedWither.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
   public static class AttributeHandler
   {
      @SubscribeEvent
      public static void onAttributes(EntityAttributeCreationEvent event)
      {
         event.put(BLAZE_MINION.get(), Blaze.createAttributes().build());
         event.put(GHAST_MINION.get(), Ghast.createAttributes().build());
         event.put(SKELETON_MINION.get(), AbstractSkeleton.createAttributes().build());
      }
   }
}
