package org.hardcore_wither.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.hardcore_wither.HardcoreWither;
import org.hardcore_wither.ModInformation;
import org.hardcore_wither.wither.PowerUpHelper;

@Mod.EventBusSubscriber(modid = HardcoreWither.MODID)
public class CommandManager
{
   @SubscribeEvent
   public static void onRegisterCommands(RegisterCommandsEvent event)
   {
      CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
      LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(ModInformation.CHANNEL)
            .requires(source -> source.hasPermission(2))
            .then(PowerUpHelper.createSpawnCommand());
      dispatcher.register(root);

      dispatcher.register(Commands.literal(ModInformation.ID).requires(source -> source.hasPermission(2))
            .then(PowerUpHelper.createSpawnCommand()));
   }

   public static Component usage()
   {
      return Component.translatable("commands." + ModInformation.CHANNEL + ".usage");
   }
}
