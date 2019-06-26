package com.cnpeng.piclib.antutils;

/**
 * 手机号加密的处理类,加密中间4位
 * Created by Shorr on 16/3/12.
 */
public class TelEncryptionUtility {

    /**
     * 得到加密的手机号码
     *
     * @param telString
     * @return
     */
    public static String getEncryptionTel(String telString) {

        if (telString.length() < 11) {
            return "";
        }

        String tel1 = telString.substring(0, 3);
        String tel2 = telString.substring(7, 11);

        String encString = tel1 + "****" + tel2;
        return encString;
    }
}
