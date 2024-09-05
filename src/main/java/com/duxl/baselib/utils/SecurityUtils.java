package com.duxl.baselib.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * 加解密工具类：
 * 支持：AES、DES、RAS、MD5、SHA-1、SHA-256、SHA-512
 */
public class SecurityUtils {

    public static void main(String[] args) throws Exception {
        AES.test();
        DES.test();
        RAS.test();
        Digest.test();
    }

    /**
     * AES对称加解密（所谓对称就是加密和解密的密钥是同一个）
     */
    public static class AES {
        public static void test() throws Exception {
            SecretKey secretKey = gencrateSecretKey();
            IvParameterSpec iv = generateIV();

            System.out.println("--------- AES加解密 ---------");
            // 原始数据
            String originStr = "hello,world";
            byte[] originData = originStr.getBytes(Charset.defaultCharset());
            System.out.println("原字符串：" + originStr);
            // 加密
            byte[] encryptResult = encrypt(originData, secretKey, iv);
            System.out.println("加密后：" + Arrays.toString(encryptResult));
            // 解密
            byte[] decryptResult = decrypt(encryptResult, secretKey, iv);
            System.out.println("解密后：" + new String(decryptResult));

        }

        private static final String transformation = "AES/CBC/PKCS5Padding";

        /**
         * aes加密（对称加密）
         *
         * @param data
         * @return
         */
        public static byte[] encrypt(byte[] data, SecretKey secretKey, IvParameterSpec iv) throws Exception {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return cipher.doFinal(data);
        }

        /**
         * @param data
         * @param secretKey
         * @param iv
         * @return
         * @throws Exception
         */
        public static byte[] decrypt(byte[] data, SecretKey secretKey, IvParameterSpec iv) throws Exception {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return cipher.doFinal(data);
        }

        /**
         * 生成AES密钥
         *
         * @return
         * @throws Exception
         */
        public static SecretKey gencrateSecretKey() throws Exception {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128); // AES使用128位密钥
            return keyGenerator.generateKey();
        }

        /**
         * 生成随机的IV（初始化向量）
         *
         * @return
         */
        public static IvParameterSpec generateIV() {
            byte[] iv = new byte[16]; // AES的IV位16字节
            new SecureRandom().nextBytes(iv);
            return new IvParameterSpec(iv);
        }

    }

    /**
     * DES对称加解密（所谓对称就是加密和解密的密钥是同一个）
     */
    public static class DES {
        public static void test() throws Exception {
            SecretKey secretKey = gencrateSecretKey();
            IvParameterSpec iv = generateIV();

            System.out.println("--------- DES加解密 ---------");
            // 原始数据
            String originStr = "我是DES原字符串";
            byte[] originData = originStr.getBytes(Charset.defaultCharset());
            System.out.println("原字符串：" + originStr);
            // 加密
            byte[] encryptResult = encrypt(originData, secretKey, iv);
            System.out.println("加密后：" + Arrays.toString(encryptResult));
            // 解密
            byte[] decryptResult = decrypt(encryptResult, secretKey, iv);
            System.out.println("解密后：" + new String(decryptResult));

        }

        private static final String transformation = "DES/CBC/PKCS5Padding";

        /**
         * aes加密（对称加密）
         *
         * @param data
         * @return
         */
        public static byte[] encrypt(byte[] data, SecretKey secretKey, IvParameterSpec iv) throws Exception {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return cipher.doFinal(data);
        }

        /**
         * @param data
         * @param secretKey
         * @param iv
         * @return
         * @throws Exception
         */
        public static byte[] decrypt(byte[] data, SecretKey secretKey, IvParameterSpec iv) throws Exception {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            return cipher.doFinal(data);
        }

        /**
         * 生成DES密钥
         *
         * @return
         * @throws Exception
         */
        public static SecretKey gencrateSecretKey() throws Exception {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            keyGenerator.init(56); // DES使用56位密钥
            return keyGenerator.generateKey();
        }

        /**
         * 生成随机的IV（初始化向量）
         *
         * @return
         */
        public static IvParameterSpec generateIV() {
            byte[] iv = new byte[8]; // DES的IV位8字节
            new SecureRandom().nextBytes(iv);
            return new IvParameterSpec(iv);
        }

    }

    /**
     * RAS非对称加密（公钥加密私钥解密，反之私钥加密公钥解密）
     */
    public static class RAS {

        public static void test() throws Exception {
            // 生成密钥对
            KeyPair keyPair = generateKeyPair();
            Key publicKey = keyPair.getPublic();
            Key privateKey = keyPair.getPrivate();

            System.out.println("---------RAS非对称加密---------");
            // 原始字符串
            String originStr = "hello, RSA";
            System.out.println("原始字符串: " + originStr);
            // 加密(公钥加密)
            byte[] encryptedData = encrypt(originStr.getBytes(StandardCharsets.UTF_8), publicKey);
            System.out.println("加密后的字节：" + Arrays.toString(encryptedData));
            // 解密（私钥解密）
            byte[] decryptedData = decrypt(encryptedData, privateKey);
            System.out.println("解密后：" + new String(decryptedData));
            // 将密钥转换为字符串并还原
            String publicKeyStr = keyToString(publicKey);
            String privateKeyStr = keyToString(privateKey);
            System.out.println("公钥：" + publicKeyStr);
            System.out.println("私钥：" + privateKeyStr);
            Key publicKey2 = stringToKey(publicKeyStr, true);
            Key privateKey2 = stringToKey(privateKeyStr, false);
            System.out.println("还原公钥后是否相等：" + Arrays.equals(publicKey.getEncoded(), publicKey2.getEncoded()));
            System.out.println("还原私钥后是否相等：" + Arrays.equals(privateKey.getEncoded(), privateKey2.getEncoded()));

        }

        private static final String transformation = "RSA/ECB/PKCS1Padding";

        // 加密
        public static byte[] encrypt(byte[] data, Key key) throws Exception {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        }

        // 解密
        public static byte[] decrypt(byte[] data, Key key) throws Exception {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        }

        // 生成密钥对
        public static KeyPair generateKeyPair() throws Exception {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        }

        // 将密钥转成字符串
        public static String keyToString(Key key) {
            return Base64.getEncoder().encodeToString(key.getEncoded());
        }

        /**
         * 将字符串还原成密钥
         *
         * @param key      字符串密钥
         * @param isPublic 是否公钥
         * @return
         * @throws Exception
         */
        public static Key stringToKey(String key, boolean isPublic) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            if (isPublic) {
                return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
            } else {
                return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
            }
        }
    }

    /**
     * 将字节数组转化成16进制的字符串
     *
     * @param data
     * @return
     */
    public static String toHexString(byte[] data) {
        if (data == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte d : data) {
            String s = Integer.toHexString(0xff & d);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * 数字摘要
     */
    public static class Digest {

        public static void test() throws Exception {
            System.out.println("------数字摘要------");
            String originStr = "hello, Digest";
            byte[] originData = originStr.getBytes(StandardCharsets.UTF_8);
            System.out.println("原字符串：" + originStr);
            System.out.println("原字节数据：" + Arrays.toString(originData));

            System.out.println("md5=" + md5(originData));
            System.out.println("sha1=" + sha1(originData));
            System.out.println("sha256=" + sha256(originData));
            System.out.println("sha512=" + sha512(originData));

        }

        // 消息摘要算法第五版
        public static String md5(byte[] input) throws NoSuchAlgorithmException {
            // 获取算法实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算
            byte[] messageDigest = md.digest(input);
            System.out.print("MD5 hash (16 bytes): ");
            for (byte b : messageDigest) {
                System.out.printf("%02x", b);
            }
            System.out.println();
            // 将字节数组转换为十六进制字符串
            return toHexString(messageDigest);
        }

        public static String sha1(byte[] input) throws NoSuchAlgorithmException {
            // 获取算法实例
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 计算
            byte[] messageDigest = md.digest(input);
            // 将字节数组转换为十六进制字符串
            return toHexString(messageDigest);
        }

        public static String sha256(byte[] input) throws NoSuchAlgorithmException {
            // 获取算法实例
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // 计算
            byte[] messageDigest = md.digest(input);
            // 将字节数组转换为十六进制字符串
            return toHexString(messageDigest);
        }

        public static String sha512(byte[] input) throws NoSuchAlgorithmException {
            // 获取算法实例
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            // 计算
            byte[] messageDigest = md.digest(input);
            // 将字节数组转换为十六进制字符串
            return toHexString(messageDigest);
        }
    }

}
