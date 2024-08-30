package net.errorpnf.bedwarsmod.data.stats;

import com.google.gson.JsonObject;
import net.errorpnf.bedwarsmod.data.BedwarsExperience;
import net.errorpnf.bedwarsmod.data.PrestigeList;
import net.errorpnf.bedwarsmod.utils.StatUtils;
import net.errorpnf.bedwarsmod.utils.formatting.RankUtils;

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

    public Stats(JsonObject apiReq) {
        this.apiReq = apiReq;
        this.s = new StatUtils(apiReq);
        this.prestigeList = new PrestigeList();

        // Initialize fields
        this.displayUsername = s.getStat("player.displayname");
        this.level = Integer.parseInt(s.getStat("player.achievements.bedwars_level"));
        this.wins = Integer.parseInt(s.getStat("player.stats.Bedwars.wins_bedwars"));
        this.losses = Integer.parseInt(s.getStat("player.stats.Bedwars.losses_bedwars"));
        this.gamesPlayed = wins + losses;

        this.wlr = roundToTwoDecimalPlaces(wins, losses);
        this.winsPerStar = roundToTwoDecimalPlaces(wins, level);
        this.lossesPerStar = roundToTwoDecimalPlaces(losses, level);

        this.finalKills = Integer.parseInt(s.getStat("player.stats.Bedwars.final_kills_bedwars"));
        this.finalDeaths = Integer.parseInt(s.getStat("player.stats.Bedwars.final_deaths_bedwars"));
        this.fkdr = roundToTwoDecimalPlaces(finalKills, finalDeaths);
        this.finalsPerGame = roundToTwoDecimalPlaces(finalKills, gamesPlayed);

        this.kills = Integer.parseInt(s.getStat("player.stats.Bedwars.kills_bedwars"));
        this.deaths = Integer.parseInt(s.getStat("player.stats.Bedwars.deaths_bedwars"));
        this.kdr = roundToTwoDecimalPlaces(kills, deaths);
        this.killsPerGame = roundToTwoDecimalPlaces(kills, gamesPlayed);

        this.beds = Integer.parseInt(s.getStat("player.stats.Bedwars.beds_broken_bedwars"));
        this.bedsLost = Integer.parseInt(s.getStat("player.stats.Bedwars.beds_lost_bedwars"));
        this.bblr = roundToTwoDecimalPlaces(beds, bedsLost);
        this.bedsPerGame = roundToTwoDecimalPlaces(beds, gamesPlayed);

        this.formattedRank = RankUtils.formatRankAndUsername(displayUsername, apiReq);
        this.formattedStar = prestigeList.getPrestige(level);
        this.formattedStarPlusOne = prestigeList.getPrestige(level + 1);

        this.exp = BedwarsExperience.parseExperience(s.getStat("player.stats.Bedwars.Experience"));
        this.currentLevelExperience = BedwarsExperience.getCurrentExperienceInLevel(exp);
        this.expReqToLevelUp = BedwarsExperience.getExperienceRequiredForCurrentLevel(exp);
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
