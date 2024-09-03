package me.errorpnf.bedwarsmod.data;

public enum GameModeEnum {
    OVERALL("Overall", "Overall"),
    EIGHT_ONE("1s", "Solos"),
    EIGHT_TWO("2s", "Doubles"),
    FOUR_THREE("3s", "3v3v3v3"),
    FOUR_FOUR("4s", "4v4v4v4"),
    TWO_FOUR("4v4", "4v4"),

    CASTLE("Castle", "Castle"),

    EIGHT_TWO_LUCKY("Lucky Doubles", "Lucky Doubles"),
    FOUR_FOUR_LUCKY("Lucky Fours", "Lucky Fours"),
    OVERALL_LUCKY("Lucky Overall", "Lucky Overall"),

    EIGHT_TWO_RUSH("Rush Doubles", "Rush Doubles"),
    FOUR_FOUR_RUSH("Rush Fours", "Rush Fours"),
    OVERALL_RUSH("Rush Overall", "Rush Overall"),

    EIGHT_TWO_VOIDLESS("Voidless Doubles", "Voidless Doubles"),
    FOUR_FOUR_VOIDLESS("Voidless Fours", "Voidless Fours"),
    OVERALL_VOIDLESS("Voidless Overall", "Voidless Overall"),

    EIGHT_TWO_ARMED("Armed Doubles", "Armed Doubles"),
    FOUR_FOUR_ARMED("Armed Fours", "Armed Fours"),
    OVERALL_ARMED("Armed Overall", "Armed Overall"),

    EIGHT_TWO_ULTIMATE("Ultimate Doubles", "Ultimate Doubles"),
    FOUR_FOUR_ULTIMATE("Ultimate Fours", "Ultimate Fours"),
    OVERALL_ULTIMATE("Ultimate Overall", "Ultimate Overall"),

    EIGHT_TWO_SWAP("Swap Doubles", "Swap Doubles"),
    FOUR_FOUR_SWAP("Swap Fours", "Swap Fours"),
    OVERALL_SWAP("Swap Overall", "Swap Overall");

    private final String shortName;
    private final String fullName;

    GameModeEnum(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }
}
