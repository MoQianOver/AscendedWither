package org.hardcore_wither.api;

import org.hardcore_wither.api.exceptions.HardcoreWitherException;

public class HardcoreWitherApiInternal
{
   private static boolean initialized = false;

   public static void setApi(IHardcoreWitherApi api) throws HardcoreWitherException
   {
      if (!initialized)
      {
         HardcoreWither.instance = api;
         initialized = true;
      }
      else
      {
         throw new HardcoreWitherException("Improper API Initialization");
      }
   }
}
