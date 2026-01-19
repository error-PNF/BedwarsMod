package me.errorpnf.bedwarsmod.utils;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.errorpnf.bedwarsmod.data.apicache.ApiCacheManager;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ApiUtils {
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public CompletableFuture<UUID> getPlayerUUIDAsync(String username) {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;

        Request request = new Request.Builder().url(url).build();

        CompletableFuture<UUID> future = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                    return;
                }

                String responseBody = Objects.requireNonNull(response.body()).string();

                try {
                    JsonObject json = gson.fromJson(responseBody, JsonObject.class);
                    future.complete(UUIDUtils.fromTrimmed(json.get("id").getAsString()));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                } finally {
                    response.close();
                }
            }
        });
        return future;
    }

    public CompletableFuture<JsonObject> hypixelApiRequest(String username) {
        UUID playerUUID = null;
        try {
            playerUUID = getPlayerUUIDAsync(username).get();
        } catch (Exception e) {
            return CompletableFuture.completedFuture(null);
        }

        OkHttpClient client = new OkHttpClient();
        String API_BASE_URL = "https://hypixel-bedwars-api-mod-backend.mggpr7qd55.workers.dev/";
        String url = API_BASE_URL + "player?uuid=" + playerUUID.toString();

        if (ApiCacheManager.getCachedRequest(username) != null) {
            return CompletableFuture.completedFuture(ApiCacheManager.getCachedRequest(username));
        }

        Request request = new Request.Builder().url(url).build();
        CompletableFuture<JsonObject> future = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                    UChat.chat("&cAn unexpected error occurred during the API request. This is likely a rate limit issue.");
                    return;
                }

                String responseBody = Objects.requireNonNull(response.body()).string();

                try {
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(responseBody).getAsJsonObject();

                    // stores this api json object to be accessible by other classes
                    ApiCacheManager.cacheRequest(username, jsonObject);

                    future.complete(jsonObject);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });

        return future;
    }
}
