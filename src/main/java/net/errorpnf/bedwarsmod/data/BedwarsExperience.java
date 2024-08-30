package net.errorpnf.bedwarsmod.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BedwarsExperience {
    private static final int EXPERIENCE_PER_PRESTIGE = 487000;
    private static final int[] STAR_EXPERIENCE = {500, 1000, 2000, 3500, 5000};

    public static int getCurrentExperienceInLevel(int totalExperience) {
        int experienceInPrestige = totalExperience % EXPERIENCE_PER_PRESTIGE;
        int accumulatedExperience = 0;

        // Handle the first 4 special levels
        for (int i = 0; i < 4; i++) {
            if (experienceInPrestige < accumulatedExperience + STAR_EXPERIENCE[i]) {
                return experienceInPrestige - accumulatedExperience;
            }
            accumulatedExperience += STAR_EXPERIENCE[i];
        }

        // Handle levels 5 and beyond
        return (experienceInPrestige - accumulatedExperience) % STAR_EXPERIENCE[4];
    }

    public static int getExperienceRequiredForCurrentLevel(int totalExperience) {
        int experienceInPrestige = totalExperience % EXPERIENCE_PER_PRESTIGE;
        int accumulatedExperience = 0;

        // Handle the first 4 special levels
        for (int i = 0; i < 4; i++) {
            if (experienceInPrestige < accumulatedExperience + STAR_EXPERIENCE[i]) {
                return STAR_EXPERIENCE[i];
            }
            accumulatedExperience += STAR_EXPERIENCE[i];
        }

        // Handle levels 5 and beyond
        return STAR_EXPERIENCE[4];
    }

    public static String getProgressBar(int totalExperience) {
        int currentExp = getCurrentExperienceInLevel(totalExperience);
        int requiredExp = getExperienceRequiredForCurrentLevel(totalExperience);

        int filledSlots = Math.min(10, (currentExp * 10 + requiredExp - 1) / requiredExp);

        StringBuilder progressBar = new StringBuilder(60); // Increased capacity for color codes
        for (int i = 0; i < filledSlots; i++) {
            progressBar.append("§b■");
        }
        for (int i = filledSlots; i < 10; i++) {
            progressBar.append("§7■");
        }

        return " §8[" + progressBar + "§8] ";
    }

    public static int parseExperience(String stat) {
        try {
            if (stat.contains(".")) {
                // If the value contains a decimal point, it's likely a double
                double doubleValue = Double.parseDouble(stat);
                return (int) doubleValue; // Cast the double to int
            } else {
                // Otherwise, parse it directly as an integer
                return Integer.parseInt(stat);
            }
        } catch (NumberFormatException e) {
            // Handle cases where the string cannot be parsed as a number
            System.err.println("Error parsing experience: " + e.getMessage());
            return 0; // Default value in case of an error
        }
    }
}
