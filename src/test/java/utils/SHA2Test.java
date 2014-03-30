package utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static utils.SHA2.getSHA2;

public class SHA2Test {

    private SHA2 sha2;
    public static final String TEST_PASS = "orSyTWBwe#5udHQj";
    public static final long TEST_DIG_PASS = 123456789;
    public static final String TEST_PASS_NULL = "";
    public static final long TEST_DIG_PASS_NULL = 0;

    @BeforeMethod
    public void setUp() throws Exception {
        sha2 = new SHA2();
    }

    @Test
    public void testSha2String()  throws Exception {
        String decodePass = sha2.getSHA2(TEST_PASS);
        String alternativeDecodePass = DigestUtils.sha256Hex(TEST_PASS);
        String decodePassNull = sha2.getSHA2(TEST_PASS_NULL);
        String alternativeDecodePassNull = DigestUtils.sha256Hex(TEST_PASS_NULL);

        Assert.assertEquals(decodePass, alternativeDecodePass);
        Assert.assertEquals(decodePassNull, alternativeDecodePassNull);
    }

    @Test
    public void testSha2Long()  throws Exception {
        String decodePass = SHA2.getSHA2(TEST_DIG_PASS);
        String alternativeDecodePass = DigestUtils.sha256Hex(String.valueOf(TEST_DIG_PASS));
        String decodePassNull = SHA2.getSHA2(TEST_DIG_PASS_NULL);
        String alternativeDecodePassNull = DigestUtils.sha256Hex(String.valueOf(TEST_DIG_PASS_NULL));

        Assert.assertEquals(decodePass, alternativeDecodePass);
        Assert.assertEquals(decodePassNull, alternativeDecodePassNull);
    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

}
