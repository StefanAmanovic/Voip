package konferencija;

/**
 * Created by SilentStorm1 on 4.9.2017..
 */
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class AES {
    private static String IV = "AAAAAAAAAAAAAAAA";
    private static String encryptionKey = "0123456789abcdefgh93405345qrswxyz";

    //to do randomize IV
    static byte[] encrypt(byte[] plainData, int offset, int length) throws Exception
    {Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
    cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
    return cipher.doFinal(plainData, offset, length);
}

    static byte[] decrypt(byte[] kriptovan_zvuk) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
        return cipher.doFinal(kriptovan_zvuk);
    }
    public static byte[] encrypt1(byte[] plainData, int offset, int length) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding", "SunJCE");//CBC
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return cipher.doFinal(plainData, offset, length);
    }

    public static byte[] decrypt1(byte[] cipherSound, int offset, int length) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "SunJCE");//CBC
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return cipher.doFinal(cipherSound, offset, length);
    }
}
    //SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
    //byte[] iv = new byte[cipher.getBlockSize()];
    //randomSecureRandom.nextBytes(iv);

      //  IvParameterSpec ivParams = new IvParameterSpec(iv);
