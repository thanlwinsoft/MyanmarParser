package org.thanlwinsoft.myanmar;
/*
 * Copyright:   Copyright (c) 2008 http://www.thanlwinsoft.org
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;


/**
 * Analysis of text to extract and count the occurrences of each Myanmar syllable.
 * If the -c option is used, then the number of occurrences is reported for
 * a context of up to i syllables.
 */
public class Analysis
{
    protected static Logger sLogger = Logger.getLogger(Analysis.class
            .getCanonicalName());
    
    private int mMaxContext = 1;
    private Map<Syllable, Integer> mSyllables = new TreeMap<Syllable, Integer> ();
    private Validator mValidator = null;
    private MyanmarParser mParser = new MyanmarParser();
    
    /**
     * Command Line analysis
     * @param arg [options] input output
     */
    public static void main(String[] arg)
    {
        final String eol = System.getProperty("line.separator");
        final String help = "Usage: Analysis [-c i] [-d] input output" + eol
            + "       Analysis [-c i] [-d] input1 input2 ... outputDir" + eol
            + "where:" + eol
            + "  -c i indicates sets the context length to i syllables e.g. -c 4" + eol
            + "  -d   disable output of statistics" + eol
            + "e.g. Analysis -c 0 -d input.txt syllableList.txt";
        if (arg.length < 2)
        {
            System.err.println(help);
            System.exit(1);
        }
        int firstArg = 0;
        boolean outputStats = true;
        int contextLength = 1;
        while (firstArg < arg.length && arg[firstArg].charAt(0) == '-')
        {
            if (arg[firstArg].length() != 2)
            {
                System.err.println("Invalid argument arg[firstArg]");
                System.exit(2);
            }
            switch (arg[firstArg].charAt(1))
            {
            case 'c':
                try
                {
                    contextLength = Integer.parseInt(arg[++firstArg]);
                }
                catch (NumberFormatException e)
                {
                    System.err.println(help);
                }
                break;
            case 'd':
                outputStats = false;
                break;
            default:
                System.err.println(help);
            }
            ++firstArg;
        }
        BufferedReader br = null;
        BufferedWriter bw = null;
        Analysis analizer = new Analysis(new MyanmarValidator(), contextLength);
        for (int i = firstArg; i < arg.length - 1; i++)
        {
            File inputFile = new File(arg[i]);
            try
            {
                br = new BufferedReader(new InputStreamReader(
                        new FileInputStream(inputFile), "UTF-8"));
                analizer.analyse(br);
            }
            catch(IOException e)
            {
                sLogger.warning(e.getLocalizedMessage());
            }
            finally
            {
                if (br != null)
                {
                    try
                    {
                        br.close();
                    }
                    catch(IOException e)
                    {
                        sLogger.warning(e.getLocalizedMessage());
                    }
                }
            }
        }
        // output analysis results
        try
        {

            File outputFile = new File(arg[arg.length - 1]);
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile), "UTF-8"));
            analizer.writeResults(bw, outputStats);
            bw.close();
        }
        catch(IOException e)
        {
            sLogger.warning(e.getLocalizedMessage());
        }
    }
    /**
     * output results to a writer
     * @param bw
     * @param outputStats
     * @throws IOException
     */
    public void writeResults(BufferedWriter bw, boolean outputStats) throws IOException
    {
        Iterator <Syllable> i = mSyllables.keySet().iterator();
        while (i.hasNext())
        {
            Syllable s = i.next();
            bw.write(s.toString());
            if (outputStats)
            {
                bw.append('\t');
                bw.append(Integer.toString(mSyllables.get(s)));
            }
            bw.newLine();
        }
    }

    /**
     * Class to hold syllable information
     * @author keith
     *
     */
    public class Syllable implements Comparable<Syllable>
    {
        private String mText;
        private int mCount = 1;
        private ArrayDeque<String> mFollowing;
        /**
         * Syllable constructor
         * @param s - the syllable
         * @param following syllables
         */
        public Syllable(String s, ArrayDeque<String> following)
        {
            mText = s;
            mFollowing = following;
        }

        @Override
        public int compareTo(Syllable o)
        {
            int r = mText.compareTo(o.mText);
            Iterator<String> i = mFollowing.iterator();
            Iterator<String> j = o.mFollowing.iterator();
            while (r == 0)
            {
                if (i.hasNext())
                    if (j.hasNext())
                    {
                        r = i.next().compareTo(j.next());
                    }
                    else
                    {
                        r = 1;
                        break;
                    }
                else if (j.hasNext())
                {
                    r = -1;
                    break;
                }
                else 
                {
                    r = 0;
                    break;
                }
            }
            return r;
        }
        /**
         * increment count of this syllables occurances
         */
        public void add() { mCount++; }
        /**
         * 
         * @return number of occurances of syllable
         */
        public int getCount() { return mCount; }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append(mText);
            Iterator <String> i = mFollowing.iterator();
            while (i.hasNext())
                sb.append(i.next());
            return sb.toString();
        }
        
    }
    /**
     * Constructor
     * @param validator
     * @param contextLength
     */
    public Analysis(Validator validator, int contextLength)
    {
    	mValidator = validator;
        mMaxContext = contextLength;
    }

    /**
     * analyse data from reader
     * @param br
     * @throws IOException
     */
    public void analyse(BufferedReader br) throws IOException
    {
        String line = null;
        while ((line = br.readLine()) != null)
        {
        	if (line.length() == 0) continue;
            BufferedReader lineReader = new BufferedReader(new StringReader(line));
            StringWriter validateWriter = new StringWriter();
            BufferedWriter lineWriter = new BufferedWriter(validateWriter);
            MyanmarValidator.Status s = mValidator.validate(lineReader, lineWriter);
            lineWriter.close();
            if (s != MyanmarValidator.Status.Invalid)
            {
                String validated = validateWriter.toString();
                int offset = 0;
                ArrayDeque<String> syllables = new ArrayDeque<String>(); 
                MyanmarParser.ClusterProperties cp = mParser.getNextSyllable(validated, offset);
                if (cp == null)
                {
                	sLogger.warning("Failed to get syllable for: " + validated);
                }
                else
                {
	                String syllableText = validated.substring(cp.getStart(), cp.getEnd());
	                while (cp != null && cp.getBreakStatus() != MyanmarParser.MyPairStatus.MY_PAIR_EOL)
	                {
	                    if (mParser.isMyanmarCharacter(syllableText.charAt(0)) == false ||
	                        cp.getBreakStatus() == MyanmarParser.MyPairStatus.MY_PAIR_WORD_BREAK)
	                    {
	                        processSyllables(syllables);
	                        syllables.clear();
	                    }
	                    else
	                    {
	                        syllables.push(syllableText);
	                    }
	                    offset = cp.getEnd();
	                    cp = mParser.getNextSyllable(validated, offset);
	                    syllableText = validated.substring(cp.getStart(), cp.getEnd());
	                }
	                if (mParser.isMyanmarCharacter(syllableText.charAt(0)))
	                    syllables.push(syllableText);
	                processSyllables(syllables);
                }
            }
            else
            {
                sLogger.warning("Ignoring: " + s);
            }
        }
    }

    private void processSyllables(ArrayDeque<String> syllables)
    {
        while (syllables.size() > 0)
        {
            String s = syllables.pop();
            ArrayDeque<String> context = syllables.clone();
            while (context.size() > mMaxContext)
                context.removeLast();
            Syllable syl = new Syllable(s, context);
            if (mSyllables.containsKey(syl))
            {
                int count = mSyllables.get(syl);
                mSyllables.put(syl, count + 1);
            }
            else mSyllables.put(syl, 1);
        }
    }
}
