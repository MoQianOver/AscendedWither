package org.ascended_wither.wither;

import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.ascended_wither.AscendedWither;
import org.ascended_wither.ModInformation;
import org.ascended_wither.api.IPowerUp;
import org.ascended_wither.api.IPowerUpEffect;
import org.ascended_wither.wither.powerups.PowerUpBlazeMinionSpawner;
import org.ascended_wither.wither.powerups.PowerUpDamageResistance;
import org.ascended_wither.wither.powerups.PowerUpDeathKnell;
import org.ascended_wither.wither.powerups.PowerUpGhastMinionSpawner;
import org.ascended_wither.wither.powerups.PowerUpHealthBoost;
import org.ascended_wither.wither.powerups.PowerUpLightning;
import org.ascended_wither.wither.powerups.PowerUpSkeletonMinionSpawner;
import org.ascended_wither.wither.powerups.PowerUpTeleport;

public class PowerUpHelper
{
   public static void initialize()
   {
      AscendedWither.API.getPowerUpRegistry().register(new PowerUpBlazeMinionSpawner());
      AscendedWither.API.getPowerUpRegistry().register(new PowerUpDeathKnell());
      AscendedWither.API.getPowerUpRegistry().register(new PowerUpGhastMinionSpawner());
      AscendedWither.API.getPowerUpRegistry().register(new PowerUpHealthBoost());
      AscendedWither.API.getPowerUpRegistry().register(new PowerUpSkeletonMinionSpawner());
      AscendedWither.API.getPowerUpRegistry().register(new PowerUpLightning());
      AscendedWither.API.getPowerUpRegistry().register(new PowerUpTeleport());
      AscendedWither.API.getPowerUpRegistry().register(new PowerUpDamageResistance());
   }

   static final SuggestionProvider<CommandSourceStack> POWER_UP_SUGGESTIONS = (context, builder) ->
   {
      List<String> powerUps = new ArrayList<>();
      for (ResourceLocation powerUp : AscendedWither.API.getPowerUpRegistry().getKeys())
      {
         powerUps.add(powerUp.toString());
      }
      return SharedSuggestionProvider.suggest(powerUps, builder);
   };

   public static LiteralArgumentBuilder<CommandSourceStack> createSpawnCommand()
   {
      return Commands.literal("spawn")
            .then(Commands.argument("powerups", StringArgumentType.greedyString())
                  .suggests(POWER_UP_SUGGESTIONS)
                  .executes(context -> spawnPoweredWither(context.getSource(), StringArgumentType.getString(context, "powerups"))));
   }

   private static int spawnPoweredWither(CommandSourceStack source, String args) throws com.mojang.brigadier.exceptions.CommandSyntaxException
   {
      ServerLevel level = source.getLevel();
      WitherBoss spawnedWither = EntityType.WITHER.create(level);
      if (spawnedWither == null)
      {
         source.sendFailure(Component.translatable("commands." + ModInformation.CHANNEL + ".spawn.failed"));
         return 0;
      }
      BlockPos coords = BlockPos.containing(source.getPosition());
      spawnedWither.moveTo(coords.getX(), coords.getY(), coords.getZ(), spawnedWither.getYRot(), spawnedWither.getXRot());

      spawnedWither.setInvulnerableTicks(220);
      spawnedWither.setHealth(spawnedWither.getMaxHealth() / 3.0F);
      level.addFreshEntity(spawnedWither);
      try
      {
         WitherData witherData = WitherData.getWitherData(spawnedWither);
         if (witherData != null)
         {
            witherData.removeAllPowerUpEffects();
            for (String arg : args.split(" "))
            {
               if (arg.isBlank())
               {
                  continue;
               }
               String[] splitArg = arg.split(",");
               String powerUpName = splitArg[0];
               int powerUpStrength = splitArg.length > 1 ? Integer.parseInt(splitArg[1]) : 1;
               IPowerUp powerUp = resolvePowerUp(powerUpName);
               if (powerUp == null)
               {
                  AscendedWither.LOGGER.debug("PowerUp: " + powerUpName + " not valid");
                  throw new com.mojang.brigadier.exceptions.SimpleCommandExceptionType(
                        Component.translatable("commands." + ModInformation.CHANNEL + ".spawn.unknown", powerUpName)).create();
               }
               witherData.addPowerUpEffect(powerUp, powerUpStrength);
            }
         }
      }
      catch (com.mojang.brigadier.exceptions.CommandSyntaxException syntax)
      {

         spawnedWither.discard();
         throw syntax;
      }
      catch (Exception excp)
      {
         spawnedWither.discard();

         AscendedWither.LOGGER.debug("PowerUp Command Formatting Error (probably) not accounted for " + excp);
         source.sendFailure(Component.translatable("commands." + ModInformation.CHANNEL + ".usage"));
         return 0;
      }
      source.sendSuccess(() -> Component.translatable("commands." + ModInformation.CHANNEL + ".spawn.success", spawnedWither.getDisplayName()), true);
      return 1;
   }

   private static IPowerUp resolvePowerUp(String name)
   {
      ResourceLocation id = ResourceLocation.tryParse(name);
      if (id == null)
      {
         return null;
      }
      IPowerUp powerUp = AscendedWither.API.getPowerUpRegistry().getValue(id);
      if (powerUp == null && id.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE))
      {
         powerUp = AscendedWither.API.getPowerUpRegistry()
               .getValue(ResourceLocation.fromNamespaceAndPath(ModInformation.ID, id.getPath()));
      }
      return powerUp;
   }

   static void powerUpWither(WitherData witherData, int sizeOfPowerUp)
   {
      if (witherData.getActivePowerUpEffectsNum() == 0)
      {
         int usedStrength = 0;
         List<IPowerUp> validPowerUps = new ArrayList<>(AscendedWither.API.getPowerUpRegistry().getValues());
         validPowerUps.removeIf(powerUp -> powerUp.minWitherLevel() > sizeOfPowerUp);

         validPowerUps.removeIf(powerUp -> !powerUp.canApply(witherData.getWither()));
         while (usedStrength < sizeOfPowerUp && validPowerUps.size() > 0)
         {
            int powerUpIndex = AscendedWither.RAND.nextInt(validPowerUps.size());
            IPowerUp powerUp = validPowerUps.get(powerUpIndex);
            IPowerUpEffect powerUpEffect = witherData.getActivePowerUpEffect(powerUp);
            if (powerUpEffect != null)
            {
               int currentStength = powerUpEffect.getStrength();
               if (currentStength < powerUp.maxStrength())
               {
                  powerUpEffect.setStrength(currentStength + 1);
                  ++usedStrength;
                  AscendedWither.LOGGER.debug("Increasing power of " + powerUp.getClass());
               }
               else
               {
                  validPowerUps.remove(powerUpIndex);
               }
            }

            else
            {
               witherData.addPowerUpEffect(powerUp, 1);
               usedStrength += powerUp.minWitherLevel() > 0 ? powerUp.minWitherLevel() : 1;
               AscendedWither.LOGGER.debug("Adding " + powerUp.getRegistryName());
            }
         }
      }
      else
      {
         AscendedWither.LOGGER.debug("Attempting to re-powerup Wither");
      }
      witherData.setPoweredUp(true);
   }

   static public void reduceWitherPowerUp(WitherData witherData)
   {
      IPowerUpEffect powerUpEffect = witherData.getRandomActivePowerUpEffect();

      final int currentStrength = powerUpEffect.getStrength();
      if (currentStrength == 1)
      {
         witherData.removePowerUpEffect(powerUpEffect);
      }
      else
      {
         powerUpEffect.setStrength(currentStrength - 1);
      }
   }
}
