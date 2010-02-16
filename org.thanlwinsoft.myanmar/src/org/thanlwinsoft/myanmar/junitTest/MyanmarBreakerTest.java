/*
* Title: MyanmarBreakerTest
* Description: Syllable/Dictionary based Myanmar Line Breaker Test
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
package org.thanlwinsoft.myanmar.junitTest;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;


import org.junit.Test;
import org.thanlwinsoft.myanmar.MyanmarBreaker;

/**
 * Tests the Myanmar Breaker
 * @author keith
 *
 */
public class MyanmarBreakerTest
{

	private void breakTest(String dict, String in, String expectedOut)
    {
		BufferedReader dictReader = new BufferedReader(new StringReader(dict));
        
        try
        {
        	MyanmarBreaker mv = new MyanmarBreaker('|', dictReader);
        	BufferedReader inReader = new BufferedReader(new StringReader(in));
        	StringWriter outWriter = new StringWriter();
        	BufferedWriter bufferedOut = new BufferedWriter(outWriter); 
        	mv.parse(inReader, bufferedOut);
            inReader.close();
            bufferedOut.close();
            
            outWriter.flush();
            if (!outWriter.toString().equals(expectedOut))
            {
                fail("MyanmarBreaker output " + outWriter.toString() + 
                        " but should have output " + expectedOut);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail("Exception during validate. " + e);
        }
    }
	/**
	 * test handling of punctuation to check word isn't split too early
	 */
	@Test
	public void testBreak1()
	{
		breakTest("ပညာ\nရေး\nပညာရေး","ပညာရေး၊ ","ပညာရေး၊ \n");
	}
	
	/**
	 * test finding words
	 */
	@Test
	public void testBreak2()
	{
		breakTest("ကျွန်ုပ်\nတို့","ကျွန်ုပ်တို့၏","ကျွန်ုပ်|တို့|၏\n");
	}
	/**
	 * test finding words
	 */
	@Test
	public void testBreak3()
	{
		breakTest("အောင်မြင်\nအောင်\nမြင်\nမှု\nတို့\nသည်","အောင်မြင်မှုတို့သည် ","အောင်မြင်|မှု|တို့|သည် \n");
	}
	
	/**
	 * test use of Word Joiner U+2060
	 */
	@Test
	public void testBreak4()
	{
		breakTest("","ပ⁠ရို⁠တိန်း","ပ⁠ရို⁠တိန်း\n");
	}
	
	/**
	 * PaO test
	 */
	@Test
	public void testBreak5()
	{
		breakTest("","တꩻညာꩻ","တꩻ|ညာꩻ\n");
	}
}

