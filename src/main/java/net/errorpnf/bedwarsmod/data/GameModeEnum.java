package net.errorpnf.bedwarsmod.data;

public enum GameModeEnum {
    OVERALL("Overall"),
    EIGHT_ONE("1s"),
    EIGHT_TWO("2s"),
    FOUR_THREE("3s"),
    FOUR_FOUR("4s"),
    TWO_FOUR("4v4"),

    CASTLE("Castle"),

    EIGHT_TWO_LUCKY("Lucky Doubles"),
    FOUR_FOUR_LUCKY("Lucky Fours"),
    OVERALL_LUCKY("Lucky Overall"),

    EIGHT_TWO_RUSH("Rush Doubles"),
    FOUR_FOUR_RUSH("Rush Fours"),
    OVERALL_RUSH("Rush Overall"),

    EIGHT_TWO_VOIDLESS("Voidless Doubles"),
    FOUR_FOUR_VOIDLESS("Voidless Fours"),
    OVERALL_VOIDLESS("Voidless Overall"),

    EIGHT_TWO_ARMED("Armed Doubles"),
    FOUR_FOUR_ARMED("Armed Fours"),
    OVERALL_ARMED("Armed Overall"),

    EIGHT_TWO_ULTIMATE("Ultimate Doubles"),
    FOUR_FOUR_ULTIMATE("Ultimate Fours"),
    OVERALL_ULTIMATE("Ultimate Overall"),

    EIGHT_TWO_SWAP("Swap Doubles"),
    FOUR_FOUR_SWAP("Swap Fours"),
    OVERALL_SWAP("Swap Overall");

    private final String shortName;

    GameModeEnum(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}
