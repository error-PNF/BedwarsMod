package me.errorpnf.bedwarsmod.utils.profileviewer;

import com.google.gson.JsonObject;
import me.errorpnf.bedwarsmod.data.GameModeEnum;
import me.errorpnf.bedwarsmod.data.stats.Stats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String titleAndModeName = formatText("&cBedwars &fStats &f(" + gamemode.getFullName() + ")");

        drawStringCenteredScaled(fontRenderer, titleAndModeName, centerX, y + 11.5f, true, 0, 1.25f);
        drawStringWithNewlinesScaled(fontRenderer, getLeftText(), centerX - ((430f * 2/3f) / 2f), y + 21f, true, 0, 5f, 5f, 0.825f);
        drawStringWithNewlinesScaled(fontRenderer, getCenterText(), centerX - ((134f * 2/3f) / 2f), y + 21f, true, 0, 5f, 5f, 0.825f);
        drawStringWithNewlinesScaled(fontRenderer, getRightText(), centerX + ((162f * 2/3f) / 2f), y + 21f, true, 0, 5f, 5f, 0.825f);
    }




    private static void drawStringCentered(FontRenderer fr, String str, float x, float y, boolean shadow, int colour) {
        int strLen = fr.getStringWidth(unformatText(str));

        float x2 = x - strLen / 2f;
        float y2 = y - fr.FONT_HEIGHT / 2f;

        GL11.glTranslatef(x2, y2, 0);
        fr.drawString(str, 0, 0, colour, shadow);
        GL11.glTranslatef(-x2, -y2, 0);
    }

    private static void drawStringWithNewlinesScaled(
            FontRenderer fr,
            String str,
            float x,
            float y,
            boolean shadow,
            int colour,
            float lineSpacing,
            float emptyLineHeight,
            float scaleFactor
    ) {

        String[] lines = str.split("\n");

        // calculate the height of each line with scaling
        int lineHeight = (int) (fr.FONT_HEIGHT * scaleFactor);

        float currentY = y;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (line.trim().isEmpty()) {
                // for empty lines, use the specified empty line height
                currentY += emptyLineHeight;
            } else {

                // draw non-empty lines
                GL11.glPushMatrix();
                GL11.glTranslatef(x, currentY, 0);
                GlStateManager.scale(scaleFactor, scaleFactor, 1);
                fr.drawString(line, 0, 0, colour, shadow);
                GlStateManager.scale(1 / scaleFactor, 1 / scaleFactor, 1);
                GL11.glPopMatrix();

                currentY += lineHeight + lineSpacing;
            }
        }
    }


    private static void drawStringCenteredScaled(
            FontRenderer fr,
            String str,
            float x,
            float y,
            boolean shadow,
            int colour,
            float scaleFactor
    ) {

        int strWidth = (int) (fr.getStringWidth(unformatText(str)) * scaleFactor);
        int strHeight = (int) (fr.FONT_HEIGHT * scaleFactor);

        float centerX = x - strWidth / 2f;
        float centerY = y - strHeight / 2f;

        GL11.glPushMatrix();
        GL11.glTranslatef(centerX, centerY, 0);
        GlStateManager.scale(scaleFactor, scaleFactor, 1);
        fr.drawString(str, 0, 0, colour, shadow);
        GlStateManager.scale(1 / scaleFactor, 1 / scaleFactor, 1);
        GL11.glPopMatrix();
    }

    private String formatText(String text) {
        return text.replace("&", "\u00A7");
    }

    private static String unformatText(String formattedString) {
        Pattern FORMAT_CODE_PATTERN = Pattern.compile("§[0-9a-fk-or]");
        Matcher matcher = FORMAT_CODE_PATTERN.matcher(formattedString);
        return matcher.replaceAll("");
    }

    private String getLeftText() {
        Stats stats = new Stats(jsonObject, gamemode);
        StringBuilder sb = new StringBuilder();

        sb.append(formatText("&9" + formatWithCommas(stats.tokens) + " &fTokens\n"))
                .append(formatText("&9" + formatWithCommas(stats.slumberTickets) + " &fTickets\n"))
                .append("\n") // Add a newfc( line
                .append(formatText("&a" + formatWithCommas(stats.wins) + " &fWins\n"))
                .append(formatText("&a" + formatWithCommas(stats.kills) + " &fKills\n"))
                .append(formatText("&a" + formatWithCommas(stats.finalKills) + " &fFinal Kills\n"))
                .append(formatText("&a" + formatWithCommas(stats.beds) + " &fBeds Broken\n"))
                .append("\n") // Add a new line
                .append(formatText("&e" + stats.killsPerGame + " &fKills/Game\n"))
                .append(formatText("&b" + stats.killsPerStar + " &fKills/Star\n"));

        return sb.toString();
    }

    private String getCenterText() {
        Stats stats = new Stats(jsonObject, gamemode);
        StringBuilder sb = new StringBuilder();

        sb.append(formatText("&9" + stats.clutchRatePercent + "% &fClutch Rate\n"))
                .append(formatText("&9" + formatWithCommas(stats.gamesPlayed) + " &fGames Played\n"))
                .append("\n") // Add a newfc( line
                .append(formatText("&c" + formatWithCommas(stats.losses) + " &fLosses\n"))
                .append(formatText("&c" + formatWithCommas(stats.deaths) + " &fDeaths\n"))
                .append(formatText("&c" + formatWithCommas(stats.finalDeaths) + " &fFinal Deaths\n"))
                .append(formatText("&c" + formatWithCommas(stats.bedsLost) + " &fBeds Lost\n"))
                .append("\n") // Add a new line
                .append(formatText("&e" + stats.finalsPerGame + " &fFinals/Game\n"))
                .append(formatText("&b" + stats.finalsPerStar + " &fFinals/Star\n"));

        return sb.toString();
    }

    private String getRightText() {
        Stats stats = new Stats(jsonObject, gamemode);
        StringBuilder sb = new StringBuilder();

        sb.append(formatText("&9" + stats.winRatePercent + "% &fWin Rate\n"))
                .append(formatText("&9" + formatWithCommas(stats.skillIndex) + " &fSkill Index\n"))
                .append("\n") // Add a newfc( line
                .append(formatText("&d" + stats.wlr + " &fWLR\n"))
                .append(formatText("&d" + stats.kdr + " &fKDR\n"))
                .append(formatText("&d" + stats.fkdr + " &fFKDR\n"))
                .append(formatText("&d" + stats.bblr + " &fBBLR\n"))
                .append("\n") // Add a new line
                .append(formatText("&e" + stats.bedsPerGame + " &fBeds/Game\n"))
                .append(formatText("&b" + stats.bedsPerStar + " &fBeds/Star\n"));

        return sb.toString();
    }

    // formats with commas
    public static String formatWithCommas(int number) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        return formatter.format(number);
    }
}
