package com.cnpeng.piclib.antutils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionPwd {
	public String encryPtion(String password) {
		String re_md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte b[] = md.digest();	 
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }	 
            re_md5 = buf.toString().toUpperCase();	 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
	}
}
