package org.thanlwinsoft.myanmar.junitTest;

import junit.framework.*;
import junit.extensions.TestSetup;
import org.thanlwinsoft.myanmar.MyanmarParser;
import org.thanlwinsoft.myanmar.MyanmarParser.ClusterProperties;

/**
 * Test line break positions
 * @author keith
 *
 */
public class LineBreakTest extends TestCase
{  
	static MyanmarParser myParser = null;
  final String STRING_A = "ကောင်း";
  final String STRING_B = "ယုံကြည်သောအားဖြင့်ကျေးဇူးတော်ကြောင့်ကယ်တင်ခြင်းသို့ရောက်ရ၏။";
  final String STRING_C = "အင်္ဂလိပ်";
  final String STRING_D = "ငွေကြေး";
  final String STRING_E = "ကုုိ";
  final String STRING_F = "ဘာ လဲ။";
  final String STRING_G = "(ရေ)";
  final String STRING_H = "လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့လီၫ.";// Pwo Karen
  final String STRING_I = "ဟ့အဒီပုၢ်ဝဲၢ်န့ၣ်";//Sgaw Karen
  final String STRING_J = "ၸႂ်ဢႂ်";

  /** */
  public void test_lb_0()
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_A, 0);
    assertEquals(cp.toString(), 6, cp.getEnd());
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL, 
                 cp.getBreakStatus());
  }
  
  /** */
  public void test_lb_1() // ယုံကြည်သောအားဖြင့်ကျေးဇူးတော်ကြောင့်ကယ်တင်ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 0);
    assertEquals(cp.toString(), 3, cp.getEnd());
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  
  /** */
  public void test_lb_2() // ကြည်သောအားဖြင့်ကျေးဇူးတော်ကြောင့်ကယ်တင်ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 3);
    assertEquals(cp.toString(), 7, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }

  /** */
  public void test_lb_3() // သောအားဖြင့်ကျေးဇူးတော်ကြောင့်ကယ်တင်ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 7);
    assertEquals(cp.toString(), 10, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_4() // အားဖြင့်ကျေးဇူးတော်ကြောင့်ကယ်တင်ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 10);
    assertEquals(cp.toString(), 13, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_5() // ဖြင့်ကျေးဇူးတော်ကြောင့်ကယ်တင်ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 13);
    assertEquals(cp.toString(), 18, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_6() // ကျေးဇူးတော်ကြောင့်ကယ်တင်ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 18);
    assertEquals(cp.toString(), 22, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_7() // ဇူးတော်ကြောင့်ကယ်တင်ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 22);
    assertEquals(cp.toString(), 25, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_8() // တော်ကြောင့်ကယ်တင်ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 25);
    assertEquals(cp.toString(), 29, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_9() // ‌ကြောင့်ကယ်တင်ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 29);
    assertEquals(cp.toString(), 36, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_10() // ‌ကယ်တင်ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 36);
    assertEquals(cp.toString(), 39, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_11() // တင်ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 39);
    assertEquals(cp.toString(), 42, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_12() // ခြင်းသို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 42);
    assertEquals(cp.toString(), 47, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_13() // သို့ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 47);
    assertEquals(cp.toString(), 51, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_14() // ရောက်ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 51);
    assertEquals(cp.toString(), 56, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_15() // ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 56);
    assertEquals(cp.toString(), 57, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_16() // ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 57);
    assertEquals(cp.toString(), 58, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_PUNCTUATION,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_17() // ။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B, 58);
    assertEquals(cp.toString(), 59, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_18() // အင်္ဂလိပ်
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_C, 0);
    assertEquals(cp.toString(), 5, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_19() // အင်္ဂလိပ်
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_C, 5);
    assertEquals(cp.toString(), 9, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_20() // ငွေကြေး
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_D, 0);
    assertEquals(cp.toString(), 3, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_21() // ကုုိ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_E, 0);
    assertEquals(cp.toString(), 4, cp.getEnd() );
    // this is illegal, but we no longer use this code for validation, so it now breaks at 4
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_22() // ဘာ လဲ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_F, 0);
    assertEquals(cp.toString(), 2, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_WORD_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_23() // ဘာ လဲ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_F, 2);
    assertEquals(cp.toString(), 3, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_WORD_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lb_24() // ဘာ လဲ။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_F, 3);
    assertEquals(cp.toString(), 5, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_PUNCTUATION,
                 cp.getBreakStatus());
  }
  
  // same tests with character array
  
  /** */
  public void test_lba_0() 
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_A.toCharArray(), 0);
    assertEquals(cp.toString(), 6, cp.getEnd());
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL, 
                 cp.getBreakStatus());
  }
  
  /** */
  public void test_lba_1() // ယုံက္ရည္‌သောအားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 0);
    assertEquals(cp.toString(), 3, cp.getEnd());
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  
  /** */
  public void test_lba_2() // က္ရည္‌သောအားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 3);
    assertEquals(cp.toString(), 7, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }

  /** */
  public void test_lba_3() // သောအားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 7);
    assertEquals(cp.toString(), 10, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_4() // အားဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 10);
    assertEquals(cp.toString(), 13, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_5() // ဖ္ရင္‌့က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 13);
    assertEquals(cp.toString(), 18, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_6() // က္ယေးဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 18);
    assertEquals(cp.toString(), 22, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_7() // ဇူးတော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 22);
    assertEquals(cp.toString(), 25, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_8() // တော္‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 25);
    assertEquals(cp.toString(), 29, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_9() // ‌က္ရောင္‌့ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 29);
    assertEquals(cp.toString(), 36, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_10() // ကယ္‌တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 36);
    assertEquals(cp.toString(), 39, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_11() // တင္‌ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 39);
    assertEquals(cp.toString(), 42, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_12() // ခ္ရင္‌းသုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 42);
    assertEquals(cp.toString(), 47, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_13() // သုိ့ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 47);
    assertEquals(cp.toString(), 51, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_14() // ရောက္‌ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 51);
    assertEquals(cp.toString(), 56, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_15() // ရ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 56);
    assertEquals(cp.toString(), 57, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_16() // ၏။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 57);
    assertEquals(cp.toString(), 58, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_PUNCTUATION,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_17() // ။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_B.toCharArray(), 58);
    assertEquals(cp.toString(), 59, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_18() // အင်္ဂလိပ်"
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_C.toCharArray(), 0);
    assertEquals(cp.toString(), 5, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_19() // အင်္ဂလိပ်"
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_C.toCharArray(), 5);
    assertEquals(cp.toString(), 9, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_20() // "ငွေကြေး";
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_D.toCharArray(), 0);
    assertEquals(cp.toString(), 3, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_21() // ကုုိ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_E.toCharArray(), 0);
    assertEquals(cp.toString(), 4, cp.getEnd() );
    // actually illegal at 2, but we don't check that anymore
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_22() // ဘာ လဲ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_F.toCharArray(), 0);
    assertEquals(cp.toString(), 2, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_WORD_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_23() // ဘာ လဲ
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_F.toCharArray(), 2);
    assertEquals(cp.toString(), 3, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_WORD_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_24() // ဘာ လဲ။
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_F.toCharArray(), 3);
    assertEquals(cp.toString(), 5, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_PUNCTUATION,
                 cp.getBreakStatus());
  }
  
  /** */
  public void test_lba_25() // (
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_G.toCharArray(), 0);
    assertEquals(cp.toString(), 1, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_WORD_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_26() //
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_G.toCharArray(), 1);
    assertEquals(cp.toString(), 3, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_WORD_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_27() // )
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_G.toCharArray(), 3);
    assertEquals(cp.toString(), 4, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL,
                 cp.getBreakStatus());
  }
 
  /** */
  public void test_lba_pwo_1() //      လံၬ|ၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 0);
    assertEquals(cp.toString(), 3, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_2() //      လံၬၥီၪ့|ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 3);
    assertEquals(cp.toString(), 7, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_3() //      လံၬၥီၪ့ထဲၩ့|ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 7);
    assertEquals(cp.toString(), 11, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_4() //      လံၬၥီၪ့ထဲၩ့ဆၧ|လၧစယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 11);
    assertEquals(cp.toString(), 13, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_5() //      လံၬၥီၪ့ထဲၩ့ဆၧလၧ|စယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 13);
    assertEquals(cp.toString(), 15, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_6() //      လံၬၥီၪ့ထဲၩ့ဆၧလၧစ|ယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 15);
    assertEquals(cp.toString(), 16, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_7() //      လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီ|ဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 16);
    assertEquals(cp.toString(), 18, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_8() //      လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့|ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 18);
    assertEquals(cp.toString(), 21, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_9() //      လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫ|အခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 21);
    assertEquals(cp.toString(), 25, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_9b() //     လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫ|အခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 25);
    assertEquals(cp.toString(), 28, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_10() //     လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫအ|ခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 28);
    assertEquals(cp.toString(), 29, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_11() //     လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫအခၪ့|ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 29);
    assertEquals(cp.toString(), 32, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_12() //     လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭ|လဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 32);
    assertEquals(cp.toString(), 35, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_13() //      လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလ|ဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 35);
    assertEquals(cp.toString(), 36, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_14() //     လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့|လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 36);
    assertEquals(cp.toString(), 40, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_15() //     လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့လီၫ|.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 40);
    assertEquals(cp.toString(), 43, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_WORD_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_pwo_16() //      လံၬၥီၪ့ထဲၩ့ဆၧလၧစယီဟၫ့ကွ့ၭဝ့ၫအခၪ့ထံၭလဘံၪ့လီၫ.
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_H.toCharArray(), 43);
    assertEquals(cp.toString(), 44, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_ksw_01() //     ဟ့|အဒီပုၢ်ဝဲၢ်န့ၣ်      
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_I.toCharArray(), 0);
    assertEquals(cp.toString(), 2, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  
  /** */
  public void test_lba_ksw_02() //     ဟ့အ|ဒီပုၢ်ဝဲၢ်န့ၣ်      
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_I.toCharArray(), 2);
    assertEquals(cp.toString(), 3, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_ksw_03() //     ဟ့အဒီ|ပုၢ်ဝဲၢ်န့ၣ်      
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_I.toCharArray(), 3);
    assertEquals(cp.toString(), 5, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_ksw_04() //     ဟ့အဒီပုၢ်|ဝဲၢ်န့ၣ်      
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_I.toCharArray(), 5);
    assertEquals(cp.toString(), 9, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_ksw_05() //     ဟ့အဒီပုၢ်ဝဲၢ်|န့ၣ်      
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_I.toCharArray(), 9);
    assertEquals(cp.toString(), 13, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /** */
  public void test_lba_ksw_06() //     ဟ့အဒီပုၢ်ဝဲၢ်န့ၣ်|
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_I.toCharArray(), 13);
    assertEquals(cp.toString(), 17, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL,
                 cp.getBreakStatus());
  }
  /**    ၸႂ်|ဢႂ် */
  public void test_lba_shn_01()
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_J.toCharArray(), 0);
    assertEquals(cp.toString(), 3, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_SYL_BREAK,
                 cp.getBreakStatus());
  }
  /**    ၸႂ်ဢႂ် */
  public void test_lba_shn_02()
  {
    ClusterProperties cp = myParser.getNextSyllable(STRING_J.toCharArray(), 3);
    assertEquals(cp.toString(), 6, cp.getEnd() );
    assertEquals(cp.toString(), MyanmarParser.MyPairStatus.MY_PAIR_EOL,
                 cp.getBreakStatus());
  }
  /** run the test suite 
   * @return test setup 
   */
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
