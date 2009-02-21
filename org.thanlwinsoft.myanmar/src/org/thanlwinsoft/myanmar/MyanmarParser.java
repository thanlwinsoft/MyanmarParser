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

	// usually you want to line break after the last whitespace character, though
    // you don't count the whitespace in the line width
    public final static int LANG_MY = 0; // Myanmar
    public final static int LANG_KSW = 1; // S'Gaw Karen
    public final static int LANG_PWO = 2; // Pwo Karen
  
    public enum MySyllablePart
    {
	    MY_SYLLABLE_UNKNOWN,
	    MY_SYLLABLE_CONSONANT,
	    MY_SYLLABLE_MEDIAL,
	    MY_SYLLABLE_VOWEL,
	    MY_SYLLABLE_TONE,
	    MY_SYLLABLE_1039,
	    MY_SYLLABLE_103A,
	    MY_SYLLABLE_NUMBER,
	    MY_SYLLABLE_SECTION,
	    MY_SYLLABLE_NUM_PARTS;
    }

    public enum MyPairStatus
    {
        MY_PAIR_ILLEGAL,
        MY_PAIR_NO_BREAK,
        MY_PAIR_SYL_BREAK,
        MY_PAIR_WORD_BREAK,
        MY_PAIR_PUNCTUATION,
        MY_PAIR_CONTEXT,
        MY_PAIR_EOL;
    }

	static final MySyllablePart [] CHAR_PART = new MySyllablePart[]
         {
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1000;MYANMAR LETTER KA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1001;MYANMAR LETTER KHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1002;MYANMAR LETTER GA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1003;MYANMAR LETTER GHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1004;MYANMAR LETTER NGA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1005;MYANMAR LETTER CA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1006;MYANMAR LETTER CHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1007;MYANMAR LETTER JA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1008;MYANMAR LETTER JHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1009;MYANMAR LETTER NYA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//100A;MYANMAR LETTER NNYA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//100B;MYANMAR LETTER TTA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//100C;MYANMAR LETTER TTHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//100D;MYANMAR LETTER DDA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//100E;MYANMAR LETTER DDHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//100F;MYANMAR LETTER NNA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1010;MYANMAR LETTER TA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1011;MYANMAR LETTER THA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1012;MYANMAR LETTER DA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1013;MYANMAR LETTER DHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1014;MYANMAR LETTER NA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1015;MYANMAR LETTER PA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1016;MYANMAR LETTER PHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1017;MYANMAR LETTER BA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1018;MYANMAR LETTER BHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1019;MYANMAR LETTER MA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//101A;MYANMAR LETTER YA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//101B;MYANMAR LETTER RA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//101C;MYANMAR LETTER LA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//101D;MYANMAR LETTER WA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//101E;MYANMAR LETTER SA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//101F;MYANMAR LETTER HA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1020;MYANMAR LETTER LLA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1021;MYANMAR LETTER A;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1022;MYANMAR LETTER SHAN A;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1023;MYANMAR LETTER I;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1024;MYANMAR LETTER II;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1025;MYANMAR LETTER U;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1026;MYANMAR LETTER UU;Lo;0;L;1025 102E;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1027;MYANMAR LETTER E;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1028;MYANMAR LETTER MON E;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1029;MYANMAR LETTER O;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//102A;MYANMAR LETTER AU;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//102B;MYANMAR VOWEL SIGN TALL AA;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//102C;MYANMAR VOWEL SIGN AA;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//102D;MYANMAR VOWEL SIGN I;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//102E;MYANMAR VOWEL SIGN II;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//102F;MYANMAR VOWEL SIGN U;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1030;MYANMAR VOWEL SIGN UU;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1031;MYANMAR VOWEL SIGN E;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1032;MYANMAR VOWEL SIGN AI;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1033;MYANMAR VOWEL SIGN MON II;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1034;MYANMAR VOWEL SIGN MON O;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1035;MYANMAR VOWEL SIGN E ABOVE;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1036;MYANMAR SIGN ANUSVARA;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//1037;MYANMAR SIGN DOT BELOW;Mn;7;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//1038;MYANMAR SIGN VISARGA;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_1039,//1039;MYANMAR SIGN VIRAMA;Mn;9;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_103A,//103A;MYANMAR SIGN ASAT;Mn;9;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_MEDIAL,//103B;MYANMAR CONSONANT SIGN MEDIAL YA;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_MEDIAL,//103C;MYANMAR CONSONANT SIGN MEDIAL RA;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_MEDIAL,//103D;MYANMAR CONSONANT SIGN MEDIAL WA;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_MEDIAL,//103E;MYANMAR CONSONANT SIGN MEDIAL HA;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//103F;MYANMAR LETTER GREAT SA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1040;MYANMAR DIGIT ZERO;Nd;0;L;;0;0;0;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1041;MYANMAR DIGIT ONE;Nd;0;L;;1;1;1;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1042;MYANMAR DIGIT TWO;Nd;0;L;;2;2;2;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1043;MYANMAR DIGIT THREE;Nd;0;L;;3;3;3;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1044;MYANMAR DIGIT FOUR;Nd;0;L;;4;4;4;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1045;MYANMAR DIGIT FIVE;Nd;0;L;;5;5;5;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1046;MYANMAR DIGIT SIX;Nd;0;L;;6;6;6;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1047;MYANMAR DIGIT SEVEN;Nd;0;L;;7;7;7;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1048;MYANMAR DIGIT EIGHT;Nd;0;L;;8;8;8;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1049;MYANMAR DIGIT NINE;Nd;0;L;;9;9;9;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_SECTION,//104A;MYANMAR SIGN LITTLE SECTION;Po;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_SECTION,//104B;MYANMAR SIGN SECTION;Po;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//104C;MYANMAR SYMBOL LOCATIVE;Po;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//104D;MYANMAR SYMBOL COMPLETED;Po;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//104E;MYANMAR SYMBOL AFOREMENTIONED;Po;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//104F;MYANMAR SYMBOL GENITIVE;Po;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1050;MYANMAR LETTER SHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1051;MYANMAR LETTER SSA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1052;MYANMAR LETTER VOCALIC R;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1053;MYANMAR LETTER VOCALIC RR;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1054;MYANMAR LETTER VOCALIC L;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1055;MYANMAR LETTER VOCALIC LL;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1056;MYANMAR VOWEL SIGN VOCALIC R;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1057;MYANMAR VOWEL SIGN VOCALIC RR;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1058;MYANMAR VOWEL SIGN VOCALIC L;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1059;MYANMAR VOWEL SIGN VOCALIC LL;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//105A;MYANMAR LETTER MON NGA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//105B;MYANMAR LETTER MON JHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//105C;MYANMAR LETTER MON BBA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//105D;MYANMAR LETTER MON BBE;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_MEDIAL,//105E;MYANMAR CONSONANT SIGN MON MEDIAL NA;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_MEDIAL,//105F;MYANMAR CONSONANT SIGN MON MEDIAL MA;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_MEDIAL,//1060;MYANMAR CONSONANT SIGN MON MEDIAL LA;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1061;MYANMAR LETTER SGAW KAREN SHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1062;MYANMAR VOWEL SIGN SGAW KAREN EU;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1063;MYANMAR TONE MARK SGAW KAREN HATHI;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1064;MYANMAR TONE MARK SGAW KAREN KE PHO;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1065;MYANMAR LETTER WESTERN PWO KAREN THA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1066;MYANMAR LETTER WESTERN PWO KAREN PWA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1067;MYANMAR VOWEL SIGN WESTERN PWO KAREN EU;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1068;MYANMAR VOWEL SIGN WESTERN PWO KAREN UE;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//1069;MYANMAR SIGN WESTERN PWO KAREN TONE-1;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//106A;MYANMAR SIGN WESTERN PWO KAREN TONE-2;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//106B;MYANMAR SIGN WESTERN PWO KAREN TONE-3;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//106C;MYANMAR SIGN WESTERN PWO KAREN TONE-4;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//106D;MYANMAR SIGN WESTERN PWO KAREN TONE-5;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//106E;MYANMAR LETTER EASTERN PWO KAREN NNA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//106F;MYANMAR LETTER EASTERN PWO KAREN YWA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1070;MYANMAR LETTER EASTERN PWO KAREN GHWA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1071;MYANMAR VOWEL SIGN GEBA KAREN I;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1072;MYANMAR VOWEL SIGN KAYAH OE;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1073;MYANMAR VOWEL SIGN KAYAH U;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1074;MYANMAR VOWEL SIGN KAYAH EE;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1075;MYANMAR LETTER SHAN KA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1076;MYANMAR LETTER SHAN KHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1077;MYANMAR LETTER SHAN GA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1078;MYANMAR LETTER SHAN CA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1079;MYANMAR LETTER SHAN ZA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//107A;MYANMAR LETTER SHAN NYA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//107B;MYANMAR LETTER SHAN DA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//107C;MYANMAR LETTER SHAN NA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//107D;MYANMAR LETTER SHAN PHA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//107E;MYANMAR LETTER SHAN FA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//107F;MYANMAR LETTER SHAN BA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1080;MYANMAR LETTER SHAN THA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//1081;MYANMAR LETTER SHAN HA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_MEDIAL,//1082;MYANMAR CONSONANT SIGN SHAN MEDIAL WA;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1083;MYANMAR VOWEL SIGN SHAN AA;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1084;MYANMAR VOWEL SIGN SHAN E;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1085;MYANMAR VOWEL SIGN SHAN E ABOVE;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_VOWEL,//1086;MYANMAR VOWEL SIGN SHAN FINAL Y;Mn;0;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//1087;MYANMAR SIGN SHAN TONE-2;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//1088;MYANMAR SIGN SHAN TONE-3;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//1089;MYANMAR SIGN SHAN TONE-5;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//108A;MYANMAR SIGN SHAN TONE-6;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//108B;MYANMAR SIGN SHAN COUNCIL TONE-2;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//108C;MYANMAR SIGN SHAN COUNCIL TONE-3;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//108D;MYANMAR SIGN SHAN COUNCIL EMPHATIC TONE;Mn;220;NSM;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//108E;MYANMAR LETTER RUMAI PALAUNG FA;Lo;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_TONE,//108F;MYANMAR SIGN RUMAI PALAUNG TONE-5;Mc;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1090;MYANMAR SHAN DIGIT ZERO;Nd;0;L;;0;0;0;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1091;MYANMAR SHAN DIGIT ONE;Nd;0;L;;1;1;1;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1092;MYANMAR SHAN DIGIT TWO;Nd;0;L;;2;2;2;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1093;MYANMAR SHAN DIGIT THREE;Nd;0;L;;3;3;3;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1094;MYANMAR SHAN DIGIT FOUR;Nd;0;L;;4;4;4;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1095;MYANMAR SHAN DIGIT FIVE;Nd;0;L;;5;5;5;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1096;MYANMAR SHAN DIGIT SIX;Nd;0;L;;6;6;6;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1097;MYANMAR SHAN DIGIT SEVEN;Nd;0;L;;7;7;7;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1098;MYANMAR SHAN DIGIT EIGHT;Nd;0;L;;8;8;8;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_NUMBER,//1099;MYANMAR SHAN DIGIT NINE;Nd;0;L;;9;9;9;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_UNKNOWN,//109A
   		  MySyllablePart.MY_SYLLABLE_UNKNOWN,//109B
   		  MySyllablePart.MY_SYLLABLE_UNKNOWN,//109C
   		  MySyllablePart.MY_SYLLABLE_UNKNOWN,//109D
   		  MySyllablePart.MY_SYLLABLE_CONSONANT,//109E;MYANMAR SYMBOL SHAN ONE;So;0;L;;;;;N;;;;;
   		  MySyllablePart.MY_SYLLABLE_CONSONANT//109F;MYANMAR SYMBOL SHAN EXCLAMATION;So;0;L;;;;;N;;;;;
   	  };

	  final static int [] [] PAIR_TABLE = new int [][]
      {
    		 // 0=illegal, 1=no, 2=yes, 3=yes-line, 4=punctuation, 5=context,
    		 //-  C  M  V  T 39 3A  N  S
    		 { 2, 3, 1, 1, 1, 1, 1, 1, 1 },//-
    		 { 3, 5, 1, 1, 1, 1, 1, 2, 4 },//C
    		 { 1, 5, 1, 1, 1, 0, 0, 2, 4 },//M
    		 { 3, 5, 0, 1, 1, 0, 1, 2, 4 },//V
    		 { 3, 2, 0, 1, 1, 0, 0, 2, 4 },//T
    		 { 3, 1, 0, 0, 0, 0, 0, 0, 0 },//1039
    		 { 3, 2, 1, 1, 1, 1, 0, 2, 4 },//103A
    		 { 1, 2, 1, 1, 1, 0, 0, 1, 4 },//N
    		 { 3, 2, 0, 0, 0, 0, 0, 2, 0 }//S
      };


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
    	MyPairStatus breakType = MyPairStatus.MY_PAIR_NO_BREAK;
		int i = offset;
		boolean foundCluster = false;
		if (offset >= text.length()) return null;
		int langGuess = guessLanguage(text.toCharArray());
	    while (i + 1 < text.length())
	    {
	    	MyPairStatus breakStatus = getBreakStatus(text.charAt(i),text.charAt(i+1));
	        /*
	        System.out.println("bs U+" + 
	                         Integer.toHexString(text.charAt(i)) + ",U+" +
	                         Integer.toHexString(text.charAt(i+1)) + 
	                         " =" + breakStatus + " " +
	                         text.charAt(i) + "|" + text.charAt(i+1));
	                         */
	    	if (breakStatus == MyPairStatus.MY_PAIR_NO_BREAK)
	    	{
	    	}
	    	else if (breakStatus == MyPairStatus.MY_PAIR_SYL_BREAK ||
	    			breakStatus == MyPairStatus.MY_PAIR_WORD_BREAK ||
	    			breakStatus == MyPairStatus.MY_PAIR_PUNCTUATION ||
	    			breakStatus == MyPairStatus.MY_PAIR_ILLEGAL)
	    	{
			    breakType = breakStatus;
			    foundCluster = true;
			}
			else if (breakStatus == MyPairStatus.MY_PAIR_CONTEXT)
			{
			    breakType = evaluateContext(text, i, langGuess);
			    if (breakType != MyPairStatus.MY_PAIR_NO_BREAK)
			    	foundCluster = true;
			}
			else
			{
				// shouldn't happen unless there is an error in this class
				System.err.println("Unexpected status" + breakStatus);
		    }
	    	if (foundCluster) break;
	    	i++;
	    }
	    if (i + 1 == text.length()) breakType = MyPairStatus.MY_PAIR_EOL;
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
	  MyPairStatus breakType = MyPairStatus.MY_PAIR_NO_BREAK;
    int i = offset;
    boolean foundCluster = false;
    if (offset >= text.length) return null;
    int langGuess = guessLanguage(text);
    while (i + 1 < text.length)
    {
      MyPairStatus breakStatus = getBreakStatus(text[i],text[i+1]);
      if (breakStatus == MyPairStatus.MY_PAIR_NO_BREAK)
      {
      }
      else if (breakStatus == MyPairStatus.MY_PAIR_SYL_BREAK ||
    		   breakStatus == MyPairStatus.MY_PAIR_WORD_BREAK ||
    		   breakStatus == MyPairStatus.MY_PAIR_PUNCTUATION ||
    		   breakStatus == MyPairStatus.MY_PAIR_ILLEGAL)
      {
          breakType = breakStatus;
          foundCluster = true;
      }
      else if (breakStatus == MyPairStatus.MY_PAIR_CONTEXT)
      {
          breakType = evaluateContext(new String(text), i, langGuess);
          if (breakType != MyPairStatus.MY_PAIR_NO_BREAK)
            foundCluster = true;
      }
      else
      {
    	  // shouldn't happen unless there is an error in this class
          System.err.println("Unexpected status" + breakStatus);
      }
      if (foundCluster) break;
      i++;
    }
    if (i + 1 == text.length) breakType = MyPairStatus.MY_PAIR_EOL;
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
        case 0x1065:
        case 0x1066:
        case 0x1067:
        case 0x1068:
        case 0x1069:
        case 0x106a:
        case 0x106b:
        case 0x106c:
        case 0x106d:
        	language = LANG_PWO;
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
      if (cp.getBreakStatus() == MyPairStatus.MY_PAIR_ILLEGAL)
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
      if (cp.getBreakStatus() == MyPairStatus.MY_PAIR_ILLEGAL || prevError)
      {  
        valid = false;
        if (errorClusters == null)
        {
          errorClusters = new ArrayList<ClusterProperties>(1);
          //errorClusters = new ArrayList(1);
        }
        errorClusters.add(cp);
        if (cp.getBreakStatus() != MyPairStatus.MY_PAIR_ILLEGAL) 
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
  public boolean isLineBreak(MyPairStatus breakStatus)
  {
    boolean lineBreak = false;
    if (breakStatus == MyPairStatus.MY_PAIR_SYL_BREAK ||
    	breakStatus == MyPairStatus.MY_PAIR_WORD_BREAK)
      lineBreak = true;
    return lineBreak;
  }
  /**
  * evaluates the context where a simple pair approach is not enough
  * @param contextText
  * @param offset
  * @return break status of specified offset in text
  */                      
  protected MyPairStatus evaluateContext(String contextText, int offset, int langHint)
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
    if (text[0] == 0x1021 && langHint == LANG_MY) return MyPairStatus.MY_PAIR_NO_BREAK;

    if (text[1] == 0x002d) return MyPairStatus.MY_PAIR_NO_BREAK;
    if (text[1] == 0x103F) return MyPairStatus.MY_PAIR_NO_BREAK;
    
    if (text[2] == 0x1039)
    {
      return MyPairStatus.MY_PAIR_NO_BREAK;
    }
    else if (text[2] == 0x103A && langHint == LANG_MY)
    {
      // Karen (and also some load words in Myanmar) can have a starting 103A
      return MyPairStatus.MY_PAIR_NO_BREAK;
    }
    else
    {
      return MyPairStatus.MY_PAIR_SYL_BREAK;
    }
  }
  /**
  * gets the break status of a pair of characters
  * @param before
  * @param after
  * @return break status code
  */
  protected static MyPairStatus getBreakStatus(char before, char after)
  {
	  return MyPairStatus.values()[PAIR_TABLE[getCharClass(before).ordinal()]
	                                         [getCharClass(after).ordinal()]];
  }
  /**
  * gets the character status of the given character
  * @param character
  * @return class
  */
  protected static MySyllablePart getCharClass(char mmChar)
  {
	  if (mmChar < 0x1000 || mmChar > 0x109F)
	  {
		  return MySyllablePart.MY_SYLLABLE_UNKNOWN;
	  }
	return CHAR_PART[mmChar - 0x1000];
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
	  if (c < 0x1000 || c > 0x109f)
		  return false;
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
    switch (Character.getType(c))
    {
    case Character.INITIAL_QUOTE_PUNCTUATION:
    case Character.FINAL_QUOTE_PUNCTUATION:
    case Character.SPACE_SEPARATOR:
    case Character.DASH_PUNCTUATION:
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
    MyPairStatus breakStatus;
    protected ClusterProperties(int start, int end, MyPairStatus breakStatus)
    {
      this.startIndex = start;
      this.endIndex = end;
      this.breakStatus = breakStatus;
    }
    public int getStart() { return startIndex; }
    public int getEnd() { return endIndex; }
    public int length() { return endIndex - startIndex; }
    public MyPairStatus getBreakStatus() { return breakStatus; }
    public String toString() 
    {
      String bs;
      
      if (breakStatus == MyPairStatus.MY_PAIR_NO_BREAK)
          bs = "NO";
      else if (breakStatus == MyPairStatus.MY_PAIR_SYL_BREAK)
          bs = "WS";
      else if (breakStatus == MyPairStatus.MY_PAIR_WORD_BREAK)
          bs = "WW";
      else if (breakStatus == MyPairStatus.MY_PAIR_CONTEXT)
          bs = "Co";
      else if (breakStatus == MyPairStatus.MY_PAIR_ILLEGAL)
          bs = "??";
      else if (breakStatus == MyPairStatus.MY_PAIR_EOL)
          bs = "EOL";
      else
    	  bs = "Err";

      return new String("Cluster " + startIndex + "-" + endIndex + " " + bs);
    }
  }  
}
