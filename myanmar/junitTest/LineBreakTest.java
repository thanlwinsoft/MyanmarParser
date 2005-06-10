package myanmar.junitTest;

import junit.framework.*;
import junit.extensions.TestSetup;
import myanmar.MyanmarParser;
import myanmar.MyanmarParser.ClusterProperties;

public class LineBreakTest extends TestCase
{  
	static MyanmarParser myParser = null;
  final String STRING_A = "ကောင္‌း";
  final String STRING_B = "ယုံက္ရည္‌သောအားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။";
  final String STRING_C = "အင္ဂလိပ္‌";
  final String STRING_D = "င‍္ဝေက္ရေး";
  final String STRING_E = "ကုုိ";
  final String STRING_F = "ဘာ လဲ။";
  public void test_lb_0() 
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_A, 0);
    assertEquals(cp.toString(), 6, cp.getEnd());
    assertEquals(cp.toString(), MyanmarParser.BK_EOL, 
                 cp.getBreakStatus());
  }
  
  public void test_lb_1() // ယုံက္ရည္‌သောအားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 0);
    assertEquals(cp.toString(), 2, cp.getEnd());
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  
  public void test_lb_2() // က္ရည္‌သောအားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 3);
    assertEquals(cp.toString(), 8, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }

  public void test_lb_3() // သောအားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 9);
    assertEquals(cp.toString(), 11, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_4() // အားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 12);
    assertEquals(cp.toString(), 14, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_5() // ဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 15);
    assertEquals(cp.toString(), 21, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_6() // က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 22);
    assertEquals(cp.toString(), 26, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_7() // ဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 27);
    assertEquals(cp.toString(), 29, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_8() // တော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 30);
    assertEquals(cp.toString(), 34, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_9() // ‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 35);
    assertEquals(cp.toString(), 43, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_10() // ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 44);
    assertEquals(cp.toString(), 47, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_11() // တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 48);
    assertEquals(cp.toString(), 51, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_12() // ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 52);
    assertEquals(cp.toString(), 58, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_13() // သုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 59);
    assertEquals(cp.toString(), 62, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_14() // ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 63);
    assertEquals(cp.toString(), 68, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_15() // ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 69);
    assertEquals(cp.toString(), 69, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_SYLLABLE,
                 cp.getBreakStatus());
  }
  public void test_lb_16() // ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 70);
    assertEquals(cp.toString(), 70, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_SYLLABLE,
                 cp.getBreakStatus());
  }
  public void test_lb_17() // ။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 71);
    assertEquals(cp.toString(), 71, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_EOL,
                 cp.getBreakStatus());
  }
  public void test_lb_18() // အင္ဂလိပ္‌
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_C, 0);
    assertEquals(cp.toString(), 2, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_SYLLABLE,
                 cp.getBreakStatus());
  }
  public void test_lb_19() // အင္ဂလိပ္‌
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_C, 3);
    assertEquals(cp.toString(), 3, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_20() // င‍္ဝေက္ရေး
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_D, 0);
    assertEquals(cp.toString(), 4, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lb_21() // ကုုိ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_E, 0);
    assertEquals(cp.toString(), 1, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_UNEXPECTED,
                 cp.getBreakStatus());
  }
  public void test_lb_22() // ဘာ လဲ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_F, 0);
    assertEquals(cp.toString(), 1, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_1,
                 cp.getBreakStatus());
  }
  public void test_lb_23() // ဘာ လဲ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_F, 2);
    assertEquals(cp.toString(), 2, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WHITESPACE,
                 cp.getBreakStatus());
  }
  
  // same tests with character array
  
  public void test_lba_0() 
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_A.toCharArray(), 0);
    assertEquals(cp.toString(), 6, cp.getEnd());
    assertEquals(cp.toString(), MyanmarParser.BK_EOL, 
                 cp.getBreakStatus());
  }
  
  public void test_lba_1() // ယုံက္ရည္‌သောအားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 0);
    assertEquals(cp.toString(), 2, cp.getEnd());
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  
  public void test_lba_2() // က္ရည္‌သောအားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 3);
    assertEquals(cp.toString(), 8, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }

  public void test_lba_3() // သောအားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 9);
    assertEquals(cp.toString(), 11, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_4() // အားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 12);
    assertEquals(cp.toString(), 14, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_5() // ဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 15);
    assertEquals(cp.toString(), 21, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_6() // က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 22);
    assertEquals(cp.toString(), 26, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_7() // ဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 27);
    assertEquals(cp.toString(), 29, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_8() // တော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 30);
    assertEquals(cp.toString(), 34, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_9() // ‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 35);
    assertEquals(cp.toString(), 43, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_10() // ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 44);
    assertEquals(cp.toString(), 47, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_11() // တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 48);
    assertEquals(cp.toString(), 51, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_12() // ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 52);
    assertEquals(cp.toString(), 58, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_13() // သုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 59);
    assertEquals(cp.toString(), 62, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_14() // ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 63);
    assertEquals(cp.toString(), 68, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_15() // ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 69);
    assertEquals(cp.toString(), 69, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_SYLLABLE,
                 cp.getBreakStatus());
  }
  public void test_lba_16() // ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 70);
    assertEquals(cp.toString(), 70, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_SYLLABLE,
                 cp.getBreakStatus());
  }
  public void test_lba_17() // ။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 71);
    assertEquals(cp.toString(), 71, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_EOL,
                 cp.getBreakStatus());
  }
  public void test_lba_18() // အင္ဂလိပ္‌
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_C.toCharArray(), 0);
    assertEquals(cp.toString(), 2, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_SYLLABLE,
                 cp.getBreakStatus());
  }
  public void test_lba_19() // အင္ဂလိပ္‌
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_C.toCharArray(), 3);
    assertEquals(cp.toString(), 3, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_20() // င‍္ဝေက္ရေး
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_D.toCharArray(), 0);
    assertEquals(cp.toString(), 4, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_2,
                 cp.getBreakStatus());
  }
  public void test_lba_21() // ကုုိ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_E.toCharArray(), 0);
    assertEquals(cp.toString(), 1, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_UNEXPECTED,
                 cp.getBreakStatus());
  }
  public void test_lba_22() // ဘာ လဲ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_F.toCharArray(), 0);
    assertEquals(cp.toString(), 1, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WEIGHT_1,
                 cp.getBreakStatus());
  }
  public void test_lba_23() // ဘာ လဲ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_F.toCharArray(), 2);
    assertEquals(cp.toString(), 2, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.BK_WHITESPACE,
                 cp.getBreakStatus());
  }
  
  
  public static Test suite() //
  {
    TestSuite suite = new TestSuite(LineBreakTest.class);
    TestSetup setup = new TestSetup( suite ) {
      protected void setUp() throws Exception 
      {
        myParser = new MyanmarParser();
      }
    };
    return setup;
  }
}
