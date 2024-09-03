package me.errorpnf.bedwarsmod.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class PrestigeList {
    private final Map<Integer, PrestigeInfo> prestiges = new HashMap<>();

    public PrestigeList() {
        loadPrestiges();
    }

    private void loadPrestiges() {
        // Load the JSON file from resources
        try (Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("assets/bedwarsmod/data/prestiges.json"))) {
            if (reader == null) {
                throw new IllegalArgumentException("File not found: assets/bedwarsmod/data/prestiges.json");
            }
            JsonParser jsonParser = new JsonParser(); // Create an instance of JsonParser
            JsonObject jsonObject = jsonParser.parse(reader).getAsJsonObject(); // Use instance method parse
            JsonObject prestigesObject = jsonObject.getAsJsonObject("prestiges");

            Gson gson = new Gson();
            for (Map.Entry<String, JsonElement> entry : prestigesObject.entrySet()) {
                int prestigeLevel = Integer.parseInt(entry.getKey());
                JsonObject prestigeObject = entry.getValue().getAsJsonObject();
                PrestigeInfo info = gson.fromJson(prestigeObject, PrestigeInfo.class);
                prestiges.put(prestigeLevel, info);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    public String getPrestige(int level) {
        // Calculate the prestige index based on the level
        int prestigeIndex = level / 100; // Each prestige covers 100 levels

        // For levels 5000 and above, use a default formatting
        if (level >= 5000) {
            PrestigeInfo info = prestiges.get(50); // Assuming index 50 corresponds to level 5000+
            if (info != null) {
                return formatPrestige(level, info);
            }
            return "§f[5000✫]"; // Default formatting if data is missing
        }

        // Ensure the prestigeIndex is within the valid range
        if (prestigeIndex < 0 || prestigeIndex >= prestiges.size()) {
            return "§f[0✫]"; // Default formatting for invalid levels
        }

        // Retrieve the appropriate PrestigeInfo for the calculated prestigeIndex
        PrestigeInfo info = prestiges.get(prestigeIndex);
        if (info != null) {
            return formatPrestige(level, info);
        } else {
            // If no info found for this prestigeIndex, return default formatting
            return "§f[" + level + "✫]";
        }
    }




    private String formatPrestige(int level, PrestigeInfo info) {
        StringBuilder sb = new StringBuilder();

        String strLevel = String.valueOf(level);
        int len = strLevel.length();

        sb.append(info.colors.leftBracket);
        sb.append("[");

        for (int i = 0; i < len; i++) {
            char digit = strLevel.charAt(i);

            if (i == 0) {
                sb.append(info.colors.numberColors[0]);
            } else if (i == 1 && len > 1) {
                sb.append(info.colors.numberColors[1]);
            } else if (i == 2 && len > 2) {
                sb.append(info.colors.numberColors[2]);
            } else if (i == 3 && len > 3) {
                sb.append(info.colors.numberColors[3]);
            }
            sb.append(digit);
        }

        // Append the star symbol with its color
        sb.append(info.colors.starColor);
        sb.append(info.starSymbol);

        // Append the right bracket
        sb.append(info.colors.rightBracket);
        sb.append("]");

        return sb.toString();
    }

    public static class PrestigeInfo {
        public final String starSymbol;
        public final ColorInfo colors;

        public PrestigeInfo(JsonObject json) {
            this.starSymbol = json.has("starSymbol") ? json.get("starSymbol").getAsString() : "✫";
            this.colors = new ColorInfo(json.getAsJsonObject("colors"));
        }

        public static class ColorInfo {
            public final String leftBracket;
            public final String rightBracket;
            public final String starColor;
            public final String[] numberColors;

            public ColorInfo(JsonObject json) {
                this.leftBracket = json.has("leftBracket") ? json.get("leftBracket").getAsString() : "§f";
                this.rightBracket = json.has("rightBracket") ? json.get("rightBracket").getAsString() : "§f";
                this.starColor = json.has("starColor") ? json.get("starColor").getAsString() : "§f";
                this.numberColors = json.has("numberColors") ?
                        json.getAsJsonArray("numberColors").toString().replaceAll("[\\[\\]\"]", "").split(",") :
                        new String[]{"", "", "", ""};
            }
        }
    }
}
