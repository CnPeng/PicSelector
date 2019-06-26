package com.cnpeng.piclib.antutils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtility {
	public static String CurrentTimeString() {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
		return df.format(new Date());
	}
}
