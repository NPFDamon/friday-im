package com.friday.common.utils;

import com.friday.common.constant.Constants;
import com.friday.common.exception.TokenException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Copyright (C),Damon
 *
 * @Description: DES加解密
 * @Author: Damon(npf)
 * @Date: 2020-05-11:14:26
 */
@Slf4j
public class DesUtil {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static SecretKeyFactory secretKeyFactory;
    private static KeyGenerator keyGenerator;

    static {
        try {
            secretKeyFactory = SecretKeyFactory.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
            keyGenerator = KeyGenerator.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
            keyGenerator.init(secureRandom);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public static byte[] encrypt(byte[] keyWord, byte[] data) throws TokenException {
        try {
            DESKeySpec desKeySpec = new DESKeySpec(keyWord);
            SecretKey key = secretKeyFactory.generateSecret(desKeySpec);

            Cipher cipher = Cipher.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, secureRandom);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("Token Encrypt fail ...");
            e.printStackTrace();
            throw new TokenException(e.getMessage());
        }
    }

    public static byte[] decrypt(byte[] keyWord, byte[] encryptData) throws TokenException {
        try {
            DESKeySpec desKeySpec = new DESKeySpec(keyWord);
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);

            Cipher cipher = Cipher.getInstance(Constants.DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, secureRandom);
            return cipher.doFinal(encryptData);
        } catch (Exception e) {
            log.error("Token Decrypt fail ...");
            e.printStackTrace();
            throw new TokenException(e.getMessage());
        }
    }
}
