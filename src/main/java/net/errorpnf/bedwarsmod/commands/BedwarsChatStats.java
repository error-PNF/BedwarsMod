package net.errorpnf.bedwarsmod.commands;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.errorpnf.bedwarsmod.data.GameModeEnum;
import net.errorpnf.bedwarsmod.data.PrestigeList;
import net.errorpnf.bedwarsmod.data.apicache.ApiCacheManager;
import net.errorpnf.bedwarsmod.utils.ApiUtils;
import net.errorpnf.bedwarsmod.utils.StatUtils;
import net.errorpnf.bedwarsmod.utils.UUIDUtils;
import net.errorpnf.bedwarsmod.utils.formatting.PrintChatStats;
import net.errorpnf.bedwarsmod.utils.formatting.RankUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;

import java.util.UUID;

public class BedwarsChatStats extends CommandBase {
    public static final String pfx = "&c[&fBW&c] &r";

    @Override
    public String getCommandName() {
        return "bws";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        String username;
        if (args.length < 1) {
            username = Minecraft.getMinecraft().thePlayer.getName();
        } else {
            username = args[0];
        }

        JsonObject cachedData = ApiCacheManager.getCachedRequest(username);
        if (cachedData != null) {
            StatUtils s = new StatUtils(cachedData);
            String displayUsername = s.getStat("player.displayname");

            UChat.chat("&aUsing cached data for &3" + RankUtils.formatRankAndUsername(displayUsername, cachedData) + "&a.");

            PrintChatStats.printChatStats(username, cachedData);
        } else {
            UChat.chat("&aFetching Stats for &3" + username + "&a...");
            ApiUtils.hypixelApiRequest(username).thenAccept(jsonObject -> {
                ApiCacheManager.cacheRequest(username, jsonObject);

                PrintChatStats.printChatStats(username, jsonObject);
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                UChat.chat("&cError fetching data for &a" + username + "&c. Did you spell their username correctly?");
                return null;
            });
        }
    }

    private void prettyStats(String username, JsonObject jsonObject) {
        StatUtils s = new StatUtils(jsonObject);
        PrestigeList prestigeList = new PrestigeList();
        String formattedStar = prestigeList.getPrestige(Integer.parseInt(s.getStat("player.achievements.bedwars_level")));

        UChat.chat(pfx + "&7Overall stats for " + RankUtils.formatRankAndUsername(username, jsonObject));
        UChat.chat(pfx + "&7Bedwars Level: &a" +  formattedStar);
        UChat.chat(pfx + "&7Bedwars Kills: &a" + s.getStat("player.stats.Bedwars.kills_bedwars"));
        UChat.chat(pfx + "&7Bedwars Deaths: &a" + s.getStat("player.stats.Bedwars.deaths_bedwars"));
        UChat.chat(pfx + "&7Bedwars Wins: &a" + s.getStat("player.achievements.bedwars_wins"));
        UChat.chat(pfx + "&7Bedwars Losses: &a" + s.getStat("player.stats.Bedwars.losses_bedwars"));
        UChat.chat("");
        UChat.chat(s.getStat("player.socialMedia.links.TIKTOK"));
        UChat.chat(s.getStat("player.socialMedia.links.DISCORD"));
        UChat.chat(s.getStat("player.socialMedia.links.HYPIXEL"));
        UChat.chat(s.getStat("player.socialMedia.links.YOUTUBE"));
    }
}
