package com.hlm.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * 用于 json 序列化解析
 */
public class UtilJsonParse {


    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private UtilJsonParse(){

    }

    /**
     * 转成json
     *
     * @param object
     * @return
     */
    public static String objToJsonString(Object object) {
        String jsonString = null;
        if (gson != null) {
            jsonString = gson.toJson(object);
        }
        return jsonString;
    }

    /**
     * 转成bean
     *
     * @param jsonString
     * @param cls
     * @return
     */
    public static <T> T jsonStringToBean(String jsonString, Class<T> cls){
        T t = null;
        try {

            if (gson != null) {
                t = gson.fromJson(jsonString, cls);
            }

        }catch (JsonParseException e){
            e.printStackTrace();//显示转化错误信息
            return null;
        }

        return t;
    }



    /**
     * 转成list
     * 泛型在编译期类型被擦除导致报错
     * @param jsonString
     * @param cls
     * @return
     */
    public static <T> List<T> jsonStringToList(String jsonString, Class<T> cls) {
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(jsonString, new TypeToken<List<T>>() {
            }.getType());
        }
        return list;
    }


//    /**
//     * 转成list
//     * 解决泛型问题
//     * @param json
//     * @param cls
//     * @param <T>
//     * @return
//     */
//    public <T> List<T> jsonToList(String json, Class<T> cls) {
//        Gson gson = new Gson();
//        List<T> list = new ArrayList<>();
//        JsonArray array = new ().parse(json).getAsJsonArray();
//        for(final JsonElement elem : array){
//            list.add(gson.fromJson(elem, cls));
//        }
//        return list;
//    }


    /**
     * 转成list中有map的
     *
     * @param jsonString
     * @return
     */
    public static <T> List<Map<String, T>> jsonToListMaps(String jsonString) {
        List<Map<String, T>> list = null;
        if (gson != null) {
            list = gson.fromJson(jsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        }
        return list;
    }


    /**
     * 转成map的
     *
     * @param jsonString
     * @return
     */
    public static <T> Map<String, T> jsonToMaps(String jsonString) {
        Map<String, T> map = null;
        if (gson != null) {
            map = gson.fromJson(jsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }
}
