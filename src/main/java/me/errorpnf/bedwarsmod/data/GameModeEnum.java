package me.errorpnf.bedwarsmod.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum GameModeEnum {
    OVERALL("Overall", "Overall", "Core Modes"),
    EIGHT_ONE("1s", "Solos", "Core Modes"),
    EIGHT_TWO("2s", "Doubles", "Core Modes"),
    FOUR_THREE("3s", "3v3v3v3", "Core Modes"),
    FOUR_FOUR("4s", "4v4v4v4", "Core Modes"),
    TWO_FOUR("4v4", "4v4", "4v4"),

    CASTLE("Castle", "Castle"),

    OVERALL_LUCKY("Lucky Overall", "Lucky Blocks"),
    EIGHT_TWO_LUCKY("Lucky Doubles", "Lucky Blocks"),
    FOUR_FOUR_LUCKY("Lucky Fours", "Lucky Blocks"),

    OVERALL_RUSH("Rush Overall", "Rush"),
    EIGHT_TWO_RUSH("Rush Doubles", "Rush"),
    FOUR_FOUR_RUSH("Rush Fours", "Rush"),

    OVERALL_VOIDLESS("Voidless Overall", "Voidless"),
    EIGHT_TWO_VOIDLESS("Voidless Doubles", "Voidless"),
    FOUR_FOUR_VOIDLESS("Voidless Fours", "Voidless"),

    OVERALL_ARMED("Armed Overall", "Armed"),
    EIGHT_TWO_ARMED("Armed Doubles", "Armed"),
    FOUR_FOUR_ARMED("Armed Fours", "Armed"),

    OVERALL_ULTIMATE("Ultimate Overall", "Ultimate"),
    EIGHT_TWO_ULTIMATE("Ultimate Doubles", "Ultimate"),
    FOUR_FOUR_ULTIMATE("Ultimate Fours", "Ultimate"),

    OVERALL_SWAP("Swap Overall", "Swap Overall", "Swap"),
    EIGHT_TWO_SWAP("Swap Doubles", "Swap"),
    FOUR_FOUR_SWAP("Swap Fours", "Swap");

    private final String shortName;
    private final String fullName;
    private final String category;

    GameModeEnum(String shortName, String fullName, String category) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.category = category;
    }
    GameModeEnum(String name, String category) {
        this.shortName = name;
        this.fullName = name;
        this.category = category;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCategory() {
        return category;
    }

    public static List<GameModeEnum> getCoreModes() {
        return Arrays.stream(values())
                .filter(mode -> "Core".equals(mode.getCategory()))
                .collect(Collectors.toList());
    }

    public static List<GameModeEnum> getModesByCategory(String category) {
        return Arrays.stream(values())
                .filter(mode -> category.equals(mode.getCategory()))
                .collect(Collectors.toList());
    }
}
