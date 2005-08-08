/*
 * Title: MyanmarParser
 * Description: Syllable based Myanmar Parser
 * Copyright:   Copyright (c) 2005 http://www.thanlwinsoft.org
 *
 * @author Keith Stribley
 * @version 0.1
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 */

package org.thanlwinsoft.myanmar;

public class MyanmarNumber
{
  public static int string2Number(String numString) throws NumberFormatException
  {
    int i = numString.length() - 1;
    int number = 0;
    int factor = 1;
    while (i >= 0)
    {
        int digit = (Character.codePointAt(numString,i) - 4160);
        if (digit < 0 || digit > 9) 
          throw new NumberFormatException("Invalid number " +
             numString.substring(i,i+1));
        number += factor * digit;
        factor *= 10;
        i--;
    }
    return number;
  }
  public static String number2String(int number)
  {
    StringBuffer myNum = new StringBuffer();
    int unconverted = number;
    int factor = 10;
    int rem = 0;
    while (unconverted != 0)
    {
      rem = unconverted % factor;
      myNum.insert(0,Character.toChars(rem + 4160));
      unconverted = (unconverted - rem) / 10;
    }
    return myNum.toString();
  }
}  