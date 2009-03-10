/*
* Title: MyanmarBreaker
* Description: Syllable/Dictionary based Myanmar Line Breaker
* Copyright:   2009 http://www.thanlwinsoft.org
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.thanlwinsoft.myanmar.MyanmarParser.ClusterProperties;
import org.thanlwinsoft.myanmar.MyanmarParser.MyPairStatus;

/**
 * Class to break Myanmar text into words separated by spaces or ZWSP
 * @author keith
 *
 */
public class MyanmarBreaker
{
	private final char mSpacer;
	private int mMaxSyllables;
	private HashSet<String> mWordList = new HashSet<String>();
	/**
	 * Constructor
	 * @param spacer - char to place in breaks which are found
	 * @param dictionary 
	 * @throws IOException 
	 */
	public MyanmarBreaker(char spacer, BufferedReader dictionary) throws IOException
	{
		mSpacer = spacer;
		MyanmarParser mp = new MyanmarParser();
		if (dictionary != null)
		{
			String word = dictionary.readLine();
			while (word != null)
			{
				int offset = 0;
				int syllableCount = 0;
				while (offset < word.length())
				{
					ClusterProperties cp = mp.getNextLineBreak(word, offset);
					offset = cp.getEnd();
					++syllableCount;
				}
				mMaxSyllables = Math.max(mMaxSyllables, syllableCount);
				mWordList.add(word);
				word = dictionary.readLine();
			}
		}
	}
	
	/** Parse text from reader and write word broken output to writer
	 * @param r
	 * @param w
	 * @throws IOException
	 */
	public void parse(BufferedReader r, BufferedWriter w) throws IOException
	{
		MyanmarParser mp = new MyanmarParser();
		String line = r.readLine();
		Vector<ClusterProperties> syllables = new Vector<ClusterProperties>();
		while (line != null)
		{
			int offset = 0;
			do
			{
				if (line.length() == 0)
				{
					w.newLine();
					continue;
				}
				ClusterProperties cp = mp.getNextSyllable(line, offset);
				syllables.add(cp);
				MyPairStatus status = cp.getBreakStatus();
				if (status == MyPairStatus.MY_PAIR_WORD_BREAK ||
					status == MyPairStatus.MY_PAIR_EOL ||
					status == MyPairStatus.MY_PAIR_PUNCTUATION)
				{
					checkAndAppendSyllables(w, line, syllables);
				}
				offset = cp.getEnd();
			} while (offset < line.length());
			w.newLine();
			line = r.readLine();
		}
	}
	
	protected void checkAndAppendSyllables(BufferedWriter w, String line, Vector<ClusterProperties> syllables) throws IOException
	{
		while (syllables.size() > 0)
		{
			int candidateLength = Math.min(syllables.size(), mMaxSyllables);
				
			for (; candidateLength > 0; --candidateLength)
			{
				ClusterProperties endSyllable = syllables.elementAt(candidateLength - 1);

				String candidate = line.substring(syllables.firstElement().getStart(),
						endSyllable.getEnd());
				if (mWordList.contains(candidate))
				{
					w.append(candidate);
					appendSpacer(w, line, endSyllable);
					List <ClusterProperties> wordCP = new ArrayList<ClusterProperties>(syllables.subList(0, candidateLength));
					syllables.removeAll(wordCP);
					break;
				}
			}
			if (candidateLength == 0)
			{
				// no match found, so append first syllable as is
				w.append(line.substring(syllables.firstElement().getStart(), syllables.firstElement().getEnd()));
				appendSpacer(w, line, syllables.firstElement());
				syllables.remove(0);
			}
		}
	}
	
	private void appendSpacer(BufferedWriter w, String line, ClusterProperties endSyllable) throws IOException
	{
		MyPairStatus status = endSyllable.getBreakStatus();
		if (status == MyPairStatus.MY_PAIR_WORD_BREAK)
		{
			if (Character.getType(line.charAt(endSyllable.getEnd() - 1)) !=
				Character.SPACE_SEPARATOR &&
				(endSyllable.getEnd() != line.length()) &&
				Character.getType(line.charAt(endSyllable.getEnd())) !=
				Character.SPACE_SEPARATOR)
			{
				w.append(mSpacer);
			}
		}
		else if (status == MyPairStatus.MY_PAIR_SYL_BREAK)
		{
			w.append(mSpacer);
		}
	}
	
	/**
	 * MyanmarBreaker command line entry point
	 * @param args - dictionary file, input text, output text, spacer to insert
	 */
	public static void main(String[] args)
	{
		BufferedWriter bw = null;
		BufferedReader br = null;
		BufferedReader dict = null;
		if (args.length < 2)
		{
			System.out.println("Usage: MyanmarBreaker dictionary.txt input.txt brokenOutput.txt [spaceChar]");
			System.out.println("\tDefault space character = ZWSP");
			System.exit(1);
		}
		File dictFile = new File(args[0]);
		String inputFile = args[1];
		String outputFile = args[2];
		char spacer = 0x200B;
		if (args.length == 4) spacer = args[3].charAt(0);
		try
		{
			bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outputFile), "UTF-8"));
			br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(inputFile), "UTF-8"));
			if (dictFile.exists())
			{
				dict = new BufferedReader(new InputStreamReader(
                    new FileInputStream(dictFile), "UTF-8"));
			}
			else
			{
				System.err.println("No dictionary found.");
			}
			MyanmarBreaker breaker = new MyanmarBreaker(spacer, dict);
			breaker.parse(br, bw);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (bw != null)
					bw.close();
				if (br != null)
					br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

}
