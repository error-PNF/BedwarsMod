package me.errorpnf.bedwarsmod.utils.profileviewer;

import com.google.gson.JsonObject;
import me.errorpnf.bedwarsmod.data.GameModeEnum;
import me.errorpnf.bedwarsmod.data.stats.Stats;
import me.errorpnf.bedwarsmod.utils.RenderUtils;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

public class PlayerStatsCard extends GuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static JsonObject jsonObject;
    private static FontRenderer fontRenderer;
    private static GameModeEnum gamemode;

    public PlayerStatsCard(JsonObject jsonObject, FontRenderer fontRenderer, GameModeEnum gamemode) {
        PlayerStatsCard.jsonObject = jsonObject;
        PlayerStatsCard.fontRenderer = fontRenderer;
        PlayerStatsCard.gamemode = gamemode;
    }

    public void drawStatsCard(float centerX, float y, int mouseX, int mouseY) {
        String titleAndModeName = FormatUtils.format("&cBedwars &fStats &f(" + gamemode.getFullName() + ")");

        RenderUtils.drawStringCenteredScaled(fontRenderer, titleAndModeName, centerX, y + 11.5f, true, 0, 1.25f);
        RenderUtils.drawStringWithNewlinesScaled(fontRenderer, getLeftText(), centerX - ((430f * 2/3f) / 2f), y + 21f, true, 0, 5f, 5f, 0.825f);
        RenderUtils.drawStringWithNewlinesScaled(fontRenderer, getCenterText(), centerX - ((134f * 2/3f) / 2f), y + 21f, true, 0, 5f, 5f, 0.825f);
        RenderUtils.drawStringWithNewlinesScaled(fontRenderer, getRightText(), centerX + ((162f * 2/3f) / 2f), y + 21f, true, 0, 5f, 5f, 0.825f);
    }

    private String getLeftText() {
        Stats stats = new Stats(jsonObject, gamemode);

        return FormatUtils.format("&9" + FormatUtils.formatCommas(stats.tokens) + " &fTokens\n") +
                FormatUtils.format("&9" + FormatUtils.formatCommas(stats.slumberTickets) + " &fTickets\n") +
                "\n" + // Add a newfc( line
                FormatUtils.format("&a" + FormatUtils.formatCommas(stats.wins) + " &fWins\n") +
                FormatUtils.format("&a" + FormatUtils.formatCommas(stats.kills) + " &fKills\n") +
                FormatUtils.format("&a" + FormatUtils.formatCommas(stats.finalKills) + " &fFinal Kills\n") +
                FormatUtils.format("&a" + FormatUtils.formatCommas(stats.beds) + " &fBeds Broken\n") +
                "\n" + // Add a new line
                FormatUtils.format("&e" + stats.killsPerGame + " &fKills/Game\n") +
                FormatUtils.format("&b" + stats.killsPerStar + " &fKills/Star\n");
    }

    private String getCenterText() {
        Stats stats = new Stats(jsonObject, gamemode);

        return FormatUtils.format("&9" + stats.clutchRatePercent + "% &fClutch Rate\n") +
                FormatUtils.format("&9" + FormatUtils.formatCommas(stats.gamesPlayed) + " &fGames Played\n") +
                "\n" + // Add a newfc( line
                FormatUtils.format("&c" + FormatUtils.formatCommas(stats.losses) + " &fLosses\n") +
                FormatUtils.format("&c" + FormatUtils.formatCommas(stats.deaths) + " &fDeaths\n") +
                FormatUtils.format("&c" + FormatUtils.formatCommas(stats.finalDeaths) + " &fFinal Deaths\n") +
                FormatUtils.format("&c" + FormatUtils.formatCommas(stats.bedsLost) + " &fBeds Lost\n") +
                "\n" + // Add a new line
                FormatUtils.format("&e" + stats.finalsPerGame + " &fFinals/Game\n") +
                FormatUtils.format("&b" + stats.finalsPerStar + " &fFinals/Star\n");
    }

    private String getRightText() {
        Stats stats = new Stats(jsonObject, gamemode);

        return FormatUtils.format("&9" + stats.winRatePercent + "% &fWin Rate\n") +
                FormatUtils.format("&9" + FormatUtils.formatCommas(stats.skillIndex) + " &fSkill Index\n") +
                "\n" + // Add a newfc( line
                FormatUtils.format("&d" + stats.wlr + " &fWLR\n") +
                FormatUtils.format("&d" + stats.kdr + " &fKDR\n") +
                FormatUtils.format("&d" + stats.fkdr + " &fFKDR\n") +
                FormatUtils.format("&d" + stats.bblr + " &fBBLR\n") +
                "\n" + // Add a new line
                FormatUtils.format("&e" + stats.bedsPerGame + " &fBeds/Game\n") +
                FormatUtils.format("&b" + stats.bedsPerStar + " &fBeds/Star\n");
    }
}
