package org.hardcore_wither.items;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.hardcore_wither.HardcoreWither;
import org.hardcore_wither.ModInformation;
import org.hardcore_wither.config.Config;
import org.hardcore_wither.config.Configurable;

import javax.annotation.Nullable;

@Configurable
public class ItemStarrySword extends SwordItem implements IItem
{
   @Config
   private static boolean isEnabled = true;

   public ItemStarrySword()
   {
      super(MaterialHelper.WITHER_EMERALD, 3, -2.4F, new Item.Properties());
      HardcoreWither.API.getWitherAffinityRegistry().register(this);
   }

   @Override
   public boolean isFoil(ItemStack stack)
   {
       return true;
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag)
   {
      tooltip.add(Component.translatable("tooltip." + ModInformation.ID + ".unbreaking").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
   }

   @Override
   public boolean isEnabled()
   {
      return isEnabled;
   }
}
