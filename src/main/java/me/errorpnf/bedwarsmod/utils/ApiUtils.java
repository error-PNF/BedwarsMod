package me.errorpnf.bedwarsmod.utils;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.*;
import me.errorpnf.bedwarsmod.data.apicache.ApiCacheManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ApiUtils {


    public static JsonObject lastApiResponse = null;

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static CompletableFuture<UUID> getPlayerUUIDAsync(String username) {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;

        Request request = new Request.Builder()
                .url(url)
                .build();

        CompletableFuture<UUID> future = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                    return;
                }

                String responseBody = response.body().string();

                try {
                    JsonObject json = gson.fromJson(responseBody, JsonObject.class);
                    String id = json.get("id").getAsString();
                    id = id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20);

                    future.complete(UUID.fromString(id));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                } finally {
                    response.close();
                }
            }
        });

        return future;
    }




    private static final String API_BASE_URL = "https://hypixel-bedwars-api-mod-backend.mggpr7qd55.workers.dev/";

    public static CompletableFuture<JsonObject> hypixelApiRequest(String username) {
        OkHttpClient client = new OkHttpClient();
        String url = API_BASE_URL + "player?name=" + username;

        Request request = new Request.Builder()
                .url(url)
                .build();

        CompletableFuture<JsonObject> future = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                    UChat.chat("&cAn unexpected error occurred during the API request. This is likely a rate limit issue.");
                    return;
                }

                String responseBody = response.body().string();
                //System.out.println("API Response: " + responseBody); // Debug output

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


    public static String formatUUIDWithHyphens(String uuidWithoutHyphens) {
        if (uuidWithoutHyphens == null || uuidWithoutHyphens.length() != 32) {
            throw new IllegalArgumentException("Invalid UUID string. Must be 32 characters long.");
        }

        // Insert hyphens at the appropriate positions
        return uuidWithoutHyphens.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5"
        );
    }
}
