package org.ascended_wither.api;

import org.ascended_wither.api.exceptions.AscendedWitherException;

public class AscendedWitherApiInternal
{
   private static boolean initialized = false;

   public static void setApi(IAscendedWitherApi api) throws AscendedWitherException
   {
      if (!initialized)
      {
         AscendedWither.instance = api;
         initialized = true;
      }
      else
      {
         throw new AscendedWitherException("Improper API Initialization");
      }
   }
}
