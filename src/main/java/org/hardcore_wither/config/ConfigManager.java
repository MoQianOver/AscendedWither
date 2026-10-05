package org.hardcore_wither.config;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.hardcore_wither.HardcoreWither;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigManager
{
   private static class Entry
   {
      final Object target;
      final Class<?> clazz;
      final String sectionName;
      final String notificationMethod;

      Entry(Object target, Class<?> clazz, Configurable annotation)
      {
         this.target = target;
         this.clazz = clazz;
         String section = annotation.sectionName();
         if (section.isEmpty())
         {
            section = clazz.getSimpleName();
         }
         this.sectionName = section;
         this.notificationMethod = annotation.syncNotification();
      }
   }

   private record ConfigField(Entry entry, Field field, Config annotation, ForgeConfigSpec.ConfigValue<?> value) {}

   public static class EntryView
   {
      public final String section;
      public final String name;
      public final String comment;
      public final Class<?> type;
      public final ForgeConfigSpec.ConfigValue<?> value;
      private final int minInt;
      private final int maxInt;
      private final float minFloat;
      private final float maxFloat;

      EntryView(String section, String name, String comment, Class<?> type, ForgeConfigSpec.ConfigValue<?> value,
                int minInt, int maxInt, float minFloat, float maxFloat)
      {
         this.section = section;
         this.name = name;
         this.comment = comment;
         this.type = type;
         this.value = value;
         this.minInt = minInt;
         this.maxInt = maxInt;
         this.minFloat = minFloat;
         this.maxFloat = maxFloat;
      }

      public boolean isBoolean()
      {
         return type == boolean.class;
      }

      public boolean isInt()
      {
         return type == int.class;
      }

      public boolean isFloat()
      {
         return type == float.class;
      }

      public String asString()
      {
         Object v = value.get();
         return v == null ? "" : String.valueOf(v);
      }

      public boolean asBoolean()
      {
         Object v = value.get();
         return v instanceof Boolean b && b;
      }

      public boolean apply(String text)
      {
         String trimmed = text.trim();
         try
         {
            if (isInt())
            {
               int parsed = Integer.parseInt(trimmed);
               parsed = Math.max(minInt, Math.min(maxInt, parsed));
               setRaw(parsed);
               return true;
            }
            if (isFloat())
            {
               float parsed = Float.parseFloat(trimmed);
               parsed = Math.max(minFloat, Math.min(maxFloat, parsed));
               setRaw(parsed);
               return true;
            }
            if (type == String.class)
            {
               setRaw(trimmed);
               return true;
            }
         }
         catch (NumberFormatException ignored)
         {
            return false;
         }
         return false;
      }

      public void setBoolean(boolean b)
      {
         setRaw(b);
      }

      @SuppressWarnings("unchecked")
      private void setRaw(Object o)
      {
         ((ForgeConfigSpec.ConfigValue<Object>) value).set(o);
      }

      public String rangeHint()
      {
         if (isInt())
         {
            return minInt + " ~ " + (maxInt == Integer.MAX_VALUE ? "∞" : String.valueOf(maxInt));
         }
         if (isFloat())
         {
            return minFloat + " ~ " + (maxFloat == Float.MAX_VALUE ? "∞" : String.valueOf(maxFloat));
         }
         return "";
      }
   }

   private final List<Entry> entries = new ArrayList<>();
   private final List<ConfigField> configFields = new ArrayList<>();
   private final List<EntryView> views = new ArrayList<>();
   private ForgeConfigSpec spec = null;
   private ModConfig activeConfig = null;

   public void setActiveConfig(ModConfig config)
   {
      this.activeConfig = config;
   }

   public ModConfig getActiveConfig()
   {
      return activeConfig;
   }

   public List<EntryView> getEntryViews()
   {
      return views;
   }

   public void saveActiveConfig()
   {
      if (activeConfig != null)
      {
         activeConfig.save();
      }
   }

   public void register(Object target)
   {
      if (target instanceof Class<?> clazz)
      {
         Configurable annotation = clazz.getAnnotation(Configurable.class);
         if (annotation != null)
         {
            entries.add(new Entry(null, clazz, annotation));
         }
         else
         {
            HardcoreWither.LOGGER.warn("Cannot find @Configurable Annotation on " + clazz.getName());
         }
      }
      else
      {
         Configurable annotation = target.getClass().getAnnotation(Configurable.class);
         if (annotation != null)
         {
            entries.add(new Entry(target, target.getClass(), annotation));
         }
         else
         {
            HardcoreWither.LOGGER.warn("Cannot find @Configurable Annotation on " + target.getClass().getName());
         }
      }
   }

   public void buildAndRegister()
   {
      ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
      Set<String> builtSections = new HashSet<>();
      for (Entry entry : entries)
      {
         if (builtSections.add(entry.sectionName))
         {
            buildSection(entry, builder);
         }
         else
         {
            HardcoreWither.LOGGER.warn("Config section " + entry.sectionName + " registered twice, skipping " + entry.clazz.getName());
         }
      }
      spec = builder.build();
      ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec, HardcoreWither.MODID + "-common.toml");
   }

   public void sync()
   {
      if (spec == null)
      {
         return;
      }
      for (ConfigField configField : configFields)
      {
         Field field = configField.field;
         Object target = configField.entry.target;
         try
         {
            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            Object value = configField.value().get();
            if (field.getType() == boolean.class)
            {
               field.setBoolean(target, (Boolean) value);
            }
            else if (field.getType() == float.class)
            {
               field.setFloat(target, ((Number) value).floatValue());
            }
            else if (field.getType() == int.class)
            {
               field.setInt(target, ((Number) value).intValue());
            }
            else if (field.getType() == String.class)
            {
               field.set(target, value);
            }
            field.setAccessible(isAccessible);
         }
         catch (IllegalAccessException excp)
         {
            HardcoreWither.LOGGER.warn("Hardcore Wither config warning: {}", excp);
            HardcoreWither.LOGGER.warn("Problem setting configurable field \"" + configField.entry.clazz.getName() + "." + field.getName() + "\".");
         }
      }
      for (Entry entry : entries)
      {
         processNotification(entry);
      }
   }

   private void processNotification(Entry entry)
   {
      if (entry.notificationMethod.isEmpty())
      {
         return;
      }
      try
      {

         Method method = null;
         for (Class<?> clazz = entry.clazz; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass())
         {
            try
            {
               method = clazz.getDeclaredMethod(entry.notificationMethod);
               break;
            }
            catch (NoSuchMethodException ignored)
            {

            }
         }
         if (method == null)
         {
            throw new NoSuchMethodException(entry.notificationMethod);
         }
         method.setAccessible(true);
         method.invoke(entry.target);
      }
      catch (Exception excp)
      {
         HardcoreWither.LOGGER.debug("Hardcore Wither config debug: {}", excp);
         HardcoreWither.LOGGER.warn("Invalid Config callback method: " + entry.clazz.getName() + "." + entry.notificationMethod);
      }
   }

   private void buildSection(Entry entry, ForgeConfigSpec.Builder builder)
   {
      Map<String, Field> defined = new HashMap<>();
      builder.push(entry.sectionName);
      List<Class<?>> hierarchy = new ArrayList<>();
      for (Class<?> c = entry.clazz; c != null && c != Object.class; c = c.getSuperclass())
      {
         hierarchy.add(0, c);
      }
      for (Class<?> clazz : hierarchy)
      {
         for (Field field : clazz.getDeclaredFields())
         {
            Config configAnnotation = field.getAnnotation(Config.class);
            if (configAnnotation == null || defined.containsKey(field.getName()))
            {
               continue;
            }
            String fieldName = configAnnotation.fieldName();
            if (fieldName.isEmpty())
            {
               fieldName = field.getName();
            }
            try
            {
               boolean isAccessible = field.isAccessible();
               field.setAccessible(true);
               Object currentValue = field.get(entry.target);
               ForgeConfigSpec.ConfigValue<?> value;
               if (field.getType() == boolean.class)
               {
                  value = builder.define(fieldName, (Boolean) currentValue);
               }
               else if (field.getType() == float.class)
               {
                  value = builder.defineInRange(fieldName, (Float) currentValue, configAnnotation.minFloat(), configAnnotation.maxFloat());
               }
               else if (field.getType() == int.class)
               {
                  value = builder.defineInRange(fieldName, (Integer) currentValue, configAnnotation.minInt(), configAnnotation.maxInt());
               }
               else if (field.getType() == String.class)
               {
                  value = builder.define(fieldName, (String) currentValue);
               }
               else
               {
                  field.setAccessible(isAccessible);
                  continue;
               }
               configFields.add(new ConfigField(entry, field, configAnnotation, value));
               views.add(new EntryView(entry.sectionName, fieldName, configAnnotation.comment(), field.getType(),
                     value, configAnnotation.minInt(), configAnnotation.maxInt(),
                     configAnnotation.minFloat(), configAnnotation.maxFloat()));
               defined.put(field.getName(), field);
               field.setAccessible(isAccessible);
            }
            catch (IllegalAccessException excp)
            {
               HardcoreWither.LOGGER.warn("Hardcore Wither config warning: {}", excp);
               String extraError = entry.target == null ? " Possibly nonstatic field used with static Class Registration" : "";
               HardcoreWither.LOGGER.warn("Problem getting configurable field \"" + clazz.getName() + "." + field.getName() + "\"." + extraError);
            }
         }
      }
      builder.pop();
   }
}
