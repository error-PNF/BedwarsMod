package net.errorpnf.bedwarsmod.data.stats;

import com.google.gson.JsonObject;
import net.errorpnf.bedwarsmod.data.BedwarsExperience;
import net.errorpnf.bedwarsmod.data.GameModeEnum;
import net.errorpnf.bedwarsmod.data.PrestigeList;
import net.errorpnf.bedwarsmod.utils.StatUtils;
import net.errorpnf.bedwarsmod.utils.formatting.RankUtils;

import java.util.HashMap;
import java.util.Map;

public class Stats {
    public JsonObject apiReq;
    public StatUtils s;
    public PrestigeList prestigeList;

    public String displayUsername;
    public int level;
    public int wins;
    public int losses;
    public int gamesPlayed;

    public double wlr;
    public double winsPerStar;
    public double lossesPerStar;

    public int finalKills;
    public int finalDeaths;
    public double fkdr;
    public double finalsPerGame;

    public int kills;
    public int deaths;
    public double kdr;
    public double killsPerGame;

    public int beds;
    public int bedsLost;
    public double bblr;
    public double bedsPerGame;

    public String formattedRank;
    public String formattedStar;
    public String formattedStarPlusOne;

    public int exp;
    public int currentLevelExperience;
    public int expReqToLevelUp;

    private static final Map<GameModeEnum, String> statPrefixes = new HashMap<>();

    static {
        statPrefixes.put(GameModeEnum.OVERALL, "");
        statPrefixes.put(GameModeEnum.EIGHT_ONE, "eight_one_");
        statPrefixes.put(GameModeEnum.EIGHT_TWO, "eight_two_");
        statPrefixes.put(GameModeEnum.FOUR_THREE, "four_three_");
        statPrefixes.put(GameModeEnum.FOUR_FOUR, "four_four_");
        statPrefixes.put(GameModeEnum.TWO_FOUR, "two_four_");
        statPrefixes.put(GameModeEnum.CASTLE, "castle_");
        statPrefixes.put(GameModeEnum.EIGHT_TWO_LUCKY, "eight_two_lucky_");
        statPrefixes.put(GameModeEnum.FOUR_FOUR_LUCKY, "four_four_lucky_");
        statPrefixes.put(GameModeEnum.EIGHT_TWO_RUSH, "eight_two_rush_");
        statPrefixes.put(GameModeEnum.FOUR_FOUR_RUSH, "four_four_rush_");
        statPrefixes.put(GameModeEnum.EIGHT_TWO_VOIDLESS, "eight_two_voidless_");
        statPrefixes.put(GameModeEnum.FOUR_FOUR_VOIDLESS, "four_four_voidless_");
        statPrefixes.put(GameModeEnum.EIGHT_TWO_ARMED, "eight_two_armed_");
        statPrefixes.put(GameModeEnum.FOUR_FOUR_ARMED, "four_four_armed_");
        statPrefixes.put(GameModeEnum.EIGHT_TWO_ULTIMATE, "eight_two_ultimate_");
        statPrefixes.put(GameModeEnum.FOUR_FOUR_ULTIMATE, "four_four_ultimate_");
        statPrefixes.put(GameModeEnum.EIGHT_TWO_SWAP, "eight_two_swap_");
        statPrefixes.put(GameModeEnum.FOUR_FOUR_SWAP, "four_four_swap_");
    }

    public Stats(JsonObject apiReq, GameModeEnum gameMode) {
        this.apiReq = apiReq;
        this.s = new StatUtils(apiReq);
        this.prestigeList = new PrestigeList();

        this.displayUsername = s.getStat("player.displayname");
        this.level = Integer.parseInt(s.getStat("player.achievements.bedwars_level"));

        // Determine the prefix based on the game mode
        String prefix = statPrefixes.get(gameMode);

        // Fetch and sum stats based on game mode
        this.wins = getStat(prefix + "wins_bedwars");
        this.losses = getStat(prefix + "losses_bedwars");
        this.finalKills = getStat(prefix + "final_kills_bedwars");
        this.finalDeaths = getStat(prefix + "final_deaths_bedwars");
        this.kills = getStat(prefix + "kills_bedwars");
        this.deaths = getStat(prefix + "deaths_bedwars");
        this.beds = getStat(prefix + "beds_broken_bedwars");
        this.bedsLost = getStat(prefix + "beds_lost_bedwars");

        // If combined modes (like OVERALL_LUCKY), add the stats from multiple prefixes
        if (gameMode == GameModeEnum.OVERALL_LUCKY) {
            addCombinedStats("eight_two_lucky_", "four_four_lucky_");
        } else if (gameMode == GameModeEnum.OVERALL_RUSH) {
            addCombinedStats("eight_two_rush_", "four_four_rush_");
        } else if (gameMode == GameModeEnum.OVERALL_VOIDLESS) {
            addCombinedStats("eight_two_voidless_", "four_four_voidless_");
        } else if (gameMode == GameModeEnum.OVERALL_ARMED) {
            addCombinedStats("eight_two_armed_", "four_four_armed_");
        } else if (gameMode == GameModeEnum.OVERALL_ULTIMATE) {
            addCombinedStats("eight_two_ultimate_", "four_four_ultimate_");
        } else if (gameMode == GameModeEnum.OVERALL_SWAP) {
            addCombinedStats("eight_two_swap_", "four_four_swap_");
        }


        this.gamesPlayed = wins + losses;

        this.wlr = roundToTwoDecimalPlaces(wins, losses);
        this.winsPerStar = roundToTwoDecimalPlaces(wins, level);
        this.lossesPerStar = roundToTwoDecimalPlaces(losses, level);

        this.fkdr = roundToTwoDecimalPlaces(finalKills, finalDeaths);
        this.finalsPerGame = roundToTwoDecimalPlaces(finalKills, gamesPlayed);

        this.kdr = roundToTwoDecimalPlaces(kills, deaths);
        this.killsPerGame = roundToTwoDecimalPlaces(kills, gamesPlayed);

        this.bblr = roundToTwoDecimalPlaces(beds, bedsLost);
        this.bedsPerGame = roundToTwoDecimalPlaces(beds, gamesPlayed);

        this.formattedRank = RankUtils.formatRankAndUsername(displayUsername, apiReq);
        this.formattedStar = prestigeList.getPrestige(level);
        this.formattedStarPlusOne = prestigeList.getPrestige(level + 1);

        this.exp = BedwarsExperience.parseExperience(s.getStat("player.stats.Bedwars.Experience"));
        this.currentLevelExperience = BedwarsExperience.getCurrentExperienceInLevel(exp);
        this.expReqToLevelUp = BedwarsExperience.getExperienceRequiredForCurrentLevel(exp);
    }

    private int getStat(String statKey) {
        return Integer.parseInt(s.getStat("player.stats.Bedwars." + statKey));
    }

    private void addCombinedStats(String prefix1, String prefix2) {
        this.wins += getStat(prefix1 + "wins_bedwars") + getStat(prefix2 + "wins_bedwars");
        this.losses += getStat(prefix1 + "losses_bedwars") + getStat(prefix2 + "losses_bedwars");
        this.finalKills += getStat(prefix1 + "final_kills_bedwars") + getStat(prefix2 + "final_kills_bedwars");
        this.finalDeaths += getStat(prefix1 + "final_deaths_bedwars") + getStat(prefix2 + "final_deaths_bedwars");
        this.kills += getStat(prefix1 + "kills_bedwars") + getStat(prefix2 + "kills_bedwars");
        this.deaths += getStat(prefix1 + "deaths_bedwars") + getStat(prefix2 + "deaths_bedwars");
        this.beds += getStat(prefix1 + "beds_broken_bedwars") + getStat(prefix2 + "beds_broken_bedwars");
        this.bedsLost += getStat(prefix1 + "beds_lost_bedwars") + getStat(prefix2 + "beds_lost_bedwars");
    }

    private static double roundToTwoDecimalPlaces(int numerator, int denominator) {
        if (numerator != 0 && denominator != 0) {
            double result = (double) numerator / denominator;
            return Math.round(result * 100.0) / 100.0;
        } else if (numerator != 0) {
            return numerator;
        } else if (denominator != 0) {
            return denominator;
        } else {
            return 0.0;
        }
    }
}
