package org.ascended_wither.api.exceptions;

public class InvalidApiUsageException extends AscendedWitherException
{
   private static final long serialVersionUID = 1L;

   public InvalidApiUsageException(String string)
   {
      super("Invalid API Usage: " + string);
   }
}
