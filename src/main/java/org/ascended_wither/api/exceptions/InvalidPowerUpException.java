package org.ascended_wither.api.exceptions;

@Deprecated
public class InvalidPowerUpException extends AscendedWitherException
{
   private static final long serialVersionUID = 1L;

   public InvalidPowerUpException(String name)
   {
      super("Id(" + name + ")not for a valid PowerUp");
   }
}
