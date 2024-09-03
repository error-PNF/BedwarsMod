package me.errorpnf.bedwarsmod.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class StatUtils {
    private final JsonObject jsonObject;

    public StatUtils(JsonObject jsonObject) {
        this.jsonObject = jsonObject;

        // check if the "player" field is null and deal with it
        if (jsonObject.get("success").getAsBoolean() && jsonObject.get("player").isJsonNull()) {
            throw new IllegalArgumentException("Player Not Found");
        }
    }

    public String getStat(String statPath) {
        JsonElement statElement = jsonObject;
        String[] keys = statPath.split("\\.");

        for (String key : keys) {
            if (statElement != null && statElement.isJsonObject()) {
                statElement = statElement.getAsJsonObject().get(key);
            } else {
                return "0";
            }
        }

        return statElement != null && !statElement.isJsonNull() ? statElement.getAsString() : "0";
    }
}
