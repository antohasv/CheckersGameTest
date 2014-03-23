package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA2 {

    public static final String SHA = "SHA-256";

    public static String getSHA2(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(SHA);
        } catch (NoSuchAlgorithmException ignor) {
            return "";
        }
        md.update(password.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String getSHA2(long password) {
        return getSHA2(String.valueOf(password));
    }
}
