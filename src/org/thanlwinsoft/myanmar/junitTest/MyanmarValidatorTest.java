package org.thanlwinsoft.myanmar.junitTest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.thanlwinsoft.myanmar.MyanmarValidator;

public class MyanmarValidatorTest
{
    private void correct(String in, String expectedOut)
    {
        validate(in, expectedOut, MyanmarValidator.Status.Corrected);
    }
    private void validate(String in, String expectedOut, MyanmarValidator.Status expectedStatus)
    {
        MyanmarValidator mv = new MyanmarValidator();
        BufferedReader inReader = new BufferedReader(new StringReader(in));
        StringWriter outWriter = new StringWriter();
        BufferedWriter bufferedOut = new BufferedWriter(outWriter); 
        MyanmarValidator.Status status = mv.validate(inReader, 
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
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail("Exception during validate. " + e);
        }
        if (status != expectedStatus)
        {
            fail("Unexpected status " + status + " (expected " + 
                 expectedStatus + ")");
        }
    }
    
    @Test
    public void testValidate01()
    {
        validate("မြန်မာ","မြန်မာ", MyanmarValidator.Status.Valid);
    }
    @Test
    public void testValidate02()
    {
        correct(" ေက"," ကေ");
    }
    @Test
    public void testValidate03()
    {
        correct("၀င်း","ဝင်း");
    }
    @Test
    public void testValidate04()
    {
        correct("၀ါ","ဝါ");
    }
    @Test
    public void testValidate05()
    {
        correct("ေ၀","ဝေ");
    }
    @Test
    public void testValidate06()
    {
        correct("ကုိ","ကို");
    }
    @Test
    public void testValidate07()
    {
        correct("ဦ","ဦ");
    }
    @Test
    public void testValidate08()
    {
        correct("ဥ်","ဉ်");
    }
    @Test
    public void testValidate09()
    {
        correct("ဥာ","ဉာ");
    }
    @Test
    public void testValidate10()
    {
        // should we try to correct this?
        validate("ကွြ","ကွြ"/*"ကြွ"*/,  MyanmarValidator.Status.Invalid);
    }
    @Test
    public void testValidate11()
    {
        correct("ေြကာင်း","ကြောင်း");
    }
    @Test
    public void testValidate12()
    {
        correct("၀တ္တု","ဝတ္တု");
    }
    @Test
    public void testValidate13()
    {
        correct("ေရှ့","ရှေ့");
    }
    @Test
    public void testValidate14()
    {
        correct("ကျွနု်ပ်", "ကျွန်ုပ်");
    }
    @Test
    public void testValidate15()
    {
        correct("ထုတ််", "ထုတ်");
    }
    @Test
    public void testValidate16()
    {
        correct("၄င်း",  "၎င်း");
    }
    @Test
    public void testValidate17()
    {
        correct("ပဥ္စ",  "ပဉ္စ");
    }
    @Test
    public void testValidate18()
    {
        validate("ဒုဿီ", "ဒုဿီ", MyanmarValidator.Status.Valid);
    }
}
