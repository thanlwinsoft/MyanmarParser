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
        Unknown(0), Consonant(1), Asat(2), Stacker(3), MedialYR(4), MedialW(5), MedialH(
                6), EVowel(7), UVowel(8), LVowel(9), AVowel(10), Anusvara(11), VisibleVirama(
                12), LowerDot(13), Visarga(14);

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
            switch (c)
            {
            case 0x25CC:// special case for dotted circle
                return Consonant;
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

    public Status validate(BufferedReader r, BufferedWriter w)
    {
        Deque<Character> utn11Queue = new ArrayDeque<Character>(UTN11.Visarga
                .getSequenceId());
        Status valid = Status.Valid;
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
                            continue;
                        }
                        UTN11 prevSeq = UTN11.fromCode(utn11Queue.peek());
                        // 0x103A needs special handling, since it occurs twice
                        // in the sequence
                        if (seq == UTN11.Asat && prevSeq != UTN11.Consonant)
                            seq = UTN11.VisibleVirama;
                        // Is it the next in the sequence within the syllable
                        // structure?
                        if (prevSeq.getSequenceId() < seq.getSequenceId())
                        {
                            utn11Queue.push(utf16[0]);
                            continue;
                        }
                        // it should be a consonant starting a new syllable
                        if (seq == UTN11.Consonant)
                        {
                            if (prevSeq != UTN11.Stacker)
                                valid = writeQueue(utn11Queue, w, valid);
                            utn11Queue.push(utf16[0]);
                            continue;
                        }
                        // something has probably gone wrong

                        // Check for some common mistakes and fix them
                        if (prevSeq == UTN11.LVowel && seq == UTN11.UVowel)
                        {
                            char lv = utn11Queue.pop();
                            utn11Queue.push(utf16[0]);
                            utn11Queue.push(lv);
                            if (valid == Status.Valid)
                                valid = Status.Corrected;
                            sLogger.fine("Corrected at: Ln " + mLine + " Col "
                                    + (mColumn - utn11Queue.size()) + ","
                                    + mColumn + " " + dumpQueue(utn11Queue));
                            continue;
                        }
                        utn11Queue.push(utf16[0]);
                        sLogger.warning("Invalid sequence at: Ln " + mLine
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
        }
        // catch (IOException e)
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
