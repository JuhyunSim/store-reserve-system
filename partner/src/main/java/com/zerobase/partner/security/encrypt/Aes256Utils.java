package com.zerobase.partner.security.encrypt;

import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class Aes256Utils {
    public static final String alg = "AES/CBC/PKCS5Padding";

    private SecretKey getSigningKey() {
        String secretKey = "YWJjZGVmZ2hpamFiY2RlZmdoaWphYmNkZWZnaGlqMTI=";
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return new SecretKeySpec(keyBytes, "AES");
    }

    private final static IvParameterSpec ivParameterSpec = generateIv();

    public String encrypt(String input)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {


        Cipher cipher = Cipher.getInstance(alg);
        log.info("encrypt ivParameterSpec: {}", ivParameterSpec);
        cipher.init(Cipher.ENCRYPT_MODE, getSigningKey(), ivParameterSpec);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        log.info ("encrpyted cipherText: {}", Base64.getEncoder().encodeToString(cipherText));
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    public String decrypt(String cipherText)
            throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(alg);
        log.info("decrypt cipherText: {}", cipherText);
        log.info("decrypt secretKey: {}", getSigningKey());
        log.info("decrypt ivParameterSpec: {}", ivParameterSpec);
        cipher.init(Cipher.DECRYPT_MODE, getSigningKey(), ivParameterSpec);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }


    private static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

}
