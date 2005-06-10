package myanmar;

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