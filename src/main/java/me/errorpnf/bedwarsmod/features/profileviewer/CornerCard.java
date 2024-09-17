package me.errorpnf.bedwarsmod.features.profileviewer;

import com.google.gson.JsonObject;
import me.errorpnf.bedwarsmod.data.ILeveling;
import me.errorpnf.bedwarsmod.utils.RenderUtils;
import me.errorpnf.bedwarsmod.utils.StatUtils;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import net.minecraft.client.gui.FontRenderer;

public class CornerCard {
    public CornerCard() {}

    public void drawCard(float x, float y, FontRenderer fontRenderer, JsonObject jsonObject) {
        StatUtils stats = new StatUtils(jsonObject);

        double networkExp = Double.parseDouble(stats.getStat("player.networkExp"));
        int networkLevel = (int) ILeveling.getLevel(networkExp);

        int karma = Integer.parseInt(stats.getStat("player.karma"));
        int ranksGifted = Integer.parseInt(stats.getStat("player.giftingMeta.ranksGiven"));
        int achievementPoints = Integer.parseInt(stats.getStat("player.achievementPoints"));


        RenderUtils.drawStringLeftAligned(
                fontRenderer,
                "§6NW Level: §f" + FormatUtils.formatCommas(networkLevel),
                x,
                y - 5f,
                0,
                true
        );
        RenderUtils.drawStringLeftAligned(
                fontRenderer,
                "§dKarma: §f" + FormatUtils.formatCommas(karma),
                x,
                y + 7f,
                0,
                true
        );
        RenderUtils.drawStringLeftAligned(
                fontRenderer,
                "§9Gifted: §f" + FormatUtils.formatCommas(ranksGifted),
                x,
                y + 19f,
                0,
                true
        );
        RenderUtils.drawStringLeftAligned(
                fontRenderer,
                "§eAP: §f" + FormatUtils.formatCommas(achievementPoints),
                x,
                y + 31f,
                0,
                true
        );
    }
}
