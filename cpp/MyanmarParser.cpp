/*
 * Title: MyanmarParser
 * Description: Syllable based Myanmar Parser
 * Copyright:   Copyright (c) 2005,2006 http://www.thanlwinsoft.org
 *
 * @author Keith Stribley
 * @version 0.2
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of one or more of the following licenses:
 * a) the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * b) the GNU General Public License Version 2 or later (the "GPL")
 * as published by the Free Software Foundation;
 * c) the Mozilla Public License Version 1.1 
 * You may not use this file except in compliance with one or more of the above
 * Licenses
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
#include "MyanmarParser.h"

#include <assert.h>

namespace org_thanlwinsoft_myanmar
{




// character classes
  const  int MMC_UNKNOWN = 0;
  const  int MMC_CI = 1;
  const  int MMC_VI = 2;
  const  int MMC_EV = 3;
  const  int MMC_LV = 4;
  const  int MMC_UV = 5;
  const  int MMC_AV = 6;
  const  int MMC_AN = 7;
  const  int MMC_LD = 8;
  const  int MMC_VG = 9;
  const  int MMC_MD = 10;
  const  int MMC_SE = 11;
  const  int MMC_VS = 12;
  const  int MMC_PL = 13;
  const  int MMC_PV = 14;
  const  int MMC_SP = 15;
  const  int MMC_LQ = 16;
  const  int MMC_RQ = 17;
  const  int MMC_NJ = 18;
  const  int MMC_WJ = 19;
  const  int MMC_OT = 20;
  // usually you want to line break after the last whitespace character, though
  // you don't count the whitespace in the line width

/**
   * Finds the next syllable in the string starting at a given offset.
   * The caller must check the return value to know whether a break is 
   * allowed there or not.
   * @param text text to search
   * @param offset index to start search
   * @return ClusterProperties
   */
  template <class TChar>
  size_t MyanmarParser<TChar>::getNextSyllable(const TChar * text, size_t length,  
	                                       size_t offset, 
					       MyanmarBreak & breakType)
  {
    breakType = BK_NO_BREAK;
    size_t i = offset;
    bool foundCluster = false;
    if (offset >= length) return length;
    while (i + 1 < length)
    {
      int breakStatus = getBreakStatus(text[i],text[i+1]);
      switch (breakStatus)
      {
        case BK_NO_BREAK:
          break;
        case BK_SYLLABLE:
        case BK_STACK_SYLLABLE:
          // this is wrong if its a medial
          if (text[i] == 0x1039)
          {
            switch (text[i+1])
            {
              case 0x101a:
              case 0x101b:
              case 0x101d:
              case 0x101f:
                break;
              default:
                breakType = breakStatus;
                foundCluster = true;
            }
            break;
          }
        case BK_WEIGHT_1:
        case BK_WEIGHT_2:
        case BK_UNEXPECTED:
        case BK_WHITESPACE:
          breakType = breakStatus;
          foundCluster = true;
          break;
        case BK_CONTEXT:
          breakType = evaluateContext(text, length, i);
          if (breakType != BK_NO_BREAK)
            foundCluster = true;
          break;
        default: // shouldn't happen unless there is an error in this class
          assert(printf("Unexpected status %d\n",breakStatus));
      }
      if (foundCluster) break;
      i++;
    }
    if (i + 1 == text.length) breakType = BK_EOL;
    return i + 1;
  }
    
  
  /**
   * Finds the next line break point in the string starting at a given offset.
   * The caller must check the return value to know whether a break is 
   * allowed there or not.
   * @param text text to search
   * @param offset index to start search
   * @return ClusterProperties
   */
  template <class TChar>
  size_t MyanmarParser<TChar>::nextBreak(const TChar * text, size_t length, size_t offset)
  {
    size_t i = offset;
    MyanmarBreak status = BK_NO_BREAK;
    size_t cp = offset;
    if (offset >= length) return length;
    do 
    {
      cp = getNextSyllable(text, i, status);
      i = cp;
    } while (!isLineBreak(status) && i < length);
    return cp;
  }
  
  /**
   * Checks for incorrect sequences of Myanmar characters
   * @param text text to search
   * @param offset index to start search
   * @return array of erroneous clusters
   */
  template <class TChar>
  bool MyanmarParser<TChar>::isValidMyanmar(const TChar * text, size_t length, size_t offset)
  {
    size_t i = offset;
    MyanmarBreak status = BK_NO_BREAK;
    size_t cp = offset;
    bool valid = true;
    if (offset >= length) return valid;
    do 
    {
      cp = getNextSyllable(text, i, status);
      i = cp;
      if (status == BK_UNEXPECTED)
      {  
        valid = false;
        break;
      }
    } while (i < length);
    return valid;
  }
  
  /**
   * Checks for incorrect sequences of Myanmar characters
   * @param text text to search
   * @param offset index to start search
   * @param errorPos of error if one was found
   * @return true if no errors were found
   */
  template <class TChar>
  bool MyanmarParser<TChar>::checkMyanmar(const TChar * text, size_t length, 
	                                  size_t offset, size_t & errorPos)
  {
    size_t i = offset;
    MyanmarBreak status = BK_NO_BREAK;
    size_t cp = 0;
    
    bool valid = true;
    if (offset >= text.length()) return valid;
    bool prevError = false;
    do 
    {
      cp = getNextSyllable(text, i, status);
      i = cp;
      if (status == BK_UNEXPECTED || prevError)
      {  
        valid = false;
        errorPos = cp;
	return valid;
      }
    } while (i < text.length());
    return valid;
  }
  
  /** A simple method to determine whether the break status allows a line
  * break. If the breakStatus is weight 1 or 2 a line break will be allowed.
  * @param breakStatus
  * @return true if a line break is permissible
  */
  template <class TChar>
  bool MyanmarParser<TChar>::isLineBreak(int breakStatus)
  {
    bool lineBreak = false;
    if (breakStatus > BK_NO_BREAK || breakStatus <= lineBreakLevel )
      lineBreak = true;
    return lineBreak;
  }                     
  /**
  * evaluates the context where a simple pair approach is not enough
  * @param contextText
  * @param offset
  * @return break status of specified offset in text
  */                      
  template <class TChar>
  int MyanmarParser<TChar>::evaluateContext(const TChar * contextText, size_t length, size_t offset)
  {
    const TChar * text = contextText + offset;
    TChar dummyContext[MM_MAX_CONTEXT_LENGTH];
    if (length < offset + MM_MAX_CONTEXT_LENGTH) 
    {
      text = dummyContext;
      for (size_t i = 0; i<MM_MAX_CONTEXT_LENGTH; i++)
      {
        if (offset + i < length)
          text[i] = contextText[offset + i];
        else text[i] = ' ';
      }
    }
    // deal with easy cases first
    if (text[0] == 0x1021) return BK_NO_BREAK;
    
    if (text[1] == 0x002d) return BK_NO_BREAK;
    
    if (text[1] == 0x1004) // nga
    {
      // check for Kinzi
      // but there must never be a line break here
      if (text[2] == 0x1039) 
      {
        return BK_NO_BREAK;
      }
      else return BK_WEIGHT_2;
    }
    else if (text[2] == 0x1039)
    {
      switch (text[3])
      {
      case 0x200C: // visible virama
          return BK_NO_BREAK;
      // stacked consonants
      case 0x1000:
      case 0x1001:
      case 0x1002:
      case 0x1003:
      //case 0x1004: 
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
      case 0x101c:
      case 0x101e:
      case 0x1020:
      case 0x1021:
          return BK_NO_BREAK;
          //return BK_STACK_SYLLABLE;
      default:
          return BK_WEIGHT_2;
      }
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
  template <class TChar>
  MyanmarBreak MyanmarParser<TChar>::getBreakStatus(const TChar & before, const TChar & after)
  {
    // BK_NO_BREAK = 0; BK_WEIGHT_1 = 1; BK_WEIGHT_2 = 2; BK_CONTEXT = 3; 
    // BK_UNEXPECTED = 4; BK_SYLLABLE = 5; BK_WHITESPACE = 6; BK_EOL = 7;
    const int BKSTATUS[20][20] = 
      {
        // ci vi ev lv uv av an ld vg md se vs pl pv sp lq rq nj wj ot 
    /*ci*/{ 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 6, 5, 2, 4, 1, 2, 5, 4, 0, 1 },
    /*vi*/{ 0, 0, 4, 0, 4, 4, 4, 4, 4, 0, 4, 4, 4, 4, 1, 2, 5, 0, 0, 1 },
    /*ev*/{ 3, 4, 4, 0, 0, 0, 0, 0, 0, 1, 6, 5, 2, 4, 1, 2, 5, 4, 0, 1 },
    /*lv*/{ 3, 4, 4, 4, 0, 0, 0, 0, 0, 1, 6, 5, 2, 4, 1, 2, 5, 4, 0, 1 },
    /*uv*/{ 3, 4, 4, 4, 4, 0, 0, 0, 0, 1, 6, 5, 2, 4, 1, 2, 5, 4, 0, 1 },
    /*av*/{ 3, 0, 4, 4, 4, 4, 0, 0, 0, 1, 6, 5, 2, 4, 1, 2, 5, 4, 0, 1 },
    /*an*/{ 2, 4, 4, 4, 4, 4, 4, 0, 0, 1, 6, 5, 2, 4, 1, 2, 5, 4, 0, 1 },
    /*ld*/{ 2, 4, 4, 4, 4, 4, 4, 4, 0, 1, 6, 5, 2, 4, 1, 2, 5, 4, 0, 1 },
    /*vg*/{ 2, 4, 4, 4, 4, 4, 4, 4, 4, 1, 6, 5, 2, 4, 1, 2, 5, 4, 0, 1 },
    /*md*/{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 1, 2, 5, 4, 0, 1 },
    /*se*/{ 1, 4, 4, 4, 4, 4, 4, 4, 4, 1, 4, 1, 1, 4, 1, 2, 5, 4, 0, 1 },
    /*vs*/{ 1, 4, 4, 4, 4, 4, 4, 0, 4, 1, 6, 5, 1, 4, 1, 2, 5, 4, 0, 1 },
    /*pl*/{ 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 6, 5, 2, 0, 1, 2, 5, 4, 0, 1 },
    /*pv*/{ 2, 4, 0, 0, 0, 0, 0, 0, 0, 1, 6, 5, 2, 4, 1, 2, 5, 4, 0, 1 },
    /*sp*/{ 6, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 5, 4, 0, 6 },
    /*lq*/{ 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 5, 5, 4, 5, 5 },
    /*rq*/{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 5, 4, 0, 1 },
    /*nj*/{ 2, 0, 4, 0, 4, 4, 4, 0, 0, 1, 6, 5, 2, 4, 1, 2, 5, 4, 0, 1 },
    /*wj*/{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 4, 0, 0 },
    /*ot*/{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 0, 0, 0 }
      };
      // nj,vi = 0  e.g. husband 
      // nj,lv = 2 e.g. abbreviation of male I
      // nj,ci = 2 I think
      // nj,lv = 0 for male first personal pronoun abbrev, 
      // should we allow nj+ev,uv,av,an for the same reason?
      // in burmese the only valid use of nj is after virama
      return static_cast<MyanmarBreak>(BKSTATUS[getCharClass(before) - 1]
	                                       [getCharClass(after) - 1]);
  }  
  /**
  * gets the character status of teh given character
  * @param character
  * @return class
  */
  template <class TChar>
  int MyanmarParser<TChar>::getCharClass(const TChar & mmChar)
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
      case 0x25cc:
      //case 0x002d: // not sure about -
        mmClass = MMC_CI; // consonants
        break;
      case 0x1039:
        mmClass = MMC_VI; // virama
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
        mmClass = MMC_UV; // upper vowel
        break;
      case 0x102c:
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
      case 0x005b:
      case 0x007b:
      case 0x00ab:
      case 0x2018:
      case 0x201c:
      case 0x2039:
        mmClass = MMC_LQ; // left quote/bracket
        break;
      case 0x0029:
      case 0x005d:
      case 0x007d:
      case 0x00bb:
      case 0x2019:
      case 0x201d:
      case 0x203a:
        mmClass = MMC_RQ; // right quote / bracket
        break;
      case 0x200c:
        mmClass = MMC_NJ; // ZWNJ
        break;
      case 0x200d:
      case 0x2060:
        mmClass = MMC_WJ; // Word joiner
        break;
      default:/*
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
                 Character.getType(mmChar) == Character.DASH_PUNCTUATION || 
                 Character.getType(mmChar) == Character.MODIFIER_SYMBOL)
        {
          mmClass = MMC_VS;
        }
        else*/
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
  template <class TChar>
  bool MyanmarParser<TChar>::isMyanmarChar(const TChar & c)
  {
    switch (getCharClass(c))
    {
      case MMC_OT:
      case MMC_WJ:
      case MMC_RQ:
      case MMC_LQ:
      case MMC_SP:
      case MMC_NJ:
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
  template <class TChar>
  bool MyanmarParser<TChar>::isNeutralChar(const TChar & c)
  {
    switch (getCharClass(c))
    {
      case MMC_WJ:
      case MMC_RQ:
      case MMC_LQ:
      case MMC_SP:
      case MMC_NJ:
        return true;
    }
    return false;
  }
  /**
   * Tests whether the character may occur within Myanmar text
   * as well as other languages. This is the case for whitespace
   * and punctuaton.
   * @param c character to test
   * @return true if character may occur in myanmar text
   */
  template <class TChar>
  bool MyanmarParser<TChar>::isNotMyanmarChar(const TChar & c)
  {
    switch (getCharClass(c))
    {
      case MMC_OT:
      case MMC_RQ:
      case MMC_LQ:
      case MMC_SP:
        return true;
    }
    return true;
  }
  /**
  * A class to hold properties of a cluster
  */
  /*
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
  }  */
}
