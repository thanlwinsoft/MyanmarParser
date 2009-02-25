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
        validate("ၝဲာ", "ၝဲာ", Validator.Status.Valid);
    }
    
}
