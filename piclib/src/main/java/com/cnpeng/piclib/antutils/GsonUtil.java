package com.cnpeng.piclib.antutils;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanglin on 2016/6/2.
 */
public class GsonUtil {
    private static Gson gson = new Gson();

    private GsonUtil() {
    }

    /**
     * 转成json
     * bean、List 都可以
     */
    public static String objToJson(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }

    /**
     * 转成bean
     *
     * CnPeng 2018/9/14 上午9:22 此处不需要判空，json内部判断如果为空直接返回null
     */
    public static <T> T stringToBean(String gsonString, Class<T> cls) {
        T t = null;
        if (gson != null) {
            t = gson.fromJson(gsonString, cls);
        }
        return t;
    }

    /**
     * 转成list优先使用这一个解析
     * CnPeng 2019-06-06 10:43
     */
    @NonNull
    public static <T> ArrayList<T> stringToList(String gsonString, Class<T> cls) {
        ArrayList<T> list = new ArrayList<>();
        if (gson != null && !TextUtils.isEmpty(gsonString)) {
            JsonArray array = new JsonParser().parse(gsonString).getAsJsonArray();
            for (final JsonElement elem : array) {
                list.add(gson.fromJson(elem, cls));
            }
        }
        return list;
    }


    /**
     * Json 转换成 map
     * value 为 非集合类型。（基本数据类型或者用户自定义bean）
     * // TODO: CnPeng 2019-06-06 10:44 这个还没做完。。。。
     */
    public static <T> Map<String, T> stringToMap(String jsonStr, Class<T> cls) {
        Map<String, T> map=null;
        if (null != gson) {
//            map = gson.fromJson(jsonStr, new TypeToken<Map<String, T>>() {
//            }.getType());


        }
        return map;
    }


    /**
     * 转成list<List<>>
     */
    public static <T> List<List<T>> stringTo2List(String gsonString, Class<T> cls) {
        List<List<T>> list = null;
        try {
            JsonArray jsonArray = new JsonParser().parse(gsonString).getAsJsonArray();
            list = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                String str = jsonArray.get(i).toString();
                JsonArray jsonArray1 = new JsonParser().parse(str).getAsJsonArray();
                List<T> list1 = new ArrayList<>();
                for (int j = 0; j < jsonArray1.size(); j++) {
                    JsonElement element = jsonArray1.get(j);
                    list1.add(gson.fromJson(element, cls));
                }
                if (list1.size() > 0) {
                    list.add(list1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 转成list, 有可能造成类型擦除
     *
     * CnPeng 2018/8/4 上午12:34 尽量不要用这个，因为会造成类型擦除。所以通过360加固之后，
     * 在7.0手机上会崩溃：java.lang.AssertionError: illegal type variable reference. 8.0系统不崩溃
     *
     * @deprecated
     */
    public static <T> ArrayList<T> stringToList(String gsonString) {
        ArrayList<T> list = null;
        if (gson != null) {
            list = gson.fromJson(gsonString, new TypeToken<ArrayList<T>>() {
            }.getType());
        }
        return list;
    }

    /**
     * 转成map的
     *
     * @deprecated
     */
    public static <T> Map<String, T> stringToMaps(String gsonString, Class<T> cls) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }
}