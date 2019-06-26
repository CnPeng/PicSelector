package com.cnpeng.piclib.antutils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yxj on 16/3/15.
 */
public class EditTextMaxLengthFilter implements InputFilter {//输入最大个数，字符是汉字的2倍
    int MAX_EN;//最大长度，一个汉字是两个字符
    String regEx = "[\u4e00-\u9fa5]";//汉字和字符

    public EditTextMaxLengthFilter(int mAX_EN) {
        super();
        MAX_EN = mAX_EN;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int destCount = dest.toString().length() + getChineseCount(dest.toString());
        int sourceCount = source.toString().length() + getChineseCount(source.toString());

        if (destCount + sourceCount > MAX_EN) {
            return "";
        } else {
            return source;
        }
    }

    private int getChineseCount(String str) {
        int count = 0;
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count;
    }
}
