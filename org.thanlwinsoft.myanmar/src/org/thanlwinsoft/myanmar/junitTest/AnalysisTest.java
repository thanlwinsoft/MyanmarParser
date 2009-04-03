/**
 * 
 */
package org.thanlwinsoft.myanmar.junitTest;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.thanlwinsoft.myanmar.Analysis;
import org.thanlwinsoft.myanmar.AnalysisSyllable;
import org.thanlwinsoft.myanmar.MyanmarValidator;


/**
 * @author keith
 *
 */
public class AnalysisTest
{
	/**
	 * 
	 */
	public AnalysisTest()
	{
		
	}

	static Map<AnalysisSyllable, Integer> runTest(String data, int contextLength)
	{
		try 
		{
			Analysis analizer = new Analysis(new MyanmarValidator(), contextLength);
			BufferedReader br = new BufferedReader(new StringReader(data));
			analizer.analyse(br);

			for (AnalysisSyllable s : analizer.getSyllables().keySet())
			{
				System.out.println(s.toString() + " " + analizer.getSyllables().get(s));
			}
			return analizer.getSyllables();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * 
	 */
	@Test
	public void test1()
	{
		Map<AnalysisSyllable, Integer> results = runTest("အၚ်ဂဒဵု\nအၚ်လိက်", 0);
		Assert.assertEquals(2, results.get(new AnalysisSyllable("အၚ်", new ArrayDeque<String>())).intValue());
		Assert.assertEquals(1, results.get(new AnalysisSyllable("ဂ", new ArrayDeque<String>())).intValue());
		Assert.assertEquals(1, results.get(new AnalysisSyllable("ဒဵု", new ArrayDeque<String>())).intValue());
		Assert.assertEquals(1, results.get(new AnalysisSyllable("လိက်", new ArrayDeque<String>())).intValue());
		Assert.assertEquals(4, results.size());
	}

	/**
	 * 
	 */
	@Test
	public void test2()
	{
		Map<AnalysisSyllable, Integer> results = runTest("38	အၚ်လိက်	a\n39	အၚ်သ,အၚ်္သXXX	b\n40	အစာ	c", 0);
		Assert.assertEquals(2, results.get(new AnalysisSyllable("အၚ်", new ArrayDeque<String>())).intValue());
		Assert.assertEquals(1, results.get(new AnalysisSyllable("သ", new ArrayDeque<String>())).intValue());
		Assert.assertEquals(1, results.get(new AnalysisSyllable("အၚ်္သ", new ArrayDeque<String>())).intValue());
		Assert.assertEquals(1, results.get(new AnalysisSyllable("လိက်", new ArrayDeque<String>())).intValue());
		Assert.assertEquals(1, results.get(new AnalysisSyllable("အစာ", new ArrayDeque<String>())).intValue());
		Assert.assertEquals(5, results.size());
	}
//ကျ	1000 103B
	/**
	 * 
	 */
	@Test
	public void test3()
	{
		Map<AnalysisSyllable, Integer> results = runTest("ကျ	1000 103B", 0);
		Assert.assertEquals(1,results.size());
		Assert.assertEquals(1, results.get(new AnalysisSyllable("ကျ", new ArrayDeque<String>())).intValue());
	}
}
