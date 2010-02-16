package org.thanlwinsoft.myanmar.junitTest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.thanlwinsoft.myanmar.Validator;
import org.thanlwinsoft.myanmar.MyanmarValidator;

/**
 * @author keith
 * Test the Myanmar Validator
 */
public class MyanmarValidatorTest
{
    private void correct(String in, String expectedOut)
    {
        validate(in, expectedOut, MyanmarValidator.Status.Corrected);
    }
    private void check(String in, Validator.Status expectedStatus)
    {
        validate(in, in, expectedStatus);
    }
    private void validate(String in, String expectedOut, Validator.Status expectedStatus)
    {
        Validator mv = new MyanmarValidator();
        BufferedReader inReader = new BufferedReader(new StringReader(in));
        StringWriter outWriter = new StringWriter();
        BufferedWriter bufferedOut = new BufferedWriter(outWriter); 
        Validator.Status status = mv.validate(inReader, 
                bufferedOut);
        try
        {
            inReader.close();
            bufferedOut.close();
            
            outWriter.flush();
            if (!outWriter.toString().equals(expectedOut))
            {
                fail("MyanmarValidator output " + outWriter.toString() + 
                        " but should have output " + expectedOut);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail("Exception during validate. " + e);
        }
        if (status != expectedStatus)
        {
            fail("Unexpected status " + status + " (expected " + 
                 expectedStatus + ")");
        }
    }
    
    /**
     * 
     */
    @Test
    public void testValidate01()
    {
        validate("မြန်မာ","မြန်မာ", Validator.Status.Valid);
    }
    /**
     * 
     */
    @Test
    public void testValidate02()
    {
        correct(" ေက"," ကေ");
    }
    /**
     * 
     */
    @Test
    public void testValidate03()
    {
        correct("၀င်း","ဝင်း");
    }
    /**
     * 
     */
    @Test
    public void testValidate04()
    {
        correct("၀ါ","ဝါ");
    }
    /**
     * e vowel before cons
     */
    @Test
    public void testValidate05()
    {
        correct("ေ၀","ဝေ");
    }
    /**
     * correct reordered upper/lower vowels
     */
    @Test
    public void testValidate06()
    {
        correct("ကုိ","ကို");
    }
    /**
     * convert to composed form
     */
    @Test
    public void testValidate07()
    {
        correct("ဦ","ဦ");
    }
    /**
     * change to cons rather than i
     */
    @Test
    public void testValidate08()
    {
        correct("ဥ်","ဉ်");
    }
    /**
     * i rather than cons
     */
    @Test
    public void testValidate09()
    {
        correct("ဥာ","ဉာ");
    }
    /**
     * wa ya medials out of order
     */
    @Test
    public void testValidate10()
    {
        // should we try to correct this?
        validate("ကွြ","ကွြ"/*"ကြွ"*/,  Validator.Status.Invalid);
    }
    /**
     * e, yayit in front of cons
     */
    @Test
    public void testValidate11()
    {
        correct("ေြကာင်း","ကြောင်း");
    }
    /**
    */
    @Test
    public void testValidate12()
    {
        correct("၀တ္တု","ဝတ္တု");
    }
    /**
     * e infront of ya hato
     */
    @Test
    public void testValidate13()
    {
        correct("ေရှ့","ရှေ့");
    }
    /**
     * contraction
     */
    @Test
    public void testValidate14()
    {
        correct("ကျွနု်ပ်", "ကျွန်ုပ်");
    }
    /**
     * double tha
     */
    @Test
    public void testValidate15()
    {
        correct("ထုတ််", "ထုတ်");
    }
    /**
     * erroneous 4
     */
    @Test
    public void testValidate16()
    {
        correct("၄င်း",  "၎င်း");
    }
    /**
     * erroneous stack with i
     */
    @Test
    public void testValidate17()
    {
        correct("ပဥ္စ",  "ပဉ္စ");
    }
    /**
     * tha gyi
     */
    @Test
    public void testValidate18()
    {
        validate("ဒုဿီ", "ဒုဿီ", Validator.Status.Valid);
    }
    /**
     * i an
     */
    @Test
    public void testValidate19()
    {
        validate("တိံ", "တိံ", Validator.Status.Valid);
    }
    /**
     * 
     */
    @Test
    public void testValidate20()
    {
        validate("ကင့်","ကင့်", Validator.Status.Valid);
    }
    /**
     * 
     */
    @Test
    public void testValidate21()
    {
        validate("ကော်","ကော်", Validator.Status.Valid);
    }
    /**
     * 
     */
    @Test
    public void testValidate22()
    {
        validate("ခေါ်","ခေါ်", Validator.Status.Valid);
    }
    /**
     * Kinzi
     */
    @Test
    public void testValidate23()
    {
        validate("အင်္ဂလိပ်","အင်္ဂလိပ်", Validator.Status.Valid);
    }
    /**
     * 
     */
    @Test
    public void testValidate24()
    {
        validate("ခြသေ့င်္","ခြသေ့င်္", Validator.Status.Invalid);
    }
    /**
     * 
     */
    @Test
    public void testValidate25()
    {
        validate("သချာင်္","သချာင်္", Validator.Status.Invalid);
    }
    /**
     * 
     */
    @Test
    public void testValidate26()
    {
        validate("ရှိှိန်","ရှိှိန်", Validator.Status.Invalid);
    }
    /**
     * 
     */
    @Test
    public void testValidate27()
    {
    	correct("ဗျဉ္ဇ္ဇ","ဗျဉ္ဇ");
    }
    /**
     * 
     */
    @Test
    public void testValidateShan1()
    {
        validate("ဢွႆႇ", "ဢွႆႇ", Validator.Status.Valid);
    }
    /**
     * 
     */
    @Test
    public void testValidateShan2()
    {
        validate("ႁိူဝ်ႉ", "ႁိူဝ်ႉ", Validator.Status.Valid);
    }
    /**
     * note order is different to Burmese contractions
     */
    @Test
    public void testValidateShan3()
    {
    	validate("ၸႂ်",  "ၸႂ်", Validator.Status.Valid);
    }
    /**
     * 
     */
    @Test
    public void testValidateShan4()
    {
        validate("ၶေႃး", "ၶေႃး", Validator.Status.Valid);
    }
    /**
     * note order is different to Burmese contractions
     */
    @Test
    public void testValidateShan5()
    {
        correct("ၸ်ႂဢ်ႂ", "ၸႂ်ဢႂ်");
    } 

    /**
     * note order is different to Burmese contractions
     */
    @Test
    public void testValidateShan6()
    {
        validate("ဢႂ်ႇ",  "ဢႂ်ႇ", Validator.Status.Valid);
    }
    /**
     * note order is different to Burmese contractions
     */
    @Test
    public void testValidateShan7()
    {
        correct("ၶႆႂ",  "ၶႂႆ");
    }
    /**
     * 
     */
    @Test
    public void testValidateShan8()
    {
        validate("ၸေႃးငႅမ်ႈ", "ၸေႃးငႅမ်ႈ", Validator.Status.Valid);
    }
    /**
     * 
     */
    @Test
    public void testValidateKayah()
    {
        validate("ယၴၤဆၳူ", "ယၴၤဆၳူ", Validator.Status.Valid);
    }
    /**
     * reorder upper vowel, medial
     */
    @Test
    public void testValidateMon1()
    {
        correct("အလဵှု",  "အလှဵု");
    }
    /**
     * 
     */
    @Test
    public void testValidateMon2()
    {
        check("ၝဲာ", Validator.Status.Valid);
    }
    /**
     * Mon medial h, different order to contractions
     */
    @Test
    public void testValidateMon3()
    {
        correct("ခဏ်ှ", "ခဏှ်");
    }
    /**
     * Mon medial h, different order to contractions
     */
    @Test
    public void testValidateMon4()
    {
        check("အောှ်", Validator.Status.Valid);
    }
    /**
     * Mon medial h, different order to contractions
     */
    @Test
    public void testValidateMon5()
    {
        check("အာဲ", Validator.Status.Valid);
    }
    

    
    /**
     * Sgaw Karen
     */
    @Test
    public void testValidateSgaw1()
    {
        //correct("လဲၣ့်", "လဲ့ၣ်");
    	check("လဲၣ့်", Validator.Status.Invalid);
    }
    /**
     * Sgaw Karen
     */
    @Test
    public void testValidateSgaw2()
    {
        check("ကၢၢ်", Validator.Status.Valid);
    }
    /**
     * Sgaw Karen
     */
    @Test
    public void testValidateSgaw3()
    {
        check("အၢ်", Validator.Status.Valid);
    }
    /**
     * Sgaw Karen
     */
    @Test
    public void testValidateSgaw4()
    {
        check("ဧူၢ", Validator.Status.Valid);
    }
    
    /**
     * Pwo Karen
     */
    @Test
    public void testValidatePwo()
    {
        check("ၥ့ၭ", Validator.Status.Valid);
    }
    
    /**
     * Pa-O
     */
    @Test
    public void testValidatePaO()
    {
    	check("သချာင်္ႏ", Validator.Status.Invalid);
    }
    
    /**
     * Khamti
     */
    @Test
    public void testValidateKhamti1()
    {
    	correct("တွꩫ်ꩰႇ", "တွꩫ်ႇꩰ");
    }
    /**
     * Khamti
     */
    @Test
    public void testValidateKhamti2()
    {
    	
    	check("ꩫဲၢႈꩰ၊ ", Validator.Status.Valid);
    }
    /**
     * Khamti
     */
    @Test
    public void testValidateKhamti3()
    {
    	correct("လံု", "လုံ");
    }
    /**
     * Khamti
     */
    @Test
    public void testValidateKhamti4()
    {
    	check("ꩢႅ် ", Validator.Status.Invalid);
    }
    /**
     * Khamti
     */
    @Test
    public void testValidateKhamti5()
    {
    	check("ꩭၢဲ", Validator.Status.Valid);
    }
    
    /**
     * Invalid sequence
     */
    @Test
    public void testValidateInvalid1()
    {
    	correct("ကံံ", "ကံ");
    }
    /**
     * Invalid sequence
     */
    @Test
    public void testValidateInvalid2()
    {
    	check("က\u1036\u1086", Validator.Status.Invalid);
    }
    /**
     * Invalid sequence
     */
    @Test
    public void testValidateInvalid3()
    {
    	check("က\u1086\u1036", Validator.Status.Invalid);
    }
    /**
     * Pali
     */
    @Test
    public void testPali1()
    {
    	check("\u101B\u103A\u1039\u1010\u1039\u1010", Validator.Status.Valid);
    }
    /**
     * Pali
     */
    @Test
    public void testPali2()
    {
    	check("\u1010\u1039\u101E\u1039\u1014", Validator.Status.Valid);
    }
}
