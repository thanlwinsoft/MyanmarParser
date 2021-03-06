/*
 * Title: MyanmarParser
 * Description: Syllable based Myanmar Parser
 * Copyright:   Copyright (c) 2007,2009 http://www.thanlwinsoft.org
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validate Myanmar text against encoding order specified by 
 * Unicode Technical Note 11
 * @author keith
 *
 */
public class MyanmarValidator implements Validator
{
    protected static Logger sLogger = Logger.getLogger(MyanmarValidator.class
            .getCanonicalName());

    enum UTN11
    {
        Unknown(0), Number(1), Sign(0), Kinzi(-1), Consonant(2), Stacker(3), Asat(4), 
		MedialYR(5), MedialW(6), MedialH(7), MonAsat(8), EVowel(9), ShanE(10),
		UVowel(11), LVowel(12), KarenVowel(13), ShanVowel(14), AVowel(15), 
		Anusvara(16), PwoTone(17), 
		LowerDot(18), MonH(19), VisibleVirama(20), Visarga(21),Reduplicator(22);

        int mSequenceId;

        UTN11(int seq)
        {
            mSequenceId = seq;
        }

        static UTN11 fromCode(char c)
        {
            // deal with the ranges
            if (c >= 0x1000 && c < 0x102B)
                return Consonant;
            if (c >= 0x105A && c < 0x105E)
                return Consonant;
            if (c >= 0x106E && c < 0x1071)
                return Consonant;
            if (c >= 0x1075 && c < 0x1082)
                return Consonant;
			if (c >= 0xAA60 && c < 0xAA70)
				return Consonant;
			if (c >= 0xAA71 && c < 0xAA77)
				return Consonant;
			if (c >= 0x1040 && c < 0x104A)
                return Number;
			if (c >= 0x1090 && c < 0x109A)
                return Number;
            switch (c)
            {
            case 0x103F:
            case 0x104E:
            case 0x1061:
            case 0x1065:
            case 0x1066:
            case 0x108E:
            //case 0x109F: // is this really a symbol, not if it takes visarga?
            case 0x25CC:// special case for dotted circle
                return Consonant;
                /*
            case 0x1022:
            case 0x1023:
            case 0x1024:
            case 0x1025:
            case 0x1026:
            case 0x1027:
            case 0x1028:
            case 0x1029:
            case 0x102A:*/
            case 0x104A:
            case 0x104B:
            case 0x104C:
            case 0x104D:
            case 0x104F:
            case 0x109E:
            case 0x109F:
                return Sign;
            case 0x103A:
                return Asat;// May also be Mon Asat or Visible Virama
            case 0x1039:
                return Stacker;
            case 0x103B:
            case 0x103C:
            case 0x105E:
            case 0x105F:
                return MedialYR;
            case 0x103D:
            case 0x1082:
                return MedialW;
            case 0x103E:
            case 0x1060:
                return MedialH;// or MonH
            case 0x1031:
            case 0x1084:
                return EVowel; // or Shan E Vowel
            case 0x102D:
            case 0x102E:
            case 0x1032:
            case 0x1033:
            case 0x1034:
            case 0x1035:
            case 0x1036:
            case 0x1071:
            case 0x1072:
            case 0x1073:
            case 0x1074:
            case 0x1085:
            case 0x109D:
                return UVowel;// may also be Anusvara for 1036,1032
            case 0x102F:
            case 0x1030:
                return LVowel;
            case 0x1062:
            case 0x1037:
            	return KarenVowel;// or lower dot
            case 0x1086:
            	return ShanVowel;
            case 0x102B:
            case 0x102C:
            //case 0x1062:
            case 0x1063:
            case 0x1067:
            case 0x1068:
            case 0x1083:
                return AVowel;
            //case 0x1036:
            //case 0x1032:
            //    return Anusvara;
            case 0x1064:// actually Sgaw
            case 0x1069:
            case 0x106A:
            case 0x106B:
            case 0x106C:
            case 0x106D:
            	return PwoTone;
            //case 0x1037:
            //    return LowerDot;
            // case 0x103E:
            //    return MonH;
            // case 103A:
            //    return VisibleVirama;
            case 0x1038:
            case 0x1087:
            case 0x1088:
            case 0x1089:
            case 0x108A:
            case 0x108B:
            case 0x108C:
            case 0x108D:
            case 0x108F:
            case 0x109A:
            case 0x109B:
            case 0x109C:
                return Visarga;
            case 0xAA70:
            	return Reduplicator;
            }
            return Unknown;
        }

        public int getSequenceId()
        {
            return mSequenceId;
        }
    }

    private long mLine = 1;
    private long mColumn = 0;
    private long mTabWidth = 4;
	private String mRef;
    private static final String sEOL = System.getProperty("line.separator");
    private long mErrorCount = 0;

    /**
     * constructor
     */
    public MyanmarValidator()
    {
        sLogger.setLevel(Level.ALL);
    }

    /**
     * tab width
     * @param w
     */
    public void setTabWidth(int w)
    {
        mTabWidth = w;
    }
    /**
     * reset line, col number
     */
    public void reset()
    {
        mLine = 1;
        mColumn = 0;
        mErrorCount = 0;
    }
    /**
     * 
     * @return line number
     */
    public long getLineNumber()
    {
        return mLine;
    };
    /**
     * 
     * @return column index
     */
    public long getColumn()
    {
        return mColumn;
    }

    /**
     * number of errors found since reset
     * @return error count
     */
    public long getErrorCount()
    {
    	return mErrorCount;
    }
	/**
	 * set a reference to assist interpretation of logs
	 * @param ref
	 */
	public void setRef(String ref)
	{
		mRef = ref;
	}
	
	protected void logFine(String msg, Deque<Character> utn11Queue)
	{
	    sLogger.fine(msg + " " + 
                mRef + " Ln " + mLine + " Col " + 
                (mColumn - utn11Queue.size()) + "," +
                mColumn + " " + dumpQueue(utn11Queue));
	}
	protected void logWarning(String msg, Deque<Character> utn11Queue)
    {
        sLogger.warning(msg + " " + 
                mRef + " Ln " + mLine + " Col " + 
                (mColumn - utn11Queue.size()) + "," +
                mColumn + " " + dumpQueue(utn11Queue));
    }

    protected void logInfo(String msg, Deque<Character> utn11Queue)
    {
    	sLogger.info(msg + " " +
                mRef + " Ln " + mLine + " Col " + 
                (mColumn - utn11Queue.size()) + "," +
                mColumn + " " + dumpQueue(utn11Queue));
	}

    /**
     * 
     * @param input
     * @param builder may be null
     * @return validation status
     */
    public Status validate(String input, StringBuilder builder)
    	{
    		BufferedReader r = new BufferedReader(new StringReader(input));
    		BufferedWriter w = new BufferedWriter(new StringWriter());
    		Status s = validate(r, w);
    		if (builder != null) builder.append(w.toString());
    		return s;
    	}
    /** validate text from specified reader
     * 
     */
    public Status validate(BufferedReader r, BufferedWriter w)
    {
        Deque<Character> utn11Queue = new ArrayDeque<Character>(UTN11.Reduplicator
                .getSequenceId());
        Status valid = Status.Valid;
		boolean badPrefix = false;
		UTN11 seq = UTN11.Unknown;
        try
        {
            do
            {
                try
                {
                    int utf32 = r.read();
                    if (utf32 < 0)
                        break; // end
                    char[] utf16 = Character.toChars(utf32);
                    if (utf16[0] == (char) '\t')
                    {
                        mColumn += mTabWidth;
                        long overshoot = mColumn % mTabWidth;
                        mColumn -= overshoot;
                    }
                    else
                        mColumn += utf16.length;
					// surrogate pairs can't be Myanmar
                    if (utf16.length > 1)
                    {
                        valid = writeQueue(utn11Queue, w, valid);
                        w.write(utf16);
                    }
                    else
                    {
                        // approx count lines
                        if (utf16[0] == sEOL.charAt(sEOL.length() - 1))
                        {
                            if (badPrefix)
                            {
                            	logWarning("Bad prefix at: ", utn11Queue);
                            	badPrefix = false;
                            }
                            mLine++;
                            mColumn = 0;
                        }
                        UTN11 prevSeq = seq;
                        seq = UTN11.fromCode(utf16[0]);
                        if (seq == UTN11.Unknown)// not Myanmar
                        {
                            valid = writeQueue(utn11Queue, w, valid);
                            w.write(utf16);
                            continue;
                        }
                        // if there is no stack, just add it and loop, normally
                        // this will only occur for consonants
                        if (utn11Queue.size() == 0)
                        {
                            utn11Queue.push(utf16[0]);
							if (seq.getSequenceId() > UTN11.Consonant.getSequenceId())
							{
								if (utf16[0] == '\u1031' || utf16[0] == '\u103C')
									badPrefix = true;
								else
								{
									logWarning("Unexpected sequence at: ", utn11Queue);
									if (valid == Status.Valid)
		                                valid = Status.Invalid;
									mErrorCount++;
								}
							}
                            continue;
                        }
                        if (prevSeq == UTN11.Number)
						{
							if (seq == UTN11.Number)
							{
								valid = writeQueue(utn11Queue, w, valid);
								utn11Queue.push(utf16[0]);
								continue;
							}
							// This isn't 100% reliable, there could be 
							// legitimate cases
							if (utn11Queue.peek() == '\u1040' && seq != UTN11.Sign)
							{
								char zero = utn11Queue.pop();// remove 0 replace with wa
								if (utn11Queue.size() > 0 &&
									(utn11Queue.peek() >= '\u1040') &&
									(utn11Queue.peek() <= '\u1049'))
								{
									utn11Queue.push(zero); // 0 was correct
								}
								else
								{
									logFine("Corrected wa at: ", utn11Queue);
									utn11Queue.push('\u101D');
									if (valid == Status.Valid)
		                                valid = Status.Corrected;
									mErrorCount++;									
								}
							}
							if (utn11Queue.peek() == '\u1044' && utf16[0] == '\u1004')
							{
							    utn11Queue.push(utf16[0]);
                                continue;
							}
							if (seq == UTN11.Consonant)
	                        {
	                            valid = writeQueue(utn11Queue, w, valid);
	                            utn11Queue.push(utf16[0]);
	                            continue;
	                        }
							utn11Queue.push(utf16[0]);
							continue;
						}
						if (utn11Queue.peek() == '\u1025')
						{
						    if (utf16[0] == '\u103A' || utf16[0] == '\u1039' || utf16[0] == '\u102C')
                            {
                                // should be 1009
                                utn11Queue.pop();
                                utn11Queue.push('\u1009');
                                utn11Queue.push(utf16[0]);
                                if (valid == Status.Valid)
                                    valid = Status.Corrected;
                                mErrorCount++;
                                logFine("Corrected U+1025 at: ", utn11Queue);
                                continue;
                            }
                            if (utf16[0] == '\u102E')
                            {
                                utn11Queue.pop();
                                utn11Queue.push('\u1026');
                                logFine("U+1025 U+102E -> U+1026 ", utn11Queue);
                                if (valid == Status.Valid)
                                    valid = Status.Corrected;
                                mErrorCount++;
                                continue;
                            }
						}
						// check for kinzi
						if (seq == UTN11.Stacker && prevSeq == UTN11.Asat)
						{
							seq = UTN11.Kinzi;
							utn11Queue.push(utf16[0]);
							continue;
						}
						if (prevSeq == UTN11.Kinzi && seq != UTN11.Consonant)
						{
							// misplaced Kinzi
							valid = Status.Invalid;
							utn11Queue.push(utf16[0]);
							logWarning("Misplaced Kinzi ", utn11Queue);
							writeQueue(utn11Queue, w, valid);
							mErrorCount++;
							continue;
						}
						// Now fix the ambiguous cases
                        // 0x103A needs special handling, since it occurs
                        // several times in the sequence
                        if (seq == UTN11.Asat && (prevSeq != UTN11.Consonant &&
                        	utn11Queue.peek() != 0x103D && prevSeq != UTN11.MedialYR))
                        {
                            if (prevSeq == UTN11.LVowel)
                            {
                                Character lv = utn11Queue.pop();
                                UTN11 prevClass = UTN11.fromCode(utn11Queue.peek());
                                if (prevClass.getSequenceId() < UTN11.MonAsat.getSequenceId())
                                {
                                utn11Queue.push(utf16[0]);
                                utn11Queue.push(lv);
                                if (valid == Status.Valid)
                                    valid = Status.Corrected;
                                mErrorCount++;
                                logFine("Corrected U+102F U+103A: ", utn11Queue);
                                continue;
                                }
                                else
                                {
                                	utn11Queue.push(lv);
                                	// more complicated error
                                }
                            }
                            if (prevSeq == UTN11.Asat)
                            {
                                // duplicate asat
                                logFine("Corrected duplicate U+103A", utn11Queue);
                                if (valid == Status.Valid)
                                    valid = Status.Corrected;
                                mErrorCount++;
                                continue;
                            }
                            if (prevSeq == UTN11.MonH)
                            {
                            	seq = UTN11.VisibleVirama;
                            }
                            else if ((utn11Queue.peek() == '\u103E') 
                                || (utn11Queue.peek() == '\u1082'))
                            {
                            	seq = UTN11.MonAsat;
                            }
                            else if (prevSeq == UTN11.KarenVowel || prevSeq == UTN11.LowerDot)
                            {
                            	seq = UTN11.VisibleVirama;
                            }
                            else if (prevSeq == UTN11.AVowel || prevSeq == UTN11.PwoTone || prevSeq == UTN11.MonH)
                            {
                            	seq = UTN11.VisibleVirama;
                            }
                        }
                        // Shan W or Mon H medial with asat - Asat constraint
                        if (prevSeq == UTN11.Asat && (utf16[0] == 0x1082 ||
                        		utf16[0] == 0x103E))
                        {
                        	// this is invalid, but it can be corrected
                          char asat = utn11Queue.pop();
                          UTN11 previousClass = UTN11.fromCode(utn11Queue.peek());
                          if (previousClass.getSequenceId() < UTN11.MedialW.getSequenceId())
                          {
                              if (valid == Status.Valid)
                                  valid = Status.Corrected;
                            	mErrorCount++;
                              utn11Queue.push(utf16[0]);
                              utn11Queue.push(asat);
                              logFine("Changed order of asat/shan wa/1037: ", utn11Queue);
                            	seq = UTN11.MonAsat;
                            	continue;
                          }
                          else
                          {
                        	  utn11Queue.push(asat);
                          }
                        }
                        // check for 1037 here, but older fonts 
                        // expect it unnormalized
                        if ((prevSeq == UTN11.VisibleVirama || prevSeq == UTN11.Asat)
                        		&& utf16[0] == 0x1037)
                        {
                            char asat = utn11Queue.pop();
                            UTN11 previousClass = UTN11.fromCode(utn11Queue.peek());
                            if (previousClass.getSequenceId() <= UTN11.AVowel.getSequenceId())
                            {
                              if (valid == Status.Valid)
                                  valid = Status.Corrected;
                            	mErrorCount++;
                              utn11Queue.push(utf16[0]);
                              utn11Queue.push(asat);
                              logFine("Changed order of visible virama 1037: ", utn11Queue);
                            	seq = UTN11.VisibleVirama;
                            	continue;
                            }
                            else
                            {
                                utn11Queue.push(asat);
                            }
                        }
                        // Shan E constraint
                        if (utf16[0] == 0x1031 && prevSeq == UTN11.EVowel)
                        	seq = UTN11.ShanE;
                        // Karen Vowel constraint
                        if (seq == UTN11.KarenVowel && prevSeq == UTN11.LVowel)
                        {
                        	if (utf16[0] == 0x1037)
                        	{
                        		seq = UTN11.LowerDot;
                        	}
                        	else if (utf16[0] == 0x1062)
                        	{
                        		seq = UTN11.AVowel;
                        	}
                        	else
                        	{
                        		valid = Status.Invalid;
                        		mErrorCount++;
                        		utn11Queue.push(utf16[0]);
                        		logWarning("Unexpected Karen Vowel at: ", utn11Queue);
                        		continue;
                        	}
                        }
                        // Mon Asat constraint
                        if (utf16[0] == 0x1037 && prevSeq == UTN11.MonAsat)
                        {
                        	valid = Status.Invalid;
                    		mErrorCount++;
                    		utn11Queue.push(utf16[0]);
                    		logWarning("Invalid MonAsat position with 1037 at: ", utn11Queue);
                    		continue;
                        }
                        // second 1062
                        if (utf16[0] == 0x1062 && prevSeq == UTN11.KarenVowel)
                        	seq = UTN11.AVowel;
                        // Anusvara Constraint
                        if ((utf16[0] == 0x1032 || utf16[0] == 0x1036) &&
                        	(prevSeq == UTN11.LVowel || 
                        	 prevSeq == UTN11.AVowel || // note need a vowel here
                        	 utn11Queue.peek() == 0x1062 ||
                        	 utn11Queue.peek() == 0x102D ||
                        	 utn11Queue.peek() == 0x102E))
//                       		prevSeq.getSequenceId() >= UTN11.UVowel.getSequenceId())
                        {
                        	seq = UTN11.Anusvara;
                        }
                        // This constraint needs to be added to UTN11
                        if ((utn11Queue.peek() == 0x1032 || utn11Queue.peek() == 0x1036)
                        	&& (utf16[0] == 0x1032 || utf16[0] == 0x1036 || utf16[0] == 0x1086))
                        {
                        	if (utn11Queue.peek() == utf16[0])
                        	{
                        		if (valid == Status.Valid)
                        			valid = Status.Corrected;
                        		logFine("Corrected duplicate upper vowel at: ", utn11Queue);
                        	}
                        	else
                        	{
                        		valid = Status.Invalid;
                        		utn11Queue.push(utf16[0]);
                        		logWarning("Invalid at: ", utn11Queue);
                        	}
                    		mErrorCount++;
                    		continue;
                        }
                        // incorrect uvowel - Upper Vowel constraint
                        if ((utn11Queue.peek() == 0x1032 || utn11Queue.peek() == 0x1036)
                        	&& seq == UTN11.LVowel)
                        {
                        	// correct by swapping
                        	char c = utn11Queue.pop();
                        	UTN11 previousClass = UTN11.fromCode(utn11Queue.peek());
                        	if (previousClass.getSequenceId() < seq.getSequenceId())
                        	{
                        	utn11Queue.push(utf16[0]);
                        	utn11Queue.push(c);
                        	seq = UTN11.Anusvara;
                        	if (valid == Status.Valid)
                                valid = Status.Corrected;
                        	mErrorCount++;
						    logFine("Corrected anusvara lower vowel at: ", 
						            utn11Queue);
                        	continue;
                        	}
                        	else
                        	{
                        		// error more complicated
                        		utn11Queue.push(c);
                        	}
                        	
                        }
                        // Lower Dot constraint what about 1086 here?
                        if (utf16[0] == 0x1037 && (prevSeq == UTN11.LVowel ||
                        	prevSeq == UTN11.AVowel || prevSeq == UTN11.PwoTone
                        	|| prevSeq == UTN11.Anusvara || prevSeq == UTN11.ShanVowel))
//                        	prevSeq.getSequenceId() >= UTN11.KarenVowel.getSequenceId())
                        {
                        	seq = UTN11.LowerDot;
                        }
                        // Mon Ha Constraint
                        if (utf16[0] == 0x103E && utn11Queue.peek() == '\u102C')
                        //prevSeq == UTN11.AVowel)
                        {
                        	seq = UTN11.MonH;
                        }
                        // UVowel Asat is a common typo, but technically allowed
                        if (prevSeq == UTN11.UVowel && seq == UTN11.VisibleVirama)
                        {
                        	logWarning("Upper vowel, asat", utn11Queue);
                        	valid = Status.Invalid;
                        	mErrorCount++;
                        }
                        // Visible Virama constraint
                        if (seq == UTN11.VisibleVirama && (prevSeq != UTN11.AVowel &&
                        	prevSeq != UTN11.PwoTone && prevSeq != UTN11.KarenVowel &&
                        	prevSeq != UTN11.LowerDot && prevSeq != UTN11.MonH))
                        {
                        	logWarning("Unexpected Visible Virama", utn11Queue);
                        	valid = Status.Invalid;
                        	mErrorCount++;
                        	utn11Queue.push(utf16[0]);
                            continue;
                        }
                        // Is it the next in the sequence within the syllable
                        // structure?
                        if (prevSeq.getSequenceId() < seq.getSequenceId())
                        {
                            utn11Queue.push(utf16[0]);
                            continue;
                        }
                        if (seq == UTN11.Sign)
                        {
                            valid = writeQueue(utn11Queue, w, valid);
                            utn11Queue.push(utf16[0]);
                            continue;
                        }
                        // it should be a consonant starting a new syllable
                        if (seq == UTN11.Consonant)
                        {
							if (prevSeq == UTN11.Stacker)
							{
								if (utn11Queue.size() > 3)
								{
									char virama = utn11Queue.pop();
									// checking the length should prevent this matching with a
									// pali kinzi plus stack
									if (utn11Queue.peek() == utf16[0] && utn11Queue.size() == 3)
									{
										// the stack is identical to the previous one, this is
										// almost certainly a double typing
										if (valid == Status.Valid)
											valid = Status.Corrected;
									}
									else
									{
										utn11Queue.push(virama);
										utn11Queue.push(utf16[0]);
										logWarning("Syllable has multiple Pali stacks. Is this really correct? ",
											utn11Queue);
									}
								}
								else
								{
									utn11Queue.push(utf16[0]);	
								}
							}
							else
							{
								if (badPrefix)
								{
								    if ((prevSeq == UTN11.MedialYR || 
                                    prevSeq == UTN11.EVowel))
								    {
    								    utn11Queue.addLast(utf16[0]);
    								    if (valid == Status.Valid)
    		                                valid = Status.Corrected;
    								    mErrorCount++;
    								    logFine("Corrected prefix sequence at: ", 
    								            utn11Queue);
    									badPrefix = false;
    									seq = UTN11.fromCode(utn11Queue.peek());
    									continue;
								    }
								    else
								    {
								        logWarning("Bad prefix at: ", utn11Queue);
								    }
								}
								valid = writeQueue(utn11Queue, w, valid);
	                            utn11Queue.push(utf16[0]);
							}
							badPrefix = false;
                            continue;
                        }
                        // E vowel, zero
                        if (utf16[0] == '\u1040' && badPrefix &&
                            (prevSeq == UTN11.EVowel || prevSeq == UTN11.MedialYR ))
                        {
                            if (valid == Status.Valid)
                                valid = Status.Corrected;
                            mErrorCount++;
                            utn11Queue.addLast('\u101D');
                            logFine("Changed 0 to wa: ", utn11Queue);
                            badPrefix = false;
                            continue;
                        }
                        // something has probably gone wrong
						//if (utf16[0] == '\u1040')
						if (seq == UTN11.Number)
						{
							//sLogger.fine("Corrected wa at: " + 
							//	mRef + " Ln " + mLine + " Col " + 
							//	(mColumn - utn11Queue.size()) + "," +
							//	mColumn + " " + dumpQueue(utn11Queue));
							valid = writeQueue(utn11Queue, w, valid);
							//utn11Queue.push('\u101D');
							utn11Queue.push(utf16[0]);
							continue;
						}
						if (badPrefix && (prevSeq == UTN11.EVowel) && 
						    (seq == UTN11.MedialYR))
						{
						    char eVowel = utn11Queue.pop();
                            utn11Queue.push(utf16[0]);
                            utn11Queue.push(eVowel);
                            if (valid == Status.Valid)
                                valid = Status.Corrected;
                            mErrorCount++;
                            logFine("Corrected e/medial ra: ", utn11Queue);
                            seq = UTN11.fromCode(utn11Queue.peek());
                            continue;
						}
						if ((utn11Queue.size() > 1) && (prevSeq == UTN11.EVowel) && 
	                        ((seq == UTN11.MedialYR) ||( seq == UTN11.MedialW) ||
	                         (seq == UTN11.MedialH)))
						{
						    char eVowel = utn11Queue.pop();
						    UTN11 beforeE = UTN11.fromCode(utn11Queue.peek());
						    if (beforeE.getSequenceId() < seq.getSequenceId())
						    {
						        utn11Queue.push(utf16[0]);
	                            utn11Queue.push(eVowel);
	                            if (valid == Status.Valid)
	                                valid = Status.Corrected;
	                            mErrorCount++;
	                            logFine("Corrected e/medial: ",utn11Queue);
	                            seq = UTN11.fromCode(utn11Queue.peek());
	                            continue;
						    }
						    else // badly wrong, give up trying to correct
						    {
						        utn11Queue.push(eVowel);
						        // error will be logged below
						    }
						}

                        // Check for some common mistakes and fix them
                        if (prevSeq == UTN11.LVowel && seq == UTN11.UVowel)
                        {
                            char lv = utn11Queue.pop();
                            utn11Queue.push(utf16[0]);
                            utn11Queue.push(lv);
                            if (valid == Status.Valid)
                                valid = Status.Corrected;
                            mErrorCount++;
                            logFine("Corrected at lower/upper vowel order: ", 
                                    utn11Queue);
                            seq = UTN11.fromCode(utn11Queue.peek());
                            continue;
                        }
                        if ((utn11Queue.peek() == 0x103D || utn11Queue.peek() == 0x103B) &&
                        	seq == UTN11.Asat)
                        {
                        	// contraction, swap the entries, asat should be 1st
                        	char lv = utn11Queue.pop();
                            utn11Queue.push(utf16[0]);
                            utn11Queue.push(lv);
                            if (valid == Status.Valid)
                                valid = Status.Corrected;
                            mErrorCount++;
                            logFine("Corrected contraction order: ", 
                                    utn11Queue);
                            seq = UTN11.fromCode(utn11Queue.peek());
                            continue;
                        }
                        if ((prevSeq == UTN11.ShanVowel || prevSeq == UTN11.UVowel) && 
                        	(seq == UTN11.MedialYR || seq == UTN11.MedialW
                        	 || seq == UTN11.MedialH))
                        {
                        	char lv = utn11Queue.pop();
                        	UTN11 previousClass = UTN11.fromCode(utn11Queue.peek());
                        	if (previousClass.getSequenceId() < seq.getSequenceId())
                        	{
                            utn11Queue.push(utf16[0]);
                            utn11Queue.push(lv);
                            if (valid == Status.Valid)
                                valid = Status.Corrected;
                            mErrorCount++;
                            logFine("Corrected medial/upper or shan vowel order: ", 
                                    utn11Queue);
                            seq = UTN11.fromCode(utn11Queue.peek());
                        	continue;
                        	}
                        	else
                        	{
                        		// error is more complicated, pushback
                        		utn11Queue.push(lv);
                        	}
                        }
                        if (prevSeq == seq && utf16[0] == utn11Queue.peek())
                        {
                        	if (valid == Status.Valid)
                                valid = Status.Corrected;
                        	mErrorCount++;
                        	logFine("Swallowed duplicate " + utf16[0] + 
                        			": ", utn11Queue);
                        	seq = UTN11.fromCode(utn11Queue.peek());
                        	continue;
                        }
                        if (seq == UTN11.Visarga && prevSeq == UTN11.Reduplicator)
                        {
                        	if (valid == Status.Valid)
                        		valid = Status.Corrected;
                        	mErrorCount++;
                        	char lv = utn11Queue.pop();
                            utn11Queue.push(utf16[0]);
                            utn11Queue.push(lv);
                        	logFine("Corrected Reduplication Visarga", utn11Queue);
                        	seq = prevSeq;
                        	continue;
                        }
                        valid = Status.Invalid;
                        mErrorCount++;
                        utn11Queue.push(utf16[0]);
                        logWarning("Invalid sequence at: ", utn11Queue);
                    }
                }
                catch (IOException e)
                {
                    break;
                }
            } while (true);
            if (utn11Queue.size() > 0) valid = writeQueue(utn11Queue, w, valid);
        }
        catch (IOException e)
        {
            sLogger.severe(e.getMessage());
        }
        finally
        {

        }
        return valid;
    }

	private Status writeQueue(Deque<Character> utn11Queue, BufferedWriter w,
            Status status) throws IOException
    {
        if (utn11Queue.size() > 0 && utn11Queue.peek() == 0x1039)
        {
            // stray 1039, should probably be 103A
            char virama = utn11Queue.pop();
            UTN11 beforeViramaClass = UTN11.fromCode(utn11Queue.peek());
            if (beforeViramaClass == UTN11.Consonant)
            {
	            utn11Queue.push(Character.valueOf((char) 0x103A));
	            sLogger.fine("Corrected 0x1039 at: Ln " + mLine + " Col "
	                    + (mColumn - utn11Queue.size()) + "," + mColumn + " "
	                    + dumpQueue(utn11Queue));
	            if (status == Status.Valid)
	                status = Status.Corrected;
	            mErrorCount++;
            }
            else
            {
            	sLogger.warning("Unexpected virama at: Ln " + mLine + " Col " +
            			(mColumn - utn11Queue.size()) + "," + mColumn + " "
	                    + dumpQueue(utn11Queue));
            	utn11Queue.push(virama);
            	status = Status.Invalid;
            	mErrorCount++;
            }
        }
        // correct ၄င်း
        if (utn11Queue.size() == 4 && utn11Queue.peekLast() == '\u1044' && 
            utn11Queue.peekFirst() == '\u1038')
        {
            Character c = utn11Queue.removeLast();
            if (utn11Queue.peekLast() == '\u1004')
            {
                w.write('\u104E');
                if (status == Status.Valid)
                    status = Status.Corrected;
                mErrorCount++;
                logFine("Corrected ၄င်း", utn11Queue);
            }
            else w.write(c);
        }
        // write the characters, starting with the oldest char on the stack
        while (utn11Queue.size() > 0)
        {
            w.write(utn11Queue.removeLast());
        }
        return status;
    }

    private final String dumpQueue(Deque<Character> utn11Queue)
    {
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        Iterator<Character> i = utn11Queue.iterator();
        while (i.hasNext())
        {
            char c = i.next();
            // iterating backwards from right to left
            if (sb2.length() > 0)
                sb2.insert(0, " ");
            sb2.insert(0, Integer.toHexString(c));
            sb2.insert(0, "0x");
            sb1.insert(0, c);
        }
        sb2.append(" [").append(sb1).append("]");
        return sb2.toString();
    }

    /**
     * Command line
     * @param arg input files, output file/dir
     */
    public static void main(String[] arg)
    {
        if (arg.length < 2)
        {
            System.err.println("Usage: MyanmarValidator input output");
            System.err.println("       MyanmarValidator input1 input2 ... outputDir");
            System.exit(1);
        }
        BufferedReader br = null;
        BufferedWriter bw = null;

        File outputDir = new File(arg[arg.length - 1]);
        if (!outputDir.isDirectory())
        {
            if (arg.length > 2)
            {
                System.err.println("Last argument should be a directory");
                System.exit(2);
            }
            outputDir = null;
        }
        for (int i = 0; i < arg.length - 1; i++)
        {
            File inputFile = new File(arg[i]);
            File outputFile = null;
            try
            {
                br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(inputFile), "UTF-8"));
                if (outputDir != null)
                {
                    outputFile = new File(outputDir, inputFile.getName());
                }
                else
                {
                    assert (arg.length == 2);
                    outputFile = new File(arg[arg.length - 1]);
                }
                if (inputFile.equals(outputFile))
                {
                    System.err.println("input/output can't be the same file!");
                    System.exit(3);
                }
                bw = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(outputFile), "UTF-8"));

                MyanmarValidator validator = new MyanmarValidator();
                if (System.getProperty("os.name").equals("Linux"))
                    validator.setTabWidth(8);
				validator.setRef(inputFile.getName());
                Status status = validator.validate(br, bw);
                if (status != Status.Valid)
                    sLogger.info(arg[i] + " Validation state: " + status);
            }
            catch (FileNotFoundException e)
            {
                sLogger.log(Level.SEVERE, e.getMessage(), e);
            }
            catch (UnsupportedEncodingException e)
            {
                sLogger.log(Level.SEVERE, e.getMessage(), e);
            }
            finally
            {
                try
                {
                    if (br != null)
                        br.close();
                }
                catch (IOException e)
                {
                    sLogger.log(Level.SEVERE, e.getMessage(), e);
                }
                try
                {
                    if (bw != null)
                        bw.close();
                }
                catch (IOException e)
                {
                    sLogger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
        System.exit(0);
    }
}
