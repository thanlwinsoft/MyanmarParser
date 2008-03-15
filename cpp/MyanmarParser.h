/*
 * Title: MyanmarParser
 * Description: Syllable based Myanmar Parser
 * Copyright:   Copyright (c) 2005,2005 http://www.thanlwinsoft.org
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
#include <stdio.h>
#include <stdint.h>

namespace org_thanlwinsoft_myanmar
{

typedef enum {
// break weights from intitial table approach
  BK_NO_BREAK = 0,
  BK_WEIGHT_1 = 1,// good line break
  BK_WEIGHT_2 = 2,// syllable based word break
  BK_CONTEXT = 3, // used internally only
  BK_UNEXPECTED = 4, // illegal sequence
  BK_SYLLABLE = 5, // syllable break, no line break
  BK_WHITESPACE = 6, // white space character
  BK_EOL = 7, // end of line or string
  BK_STACK_SYLLABLE = 8 // within a stacked combination
} MyanmarBreak;

enum {
	MM_MAX_CONTEXT_LENGTH = 4
};

extern MyanmarBreak lineBreakLevel;

/**
* A class to parse Myanamar Text for line break points. 
* It can also be used to check for erroneous code sequences of
* for Tokenizing, since space based tokenizing gives bad results
* with Myanmar.
* It implements the algorithm described at:
* http://www.thanlwinsoft.org/ThanLwinSoft/MyanmarUnicode/Parsing/
*/
template <class TChar> class MyanmarParser
{
public:
	/**
	* Is the character Myanmar specific?
	* Note: ZWJ, ZWNJ, WJ will all return false.
	*/
	static bool isMyanmarChar(const TChar & ch);
	/**
	* Is the character definitely not Myanmar? 
	* Note: ZWJ, ZWNJ, WJ will all return false.
	* so in general !(isMyanmarChar(c)) != isNotMyanmarChar(c) 
	*/
	static bool isNotMyanmarChar(const TChar & ch);
	/**
	* Is the character a neutral character that can occur in Myanmar as well as
	* other languages?
	* e.g. ZWJ, ZWNJ, WJ will all return false. 
	*/
	static bool isNeutralChar(const TChar & ch);
	
	static bool isLineBreak(const TChar * before, size_t beforeLength,
		                    const TChar * after, size_t afterLength);
	/** Is this a possible word break? 
	* This uses a syllable based algorithm, so will allow breaks within multi-
	* syllable words. However, it will not allow breaks in the middle of 
	* stacked consonants 
	*/
    static bool isWordSyllableBreak(const TChar * before, size_t beforeLength,
		                            const TChar * after, size_t afterLength);
    static size_t nextBreak(const TChar * text, size_t length, size_t pos);
    static size_t prevBreak(const TChar * text, size_t length, size_t pos);
	static bool isValidMyanmar(const TChar * text, size_t length, size_t offset);
	static bool checkMyanmar(const TChar * text, size_t length, 
	                                  size_t offset, size_t & errorPos);

protected:
	static int getCharClass(const TChar & ch);
	static MyanmarBreak getBreakStatus(const TChar & charBefore, 
		                               const TChar & charAfter);
	static size_t getNextSyllable(const TChar * text, size_t length,  
	                              size_t offset, 
					              MyanmarBreak & breakType);
	static bool isLineBreak(int breakStatus);
    static int evaluateContext(const TChar * contextText, size_t length, 
		                       size_t offset);
  
};

typedef MyanmarParser<uint16_t> MyanmarParser16;
typedef MyanmarParser<uint32_t> MyanmarParser32;
typedef MyanmarParser<wchar_t> MyanmarParserW;


}

