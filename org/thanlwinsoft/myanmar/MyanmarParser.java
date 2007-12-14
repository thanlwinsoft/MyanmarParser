/*
 * Title: MyanmarParser
 * Description: Syllable based Myanmar Parser
 * Copyright:   Copyright (c) 2005 http://www.thanlwinsoft.org
 *
 * $LastChangedBy: keith $
 * $LastChangedDate: 2006-10-23 13:37:55 +0100 (Mon, 23 Oct 2006) $
 * $LastChangedRevision: 668 $
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

import java.util.ArrayList;

/**
* A class to parse Myanamar Text for line break points. 
* It can also be used to check for erroneous code sequences of
* for Tokenizing, since space based tokenizing gives bad results
* with Myanmar.
* It implements the algorithm described at:
* http://www.thanlwinsoft.org/ThanLwinSoft/MyanmarUnicode/Parsing/
*/
public class MyanmarParser
{
// Myanmar Constants
  public final static int MAX_CONTEXT_LENGTH = 3;
// character classes
  protected final static int MMC_UNKNOWN = 0;
  protected final static int MMC_CI = 1;
  protected final static int MMC_ME = 2;
  protected final static int MMC_VI = 3;
  protected final static int MMC_EV = 4;
  protected final static int MMC_UV = 5;
  protected final static int MMC_LV = 6;
  protected final static int MMC_AV = 7;
  protected final static int MMC_AN = 8;
  protected final static int MMC_KI = 9;
  protected final static int MMC_LD = 10;
  protected final static int MMC_VG = 11;
  protected final static int MMC_MD = 12;
  protected final static int MMC_SE = 13;
  protected final static int MMC_VS = 14;
  protected final static int MMC_PL = 15;
  protected final static int MMC_PV = 16;
  protected final static int MMC_SP = 17;
  protected final static int MMC_LQ = 18;
  protected final static int MMC_RQ = 19;
  //protected final static int MMC_NJ = 18;
  protected final static int MMC_WJ = 20;
  protected final static int MMC_OT = 21;
// break weights from intitial table approach
  public final static int BK_NO_BREAK = 0;
  public final static int BK_WEIGHT_1 = 1;
  public final static int BK_WEIGHT_2 = 2;
  public final static int BK_CONTEXT = 3; // used internally only
  public final static int BK_UNEXPECTED = 4; // illegal sequence
  public final static int BK_SYLLABLE = 5; // syllable break, no line break
  public final static int BK_WHITESPACE = 6; // white space character
  public final static int BK_EOL = 7; // end of line or string
  public final static int BK_STACK_SYLLABLE = 8; // within a stacked combination
  // usually you want to line break after the last whitespace character, though
  // you don't count the whitespace in the line width
  public final static int LANG_MY = 0; // Myanmar
  public final static int LANG_KSW = 1; // S'Gaw Karen
  /**
   * Finds the next syllable in the string starting at a given offset.
   * The caller must check the return value to know whether a break is 
   * allowed there or not.
   * @param text text to search
   * @param offset index to start search
   * @return ClusterProperties
   */
  public ClusterProperties getNextSyllable(String text, int offset)
  {
    int breakType = BK_NO_BREAK;
    int i = offset;
    boolean foundCluster = false;
    if (offset >= text.length()) return null;
    int langGuess = guessLanguage(text.toCharArray());
    while (i + 1 < text.length())
    {
      int breakStatus = getBreakStatus(text.charAt(i),text.charAt(i+1));
      /*
      System.out.println("bs U+" + 
                         Integer.toHexString(text.charAt(i)) + ",U+" +
                         Integer.toHexString(text.charAt(i+1)) + 
                         " =" + breakStatus + " " +
                         text.charAt(i) + "|" + text.charAt(i+1));
                         */
      switch (breakStatus)
      {
        case BK_NO_BREAK:
        case BK_STACK_SYLLABLE:
          break;
        case BK_SYLLABLE:
        case BK_WEIGHT_1:
        case BK_WEIGHT_2:
        case BK_UNEXPECTED:
        case BK_WHITESPACE:
          breakType = breakStatus;
          foundCluster = true;
          break;
        case BK_CONTEXT:
          breakType = evaluateContext(text, i, langGuess);
          if (breakType != BK_NO_BREAK)
            foundCluster = true;
          break;
        default: // shouldn't happen unless there is an error in this class
          System.err.println("Unexpected status" + breakStatus);
      }
      if (foundCluster) break;
      i++;
    }
    if (i + 1 == text.length()) breakType = BK_EOL;
    return new ClusterProperties(offset, i + 1, breakType);
  }

/**
   * Finds the next syllable in the string starting at a given offset.
   * The caller must check the return value to know whether a break is 
   * allowed there or not.
   * @param text text to search
   * @param offset index to start search
   * @return ClusterProperties
   */
  public ClusterProperties getNextSyllable(char [] text, int offset)
  {
    int breakType = BK_NO_BREAK;
    int i = offset;
    boolean foundCluster = false;
    if (offset >= text.length) return null;
    int langGuess = guessLanguage(text);
    while (i + 1 < text.length)
    {
      int breakStatus = getBreakStatus(text[i],text[i+1]);
      switch (breakStatus)
      {
        case BK_NO_BREAK:
        case BK_STACK_SYLLABLE:
          break;
        case BK_SYLLABLE:
        case BK_WEIGHT_1:
        case BK_WEIGHT_2:
        case BK_UNEXPECTED:
        case BK_WHITESPACE:
          breakType = breakStatus;
          foundCluster = true;
          break;
        case BK_CONTEXT:
          breakType = evaluateContext(new String(text), i, langGuess);
          if (breakType != BK_NO_BREAK)
            foundCluster = true;
          break;
        default: // shouldn't happen unless there is an error in this class
          System.err.println("Unexpected status" + breakStatus);
      }
      if (foundCluster) break;
      i++;
    }
    if (i + 1 == text.length) breakType = BK_EOL;
    return new ClusterProperties(offset, i + 1, breakType);
  }
    
  public int guessLanguage(char [] text)
  {
    int language = LANG_MY;
    char prevChar = ' ';
    for (int i = 0; i < text.length && language == LANG_MY; i++)
    {
        // we could look for specific sequences that are Karen specific as well
        switch (text[i])
        {
        case 0x1060:
        case 0x1061:
        case 0x1062:
        case 0x1063:
        case 0x1064:
            language = LANG_KSW;
            break;
        case 0x102C:
            if (prevChar == 0x1036 || prevChar == 0x1037)
            {
                language = LANG_KSW;
                break;
            }
        default:
            prevChar = text[i];
        }
    }
    return language;
  }
    
  /**
   * Finds the next line break point in the string starting at a given offset.
   * The caller must check the return value to know whether a break is 
   * allowed there or not.
   * @param text text to search
   * @param offset index to start search
   * @return ClusterProperties
   */
  public ClusterProperties getNextLineBreak(String text, int offset)
  {
    int i = offset;
    ClusterProperties cp = null;
    if (offset >= text.length()) return null;
    do
    {
      cp = getNextSyllable(text, i);
      i = cp.getEnd();
    } while (!isLineBreak(cp.getBreakStatus()) && i < text.length());
    return new ClusterProperties(offset, cp.getEnd(), cp.getBreakStatus());
  }
  
  /**
   * Checks for incorrect sequences of Myanmar characters
   * @param text text to search
   * @param offset index to start search
   * @return array of erroneous clusters
   */
  public boolean isValidMyanmar(String text, int offset)
  {
    int i = offset;
    ClusterProperties cp = null;
    boolean valid = true;
    if (offset >= text.length()) return valid;
    do 
    {
      cp = getNextSyllable(text, i);
      i = cp.getEnd() + 1;
      if (cp.getBreakStatus() == BK_UNEXPECTED)
      {  
        valid = false;
        break;
      }
    } while (i < text.length());
    return valid;
  }
  
  /**
   * Checks for incorrect sequences of Myanmar characters
   * @param text text to search
   * @param offset index to start search
   * @return array of erroneous clusters
   */
  public ClusterProperties [] checkMyanmar(String text, int offset)
  {
    int i = offset;
    ClusterProperties cp = null;
    
    // if you are using Java version < 1.5 change to the commented out
    // lines
    //ArrayList errorClusters = null;
    ArrayList <ClusterProperties> errorClusters = null;
    boolean valid = true;
    if (offset >= text.length()) return null;
    boolean prevError = false;
    do 
    {
      cp = getNextSyllable(text, i);
      i = cp.getEnd() + 1;
      if (cp.getBreakStatus() == BK_UNEXPECTED || prevError)
      {  
        valid = false;
        if (errorClusters == null)
        {
          errorClusters = new ArrayList<ClusterProperties>(1);
          //errorClusters = new ArrayList(1);
        }
        errorClusters.add(cp);
        //break;
        if (cp.getBreakStatus() != BK_UNEXPECTED) 
          prevError = false;
        else prevError = true;
      }
    } while (i < text.length());
    if (valid) return null;
    return errorClusters.toArray(new ClusterProperties[errorClusters.size()]);
    //return (ClusterProperties[])errorClusters.toArray();
  }
  
  /** A simple method to determine whether the break status allows a line
  * break. If the breakStatus is weight 1 or 2 a line break will be allowed.
  * @param breakStatus
  * @return true if a line break is permissible
  */
  public boolean isLineBreak(int breakStatus)
  {
    boolean lineBreak = false;
    if (breakStatus == BK_WEIGHT_1 || breakStatus == BK_WEIGHT_2)
      lineBreak = true;
    return lineBreak;
  }                     
  /**
  * evaluates the context where a simple pair approach is not enough
  * @param contextText
  * @param offset
  * @return break status of specified offset in text
  */                      
  protected int evaluateContext(String contextText, int offset, int langHint)
  {
    char [] text;
    if (contextText.length() >= offset + 4) 
      text = contextText.substring(offset, offset + 4).toCharArray();
    else
    {
      text = new char[4];
      for (int i = 0; i<4; i++)
      {
        if (offset + i < contextText.length())
          text[i] = contextText.charAt(offset + i);
        else text[i] = ' ';
      }
    }
    // deal with easy cases first
    if (text[0] == 0x1021) return BK_NO_BREAK;
    
    if (text[1] == 0x002d) return BK_NO_BREAK;
    if (text[1] == 0x103F) return BK_NO_BREAK;
    
    if (text[2] == 0x1039)
    {
      return BK_NO_BREAK;
    }
    else if (text[2] == 0x103A && langHint == LANG_MY)
    {
      // Karen (and also some load words in Myanmar) can have a starting 103A
      return BK_NO_BREAK;
    }
    else
    {
      return BK_WEIGHT_2;
    }
  }
  /**
  * gets the break status of a pair of characters
  * @param before
  * @param after
  * @return break status code
  */
  protected static int getBreakStatus(char before, char after)
  {
    // BK_NO_BREAK = 0; BK_WEIGHT_1 = 1; BK_WEIGHT_2 = 2; BK_CONTEXT = 3; 
    // BK_UNEXPECTED = 4; BK_SYLLABLE = 5; BK_WHITESPACE = 6; BK_EOL = 7;
    final int [][] BKSTATUS = 
      {
        // ci me vi ev uv lv av an ki ld vg md se vs pl pv sp lq rq wj ot 
    /*ci*/{ 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 5, 5, 2, 4, 1, 2, 5, 0, 1 },
    /*me*/{ 3, 0, 4, 0, 0, 0, 0, 0, 4, 0, 0, 1, 5, 5, 2, 4, 1, 2, 5, 0, 1 },
    /*vi*/{ 0, 4, 0, 4, 0, 4, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 1, 2, 5, 0, 1 },
    /*ev*/{ 3, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 1, 5, 5, 2, 4, 1, 2, 5, 0, 1 },
    /*uv*/{ 3, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 1, 5, 5, 2, 4, 1, 2, 5, 0, 1 },
    /*lv*/{ 3, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 1, 5, 5, 2, 4, 1, 2, 5, 0, 1 },
    /*av*/{ 3, 4, 0, 4, 4, 4, 0, 0, 0, 0, 0, 1, 5, 5, 2, 4, 1, 2, 5, 0, 1 },
    /*an*/{ 2, 4, 4, 4, 4, 4, 0, 4, 0, 0, 0, 1, 5, 5, 2, 4, 1, 2, 5, 0, 1 },
    /*ki*/{ 2, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 1, 5, 5, 2, 4, 1, 2, 5, 0, 1 },
    /*ld*/{ 2, 4, 4, 4, 4, 4, 0, 4, 4, 4, 0, 1, 5, 5, 2, 4, 1, 2, 5, 0, 1 },
    /*vg*/{ 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 5, 5, 2, 4, 1, 2, 5, 0, 1 },
    /*md*/{ 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 1, 2, 5, 0, 1 },
    /*se*/{ 1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 4, 1, 1, 4, 1, 2, 5, 0, 1 },
    /*vs*/{ 1, 4, 4, 4, 4, 4, 4, 4, 4, 0, 4, 1, 5, 5, 1, 4, 1, 2, 5, 0, 1 },
    /*pl*/{ 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 5, 5, 2, 0, 1, 2, 5, 0, 1 },
    /*pv*/{ 2, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 1, 5, 5, 2, 4, 1, 2, 5, 0, 1 },
    /*sp*/{ 6, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 5, 0, 6 },
    /*lq*/{ 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 5, 5, 5, 5 },
    /*rq*/{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 5, 0, 1 },
    /*wj*/{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 0, 0 },
    /*ot*/{ 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0 }
        // ci me vi ev uv lv av an ki ld vg md se vs pl pv sp lq rq wj ot 
      };
      // nj,vi = 0  e.g. husband 
      // nj,lv = 2 e.g. abbreviation of male I
      // nj,ci = 2 I think
      // nj,lv = 0 for male first personal pronoun abbrev, 
      // should we allow nj+ev,uv,av,an for the same reason?
      // in burmese the only valid use of nj is after virama
      return BKSTATUS[getCharClass(before) - 1][getCharClass(after) - 1];
  }  
  /**
  * gets the character status of teh given character
  * @param character
  * @return class
  */
  protected static int getCharClass(char mmChar)
  {
    int mmClass = MMC_UNKNOWN;
    switch (mmChar)
    {
      case 0x1000:
      case 0x1001:
      case 0x1002:
      case 0x1003:
      case 0x1004:
      case 0x1005:
      case 0x1006:
      case 0x1007:
      case 0x1008:
      case 0x1009:
      case 0x100a:
      case 0x100b:
      case 0x100c:
      case 0x100d:
      case 0x100e:
      case 0x100f:
      case 0x1010:
      case 0x1011:
      case 0x1012:
      case 0x1013:
      case 0x1014:
      case 0x1015:
      case 0x1016:
      case 0x1017:
      case 0x1018:
      case 0x1019:
      case 0x101a:
      case 0x101b:
      case 0x101c:
      case 0x101d:
      case 0x101e:
      case 0x101f:
      case 0x1020:
      case 0x1021:
      case 0x1022:
      case 0x1023:
      case 0x1024:
      case 0x1025:
      case 0x1026:
      case 0x1027:
      case 0x1028:
      case 0x1029:
      case 0x102a:
      case 0x104e:
      case 0x105a:
      case 0x105b:
      case 0x105c:
      case 0x105d:
      case 0x1061:
      case 0x25cc:
      case 0x103f: // tha kyi is almost like a consonant
      //case 0x002d: // not sure about -
        mmClass = MMC_CI; // consonants
        break;
      case 0x103b:
      case 0x103c:
      case 0x103d:
      case 0x103e:
      case 0x105e:
      case 0x105f:
      case 0x1060:
    	  mmClass = MMC_ME; // medials
    	  break;
      case 0x1039:
        mmClass = MMC_VI; // virama
        break;
      case 0x103A:
          mmClass = MMC_KI; // visible killer
          break;
      case 0x1031:
        mmClass = MMC_EV; // e vowel (thwetoo)
        break;
      case 0x102f:
      case 0x1030:
        mmClass = MMC_LV; // lower vowel 
        break;
      case 0x102d:
      case 0x102e:
      case 0x1032:
      case 0x1033:
      case 0x1034:
        mmClass = MMC_UV; // upper vowel
        break;
      case 0x102b:
      case 0x102c:
      case 0x1062:
      case 0x1063:
      case 0x1064:
        mmClass = MMC_AV; // a vowel / yecha
        break;
      case 0x1036:
        mmClass = MMC_AN; // upper dot
        break;    
      case 0x1037:
        mmClass = MMC_LD; // lower dot (aukumit)
        break;
      case 0x1038:
        mmClass = MMC_VG; // visarga / wisanalonpa
        break;
      case 0x1040:
      case 0x1041:
      case 0x1042:
      case 0x1043:
      case 0x1044:
      case 0x1045:
      case 0x1046:
      case 0x1047:
      case 0x1048:
      case 0x1049:
        mmClass = MMC_MD; // myanmar digit
        break;
      case 0x104a:
      case 0x104b:
      case 0x002c:
      case 0x002e:
      case 0x003a:
      case 0x003b:
        mmClass = MMC_SE; // section
        break;  
      case 0x104c:
      case 0x104d:
      case 0x104f:
        mmClass = MMC_VS; // various signs
        break;  
      case 0x1050:
      case 0x1051:
      case 0x1052:
      case 0x1053:
      case 0x1054:
      case 0x1055:
        mmClass = MMC_PL; // Sanskrit letters
        break;
      case 0x1056:
      case 0x1057:
      case 0x1058:
      case 0x1059:
        mmClass = MMC_PV; // Sanskrit vowels
        break;
      case 0x0020:
      case 0x2000:
      case 0x200B:
        mmClass = MMC_SP; // Space
        break;
      case 0x0028:
      case 0x003C:
      case 0x005b:
      case 0x007b:
      case 0x00ab:
      case 0x2018:
      case 0x201c:
      case 0x2039:
        mmClass = MMC_LQ; // left quote/bracket
        break;
      case 0x0029:
      case 0x003E:
      case 0x005d:
      case 0x007d:
      case 0x00bb:
      case 0x2019:
      case 0x201d:
      case 0x203a:
        mmClass = MMC_RQ; // right quote / bracket
        break;
      //case 0x200c:
      //  mmClass = MMC_NJ; // ZWNJ
      //  break;
      case 0x200d:
      case 0x2060:
        mmClass = MMC_WJ; // Word joiner
        break;
      default:
        int charType = Character.getType(mmChar);
        if (Character.isWhitespace(mmChar) 
            || charType == Character.DASH_PUNCTUATION 
            || charType == Character.START_PUNCTUATION 
            || charType == Character.END_PUNCTUATION 
            || charType == Character.OTHER_PUNCTUATION 
            || charType == Character.DASH_PUNCTUATION 
            || charType == Character.DASH_PUNCTUATION 
            || charType == Character.DASH_PUNCTUATION 
            )
          mmClass = MMC_SP;
        else if (Character.getType(mmChar) == Character.START_PUNCTUATION ||
                 Character.getType(mmChar) == Character.INITIAL_QUOTE_PUNCTUATION)
        {
          mmClass = MMC_LQ;
        }
        else if (Character.getType(mmChar) == Character.END_PUNCTUATION ||
                 Character.getType(mmChar) == Character.FINAL_QUOTE_PUNCTUATION)
        {
          mmClass = MMC_RQ;
        }
        else if (Character.getType(mmChar) == Character.OTHER_PUNCTUATION ||
                 Character.getType(mmChar) == Character.DASH_PUNCTUATION /*|| 
                 Character.getType(mmChar) == Character.MODIFIER_SYMBOL*/)
        {
          mmClass = MMC_VS;
        }
        else
          mmClass = MMC_OT;
    }

    //System.out.println("Char " + Integer.toHexString(mmChar) + " " + mmClass + 
    //                   " " + mmChar);
    return mmClass;
  }
  /**
   * Tests whether the character is exclusive to text using the Myanmar script.
   * If this fails, call isNeutralCharacter to check whether the character may
   * occur for punctuation or control within Myanmar text.
   * @param c character to test
   * @return true if character may occur in myanmar text
   */
  public boolean isMyanmarCharacter(char c)
  {
    switch (getCharClass(c))
    {
      case MMC_OT:
      case MMC_WJ:
      case MMC_RQ:
      case MMC_LQ:
      case MMC_SP:
        return false;
    }
    return true;
  }
  /**
   * Tests whether the character may occur within Myanmar text
   * as well as other languages. This is the case for whitespace
   * and punctuaton.
   * @param c character to test
   * @return true if character may occur in myanmar text
   */
  public static boolean isNeutralCharacter(char c)
  {
    switch (getCharClass(c))
    {
      case MMC_WJ:
      case MMC_RQ:
      case MMC_LQ:
      case MMC_SP:
        return true;
    }
    return false;
  }
  /**
  * A class to hold properties of a cluster
  */
  public class ClusterProperties
  {
    int startIndex;
    int endIndex; //< one after last index in cluster
    int breakStatus;
    protected ClusterProperties(int start, int end, int breakStatus)
    {
      this.startIndex = start;
      this.endIndex = end;
      this.breakStatus = breakStatus;
    }
    public int getStart() { return startIndex; }
    public int getEnd() { return endIndex; }
    public int length() { return endIndex - startIndex; }
    public int getBreakStatus() { return breakStatus; }
    public String toString() 
    {
      String bs;
      switch (breakStatus)
      {
        case BK_NO_BREAK:
          bs = "NO";
        case BK_WEIGHT_1:
          bs = "W1";
          break;
        case BK_WEIGHT_2:
          bs = "W2";
          break;
        case BK_CONTEXT:
          bs = "Co";
          break;
        case BK_UNEXPECTED:
          bs = "??";
          break;
        case BK_SYLLABLE:
          bs = "Sy";
          break;
        case BK_WHITESPACE:
          bs = "WS";
          break;
        case BK_EOL:
          bs = "EOL";
          break;
        default:
          bs = "Err";
      }
      return new String("Cluster " + startIndex + "-" + endIndex + " " + bs);
    }
  }  
}