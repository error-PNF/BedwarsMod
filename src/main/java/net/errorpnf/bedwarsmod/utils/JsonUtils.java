package net.errorpnf.bedwarsmod.utils;

import com.google.gson.JsonObject;

public class JsonUtils {
    public static boolean isEmpty(JsonObject jsonObject) {
        return jsonObject == null || jsonObject.entrySet().isEmpty();
    }
}
