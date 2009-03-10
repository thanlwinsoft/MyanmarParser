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
}
