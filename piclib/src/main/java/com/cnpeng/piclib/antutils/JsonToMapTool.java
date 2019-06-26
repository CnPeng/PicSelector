package com.cnpeng.piclib.antutils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonToMapTool {
	public static Map<String, Object> getMapForJson(String jsonStr) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonStr);
			Iterator<String> keyIter = jsonObject.keys();
			String key = "";
			String value;
			Map<String, Object> valueMap = new HashMap<>();
			while (keyIter.hasNext()) {
				key = keyIter.next();
				value = jsonObject.get(key).toString();
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
