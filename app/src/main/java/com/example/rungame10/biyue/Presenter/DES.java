package com.example.rungame10.biyue.Presenter;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DES {
    //密码加密解密算法

    public static String KEY = "biyuesdk";

    public static Key getKey(String keyString) {
        byte[] keyStringByte = keyString.getBytes();
        byte[] keyByte = new byte[8];
        for(int i = 0; i<keyStringByte.length && i < keyByte.length; i++) {
            keyByte[i] = keyStringByte[i];
        }
        Key key = new SecretKeySpec(keyByte,"DES");
        return key;
    }

    public static String byteArr2HexStr(byte[] bytes) throws Exception {
        StringBuffer sb = new StringBuffer(bytes.length*2);
        for(int i = 0; i < bytes.length; i++){
            if((bytes[i] & 0xFF) < 0x10)
                sb.append("0");
            sb.append(Integer.toHexString(bytes[i]&0xFF));
        }
        return sb.toString();
    }

    public static byte[] hexStr2ByteArr(String str) throws Exception {

        byte[] bytes = str.getBytes();

        int len = bytes.length;
        byte[] arr = new byte[len/2];
        for(int i = 0; i < len; i=i+2) {
            String tmp = new String(bytes,i,2);
            arr[i/2] = (byte) Integer.parseInt(tmp,16);
        }
        return arr;
    }


    public static String getDES(String val, String key) throws Exception {
        if(val == null || key == null)
            return null;

        Cipher encryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE,getKey(key));
        byte[] cipherByte = encryptCipher.doFinal(val.getBytes());

        return byteArr2HexStr(cipherByte);
    }

    public static String getDESOri(String val, String key) throws Exception {
        if(val == null || key == null)
            return null;

        Cipher decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE,getKey(key));
        byte[] originalByte = decryptCipher.doFinal(hexStr2ByteArr(val));

        return new String(originalByte);
    }


}
