package org.ascended_wither.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.ascended_wither.AscendedWither;
import org.ascended_wither.ModInformation;
import org.ascended_wither.wither.PowerUpHelper;

@Mod.EventBusSubscriber(modid = AscendedWither.MODID)
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
