package org.hardcore_wither.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.hardcore_wither.HardcoreWither;
import org.hardcore_wither.ModInformation;
import org.hardcore_wither.config.Config;
import org.hardcore_wither.config.Configurable;

import javax.annotation.Nullable;
import java.util.List;

@Configurable
public class ItemDeathStick extends Item implements IItem
{
   @Config(comment="Death Stick will kill anything it hits. Creative ONLY")
   private static boolean enableDeathStick = true;

   public ItemDeathStick()
   {
      super(new Item.Properties());
      MinecraftForge.EVENT_BUS.register(this);
   }

   @Override
   public boolean isFoil(ItemStack stack)
   {
      if (net.minecraftforge.fml.loading.FMLEnvironment.dist.isClient())
      {
         try
         {
            return net.minecraft.client.Minecraft.getInstance().player != null
                  && net.minecraft.client.Minecraft.getInstance().player.isCreative();
         }
         catch (Throwable t)
         {
            return true;
         }
      }
      return true;
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag)
   {
      tooltip.add(Component.translatable("tooltip." + ModInformation.ID + ".creative").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
   }

   @Override
   public boolean isEnabled()
   {
      return enableDeathStick;
   }

   @Override
   public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
   {
      if (!player.level().isClientSide() && player.isCreative() && stack.getItem() == this)
      {
         entity.discard();
         return true;
      }
      return super.onLeftClickEntity(stack, player, entity);
   }
}
