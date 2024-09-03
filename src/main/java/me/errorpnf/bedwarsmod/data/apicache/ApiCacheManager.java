package me.errorpnf.bedwarsmod.data.apicache;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ApiCacheManager {
    private static final long CACHE_DURATION = 300000; // 2.5 minutes in milliseconds
    private static final Map<String, CachedRequest> cache = new HashMap<>();

    private static class CachedRequest {
        JsonObject data;
        long timestamp;
        String username;

        CachedRequest(JsonObject data, long timestamp, String username) {
            this.data = data;
            this.timestamp = timestamp;
            this.username = username;
        }
    }

    public static void cacheRequest(String username, JsonObject data) {
        cache.put(username.toLowerCase(), new CachedRequest(data, System.currentTimeMillis(), username));
    }

    public static JsonObject getCachedRequest(String username) {
        for (CachedRequest cachedRequest : cache.values()) {
            if (cachedRequest.username.equalsIgnoreCase(username)) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - cachedRequest.timestamp < CACHE_DURATION) {
                    return cachedRequest.data;
                } else {
                    cache.remove(cachedRequest.username.toLowerCase());
                    break;
                }
            }
        }
        return null;
    }
}