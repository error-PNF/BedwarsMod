package me.errorpnf.bedwarsmod.data.stats;

import com.google.gson.JsonObject;
import me.errorpnf.bedwarsmod.data.BedwarsExperience;
import me.errorpnf.bedwarsmod.data.GameModeEnum;
import me.errorpnf.bedwarsmod.data.PrestigeList;
import me.errorpnf.bedwarsmod.utils.StatUtils;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import me.errorpnf.bedwarsmod.utils.formatting.RankUtils;

import java.util.HashMap;
import java.util.Map;

public class Stats {
    public JsonObject apiReq;
    public StatUtils statUtils;
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
    public double finalsPerStar;

    public int kills;
    public int deaths;
    public double kdr;
    public double killsPerGame;
    public double killsPerStar;

    public int beds;
    public int bedsLost;
    public double bblr;
    public double bedsPerGame;
    public double bedsPerStar;

    public String formattedRank;
    public String formattedStar;
    public String formattedStarPlusOne;

    public int exp;
    public int currentLevelExperience;
    public int expReqToLevelUp;

    public int tokens;
    public int slumberTickets;

    public double clutchRatePercent;
    public double winRatePercent;

    public int skillIndex;

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
        this.statUtils = new StatUtils(apiReq);
        this.prestigeList = new PrestigeList();

        this.displayUsername = statUtils.getStat("player.displayname");
        this.level = Integer.parseInt(statUtils.getStat("player.achievements.bedwars_level"));

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

        this.wlr = FormatUtils.roundToTwoDecimalPlacesForStats(wins, losses);
        this.winsPerStar = FormatUtils.roundToTwoDecimalPlacesForStats(wins, level);
        this.lossesPerStar = FormatUtils.roundToTwoDecimalPlacesForStats(losses, level);

        this.fkdr = FormatUtils.roundToTwoDecimalPlacesForStats(finalKills, finalDeaths);
        this.finalsPerGame = FormatUtils.roundToTwoDecimalPlacesForStats(finalKills, gamesPlayed);
        this.finalsPerStar = FormatUtils.roundToTwoDecimalPlacesForStats(finalKills, level);

        this.kdr = FormatUtils.roundToTwoDecimalPlacesForStats(kills, deaths);
        this.killsPerGame = FormatUtils.roundToTwoDecimalPlacesForStats(kills, gamesPlayed);
        this.killsPerStar = FormatUtils.roundToTwoDecimalPlacesForStats(kills, level);

        this.bblr = FormatUtils.roundToTwoDecimalPlacesForStats(beds, bedsLost);
        this.bedsPerGame = FormatUtils.roundToTwoDecimalPlacesForStats(beds, gamesPlayed);
        this.bedsPerStar = FormatUtils.roundToTwoDecimalPlacesForStats(beds, level);

        this.formattedRank = RankUtils.formatRankAndUsername(displayUsername, apiReq);
        this.formattedStar = prestigeList.getPrestige(level);
        this.formattedStarPlusOne = prestigeList.getPrestige(level + 1);

        this.exp = BedwarsExperience.parseExperience(statUtils.getStat("player.stats.Bedwars.Experience"));
        this.currentLevelExperience = BedwarsExperience.getCurrentExperienceInLevel(exp);
        this.expReqToLevelUp = BedwarsExperience.getExperienceRequiredForCurrentLevel(exp);

        this.tokens = getStat("coins");
        this.slumberTickets = getStat("slumber.total_tickets_earned");

        double rawPercentage = (double) (wins - (gamesPlayed - bedsLost)) / bedsLost * 100d;
        this.clutchRatePercent = Math.max(0, FormatUtils.formatDecimal(rawPercentage));
        this.winRatePercent = FormatUtils.formatDecimal((100d / gamesPlayed) * wins);

        this.skillIndex = calculateSkillIndex(fkdr, level);
    }

    private int getStat(String statKey) {
        return Integer.parseInt(statUtils.getStat("player.stats.Bedwars." + statKey));
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

    public static int calculateSkillIndex(double fkdr, double stars) {
        // calculate skill score using the formula (fkdr^2) * stars
        double skillScore = Math.pow(fkdr, 2) * stars;

        return (int) skillScore;
    }
}
