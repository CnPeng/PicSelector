package com.cnpeng.piclib.antutils;

public class StringSplitTool {
	public String dateRemoveChar(String date) {
		if ("".equals(date) || date == null) {
			return null;
		}
		String b = "-";
		String c = date.replaceAll(b, "");
		c = c.replaceAll(" ", "");
		c = c.replaceAll(":", "");
		return c;
	}
}
