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
    private MyanmarValidator.Status validate(String in, String expectedOut)
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
        return status;
    }
    
    @Test
    public void testValidate01()
    {
        validate("မြန်မာ","မြန်မာ");
    }
    @Test
    public void testValidate02()
    {
        validate(" ေက"," ကေ");
    }
    @Test
    public void testValidate03()
    {
        validate("၀င်း","ဝင်း");
    }
    @Test
    public void testValidate04()
    {
        validate("၀ါ","ဝါ");
    }
    @Test
    public void testValidate05()
    {
        validate("ေ၀","ဝေ");
    }
    @Test
    public void testValidate06()
    {
        validate("ကုိ","ကို");
    }
    @Test
    public void testValidate07()
    {
        validate("ဦ","ဦ");
    }
    @Test
    public void testValidate08()
    {
        validate("ဥ်","ဉ်");
    }
    @Test
    public void testValidate09()
    {
        validate("ဥာ","ဉာ");
    }
    @Test
    public void testValidate10()
    {
        // should we try to correct this?
        if (validate("ကွြ","ကွြ"/*"ကြွ"*/) != MyanmarValidator.Status.Invalid)
            fail("Failed to detect wrong medial order.");
    }
}
