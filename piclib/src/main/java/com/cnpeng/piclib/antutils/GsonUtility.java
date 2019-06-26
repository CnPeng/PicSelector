package com.cnpeng.piclib.antutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Gson解析的工具类
 * Created by shorr on 2015/12/4.
 */
public class GsonUtility {

    /**
     * Json数据转换为JavaBean
     *
     * @param pJsonString
     * @param pClassOfT
     * @param <T>
     * @return
     */
    public static <T> T json2Bean(String pJsonString, Class<T> pClassOfT) {
        GsonBuilder _gsonBuilder = new GsonBuilder();
        _gsonBuilder.serializeNulls();
        _gsonBuilder.registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory());
        Gson _gson = _gsonBuilder.create();
        return _gson.fromJson(pJsonString, pClassOfT);
    }

    /**
     * JavaBean转换为Json数据
     *
     * @param pBean
     * @return
     */
    public static String bean2Json(Object pBean) {
        Gson _gson = new Gson();
        String _json = _gson.toJson(pBean);
        return _json;
    }

    /**
     * NullStringToEmptyAdapterFactory 若返回的字段数据为null时替换为“”
     *
     * @param <T>
     */
    private static class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {

        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringAdapter();
        }
    }

    private static class StringAdapter extends TypeAdapter<String> {

        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }

}
