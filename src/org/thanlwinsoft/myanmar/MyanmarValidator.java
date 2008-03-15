/*
 * Title: MyanmarParser
 * Description: Syllable based Myanmar Parser
 * Copyright:   Copyright (c) 2007 http://www.thanlwinsoft.org
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyanmarValidator
{
    protected static Logger sLogger = Logger.getLogger(MyanmarValidator.class
            .getCanonicalName());

    public enum Status
    {
        Valid, Corrected, Invalid
    }

    enum UTN11
    {
        Unknown(0), Number(1), Sign(0), Consonant(2), Asat(3), Stacker(4), 
		MedialYR(5), MedialW(6), MedialH(7), EVowel(8), 
		UVowel(9), LVowel(10), AVowel(11), Anusvara(12), 
		VisibleVirama(13), LowerDot(14), Visarga(15);

        int mSequenceId;

        UTN11(int seq)
        {
            mSequenceId = seq;
        }

        static UTN11 fromCode(char c)
        {
            // deal with the ranges
            if (c >= 0x1000 && c < 0x1023)
                return Consonant;
			if (c >= 0x1040 && c < 0x104A)
                return Number;
            switch (c)
            {
            case 0x25CC:// special case for dotted circle
                return Consonant;
            case 0x1022:
            case 0x1023:
            case 0x1024:
            case 0x1025:
            case 0x1026:
            case 0x1027:
            case 0x1028:
            case 0x1029:
            case 0x102A:
            case 0x104A:
            case 0x104B:
            case 0x104C:
            case 0x104D:
            case 0x104E:
            case 0x104F:
                return Sign;
            case 0x103A:
                return Asat;
            case 0x1039:
                return Stacker;
            case 0x103B:
            case 0x103C:
                return MedialYR;
            case 0x103D:
                return MedialW;
            case 0x103E:
                return MedialH;
            case 0x1031:
                return EVowel;
            case 0x102D:
            case 0x102E:
            case 0x1032:
                return UVowel;
            case 0x102F:
            case 0x1030:
                return LVowel;
            case 0x102B:
            case 0x102C:
                return AVowel;
            case 0x1036:
                return Anusvara;
            case 0x1037:
                return LowerDot;
            case 0x1038:
                return Visarga;

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

    public MyanmarValidator()
    {
        sLogger.setLevel(Level.ALL);
    }

    public void setTabWidth(int w)
    {
        mTabWidth = w;
    }

    public void reset()
    {
        mLine = 1;
        mColumn = 0;
    }

    public long getLineNumber()
    {
        return mLine;
    };

    public long getColumn()
    {
        return mColumn;
    }
	
	public void setRef(String ref)
	{
		mRef = ref;
	}

    public Status validate(BufferedReader r, BufferedWriter w)
    {
        Deque<Character> utn11Queue = new ArrayDeque<Character>(UTN11.Visarga
                .getSequenceId());
        Status valid = Status.Valid;
		boolean badPrefix = false;
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
                            mLine++;
                            mColumn = 0;
                        }

                        UTN11 seq = UTN11.fromCode(utf16[0]);
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
									sLogger.warning("Unexpected sequence at: " + 
										mRef + " Ln " + mLine + " Col " + 
										(mColumn - utn11Queue.size()) + "," +
										mColumn + " " + dumpQueue(utn11Queue));
								}
							}
                            continue;
                        }
                        UTN11 prevSeq = UTN11.fromCode(utn11Queue.peek());
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
							if (utn11Queue.peek() == '\u1040')
							{
								sLogger.fine("Corrected wa at: " + 
									mRef + " Ln " + mLine + " Col " + 
									(mColumn - utn11Queue.size()) + "," +
									mColumn + " " + dumpQueue(utn11Queue));
								utn11Queue.pop();// remove 0 replace with wa
								utn11Queue.push('\u101D');
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
						if (utn11Queue.peek() == '\u1025' && 
						    (utf16[0] == '\u103A' || utf16[0] == '\u102C'))
                        {
                            // should be 1009
                            utn11Queue.pop();
                            utn11Queue.push('\u1009');
                            utn11Queue.push(utf16[0]);
                            sLogger.fine("Corrected U+1025 at: " + 
                                    mRef + " Ln " + mLine + " Col " + 
                                    (mColumn - utn11Queue.size()) + "," +
                                    mColumn + " " + dumpQueue(utn11Queue));
                            continue;
                        }
                        // 0x103A needs special handling, since it occurs twice
                        // in the sequence
                        if (seq == UTN11.Asat && prevSeq != UTN11.Consonant)
                        {
                            seq = UTN11.VisibleVirama;
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
	                            utn11Queue.push(utf16[0]);
							}
							else
							{
								if (badPrefix)
								{
								    if ((prevSeq == UTN11.MedialYR || 
                                    prevSeq == UTN11.EVowel))
								    {
    								    utn11Queue.addLast(utf16[0]);
    									sLogger.fine("Corrected prefix sequence at: " + 
    										mRef + " Ln " + mLine + " Col " + 
    										(mColumn - utn11Queue.size()) + "," +
    										mColumn + " " + dumpQueue(utn11Queue));
    									badPrefix = false;
    									continue;
								    }
								    else
								    {
								        sLogger.warning("Bad prefix at: "
				                            + mRef + " Ln " + mLine
				                            + " Col " + (mColumn - utn11Queue.size()) + ","
				                            + mColumn + " " + dumpQueue(utn11Queue));
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
                            utn11Queue.addLast('\u101D');
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
                            sLogger.fine("Corrected e/medial ra: "
                                    + mRef + " Ln " + mLine + " Col "
                                    + (mColumn - utn11Queue.size()) + ","
                                    + mColumn + " " + dumpQueue(utn11Queue));
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
	                            sLogger.fine("Corrected e/medial: "
	                                    + mRef + " Ln " + mLine + " Col "
	                                    + (mColumn - utn11Queue.size()) + ","
	                                    + mColumn + " " + dumpQueue(utn11Queue));
	                            continue;
						    }
						    else // badly wrong, give up trying to correct
						    {
						        utn11Queue.push(eVowel);
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
                            sLogger.fine("Corrected at: "
									+ mRef + " Ln " + mLine + " Col "
                                    + (mColumn - utn11Queue.size()) + ","
                                    + mColumn + " " + dumpQueue(utn11Queue));
                            continue;
                        }
                        utn11Queue.push(utf16[0]);
                        sLogger.warning("Invalid sequence at: "
								+ mRef + " Ln " + mLine
                                + " Col " + (mColumn - utn11Queue.size()) + ","
                                + mColumn + " " + dumpQueue(utn11Queue));
                        valid = Status.Invalid;
                    }
                }
                catch (IOException e)
                {
                    break;
                }
            } while (true);
            if (utn11Queue.size() > 0) writeQueue(utn11Queue, w, valid);
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
            utn11Queue.pop();
            utn11Queue.push(Character.valueOf((char) 0x103A));
            sLogger.fine("Corrected 0x1039 at: Ln " + mLine + " Col "
                    + (mColumn - utn11Queue.size()) + "," + mColumn + " "
                    + dumpQueue(utn11Queue));
            if (status == Status.Valid)
                status = Status.Corrected;
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
