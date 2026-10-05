package org.ascended_wither.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.ascended_wither.AscendedWither;
import org.ascended_wither.config.ConfigManager;

public class AscendedWitherConfigScreen extends Screen
{
   private static final int ROW_HEIGHT = 24;
   private static final int LIST_TOP = 34;
   private static final int LIST_BOTTOM_MARGIN = 64;

   private static class Row
   {
      final ConfigManager.EntryView view;
      final String headerText;
      Button toggle;
      EditBox box;
      int y;

      Row(ConfigManager.EntryView view, String headerText)
      {
         this.view = view;
         this.headerText = headerText;
      }
   }

   private final Screen parent;
   private final List<Row> rows = new ArrayList<>();
   private double scroll = 0.0D;
   private int listBottom;

   public AscendedWitherConfigScreen(Screen parent)
   {
      super(Component.translatable("gui." + AscendedWither.MODID + ".config.title"));
      this.parent = parent;

      String lastSection = null;
      for (ConfigManager.EntryView view : AscendedWither.CONFIG.getEntryViews())
      {
         if (!view.section.equals(lastSection))
         {
            rows.add(new Row(null, view.section));
            lastSection = view.section;
         }
         rows.add(new Row(view, null));
      }
   }

   @Override
   protected void init()
   {
      listBottom = this.height - LIST_BOTTOM_MARGIN;
      int widgetWidth;
      for (Row row : rows)
      {
         if (row.view == null || row.view.type == String.class)
         {
            widgetWidth = row.view == null ? 0 : 180;
         }
         else if (row.view.isBoolean())
         {
            widgetWidth = 60;
         }
         else
         {
            widgetWidth = 90;
         }
         int x = this.width - 24 - widgetWidth;
         if (row.view == null)
         {
            continue;
         }
         if (row.view.isBoolean())
         {
            row.toggle = Button.builder(boolLabel(row.view.asBoolean()), b ->
            {
               boolean next = !row.view.asBoolean();
               row.view.setBoolean(next);
               b.setMessage(boolLabel(next));
            }).bounds(x, 0, widgetWidth, 18).build();
            addRenderableWidget(row.toggle);
         }
         else
         {
            row.box = new EditBox(this.font, x, 0, widgetWidth, 18, Component.literal(row.view.name));
            row.box.setValue(row.view.asString());
            row.box.setMaxLength(120);
            addRenderableWidget(row.box);
         }
      }
      addRenderableWidget(Button.builder(Component.translatable("gui." + AscendedWither.MODID + ".config.save"), b -> saveAndClose())
            .bounds(this.width / 2 - 154, this.height - 32, 150, 20).build());
      addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), b -> onClose())
            .bounds(this.width / 2 + 4, this.height - 32, 150, 20).build());
      layoutWidgets();
   }

   private Component boolLabel(boolean value)
   {
      return Component.translatable(value
            ? "gui." + AscendedWither.MODID + ".config.enabled"
            : "gui." + AscendedWither.MODID + ".config.disabled");
   }

   private int contentHeight()
   {
      return rows.size() * ROW_HEIGHT;
   }

   private void layoutWidgets()
   {
      int y = LIST_TOP - (int) scroll;
      for (Row row : rows)
      {
         row.y = y;
         if (row.toggle != null)
         {
            row.toggle.setY(y + 2);
            row.toggle.visible = y >= LIST_TOP - ROW_HEIGHT && y <= listBottom;
         }
         if (row.box != null)
         {
            row.box.setY(y + 2);
            row.box.visible = y >= LIST_TOP - ROW_HEIGHT && y <= listBottom;
         }
         y += ROW_HEIGHT;
      }
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
   {
      this.renderBackground(graphics);
      graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);

      ConfigManager config = AscendedWither.CONFIG;
      String path = config.getActiveConfig() != null ? config.getActiveConfig().getFullPath().toString() : "";
      graphics.drawCenteredString(this.font, Component.literal(path), this.width / 2, this.height - 48, 0x707070);

      graphics.enableScissor(0, LIST_TOP, this.width, listBottom);
      ConfigManager.EntryView hovered = null;
      for (Row row : rows)
      {
         if (row.view == null)
         {
            if (row.y >= LIST_TOP - ROW_HEIGHT && row.y <= listBottom)
            {
               graphics.fill(12, row.y + 2, this.width - 12, row.y + ROW_HEIGHT - 2, 0x40000000);
               graphics.drawString(this.font, sectionLabel(row.headerText), 20, row.y + 8, 0xFFD479, false);
            }
            continue;
         }
         if (row.y < LIST_TOP - ROW_HEIGHT || row.y > listBottom)
         {
            continue;
         }
         boolean hovering = mouseX >= 20 && mouseX <= this.width - 24 && mouseY >= row.y && mouseY <= row.y + ROW_HEIGHT;
         int color = hovering ? 0xFFFF80 : 0xE0E0E0;
         graphics.drawString(this.font, fieldLabel(row.view.name), 20, row.y + 8, color, false);
         if (!row.view.rangeHint().isEmpty())
         {
            graphics.drawString(this.font, Component.literal(row.view.rangeHint()), this.width - 200, row.y + 8, 0x808080, false);
         }
         if (hovering)
         {
            hovered = row.view;
         }
      }
      graphics.disableScissor();

      super.render(graphics, mouseX, mouseY, partialTick);

      if (hovered != null)
      {
         List<Component> tooltip = new ArrayList<>();
         tooltip.add(Component.literal(hovered.section + "." + hovered.name));
         tooltip.add(fieldLabel(hovered.name));
         if (!hovered.comment.isEmpty())
         {
            tooltip.add(Component.translatableWithFallback("config." + AscendedWither.MODID + ".comment." + hovered.name, hovered.comment));
         }
         if (!hovered.rangeHint().isEmpty())
         {
            tooltip.add(Component.translatable("gui." + AscendedWither.MODID + ".config.range", hovered.rangeHint()));
         }
         graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
      }
   }

   private static Component sectionLabel(String section)
   {
      return Component.translatableWithFallback("config." + AscendedWither.MODID + ".section." + section, section);
   }

   private static Component fieldLabel(String field)
   {
      return Component.translatableWithFallback("config." + AscendedWither.MODID + ".field." + field, field);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double amount)
   {
      if (mouseY >= LIST_TOP && mouseY <= listBottom)
      {
         double max = Math.max(0, contentHeight() - (listBottom - LIST_TOP));
         scroll = Math.max(0, Math.min(max, scroll - amount * ROW_HEIGHT));
         layoutWidgets();
         return true;
      }
      return super.mouseScrolled(mouseX, mouseY, amount);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button)
   {

      if (mouseY < LIST_TOP || mouseY > listBottom)
      {
         for (Row row : rows)
         {
            if (row.box != null && row.box.isFocused())
            {
               row.box.setFocused(false);
            }
         }
      }
      return super.mouseClicked(mouseX, mouseY, button);
   }

   private void saveAndClose()
   {
      boolean ok = true;
      for (Row row : rows)
      {
         if (row.view == null || row.box == null)
         {
            continue;
         }
         boolean applied = row.view.apply(row.box.getValue());
         row.box.setTextColor(applied ? 0xE0E0E0 : 0xFF5555);
         ok &= applied;
      }
      if (!ok)
      {
         return;
      }
      AscendedWither.CONFIG.saveActiveConfig();
      AscendedWither.CONFIG.sync();
      onClose();
   }

   @Override
   public void onClose()
   {
      if (this.minecraft != null)
      {
         this.minecraft.setScreen(parent);
      }
   }

   @Override
   public boolean isPauseScreen()
   {
      return false;
   }
}
