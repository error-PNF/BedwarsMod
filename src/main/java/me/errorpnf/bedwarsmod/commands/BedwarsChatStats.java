package me.errorpnf.bedwarsmod.commands;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.JsonObject;
import me.errorpnf.bedwarsmod.BedwarsMod;
import me.errorpnf.bedwarsmod.utils.ApiUtils;
import me.errorpnf.bedwarsmod.data.GameModeEnum;
import me.errorpnf.bedwarsmod.data.PrestigeList;
import me.errorpnf.bedwarsmod.data.apicache.ApiCacheManager;
import me.errorpnf.bedwarsmod.utils.StatUtils;
import me.errorpnf.bedwarsmod.utils.formatting.PrintChatStats;
import me.errorpnf.bedwarsmod.utils.formatting.RankUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BedwarsChatStats extends CommandBase {
    public static final String pfx = BedwarsMod.prefix;

    @Override
    public String getCommandName() {
        return "bedwars";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("bws", "bwstats");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bedwars <username>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        String username;
        GameModeEnum gamemode;
        if (args.length < 1) {
            username = Minecraft.getMinecraft().thePlayer.getName();
        } else {
            username = args[0];
        }

        if (!(args.length < 2)) {
            if (args[1].equalsIgnoreCase("fours") || args[1].equalsIgnoreCase("4s")) {
                gamemode = GameModeEnum.FOUR_FOUR;
            } else if (args[1].equalsIgnoreCase("threes") || args[1].equalsIgnoreCase("3s")) {
                gamemode = GameModeEnum.FOUR_THREE;
            } else if (args[1].equalsIgnoreCase("twos") || args[1].equalsIgnoreCase("2s") || args[1].equalsIgnoreCase("duos") || args[1].equalsIgnoreCase("doubles")) {
                gamemode = GameModeEnum.EIGHT_TWO;
            } else if (args[1].equalsIgnoreCase("ones") || args[1].equalsIgnoreCase("1s") || args[1].equalsIgnoreCase("solo") || args[1].equalsIgnoreCase("solos")) {
                gamemode = GameModeEnum.EIGHT_ONE;
            } else {
                gamemode = GameModeEnum.OVERALL;
            }
        } else {
            gamemode = GameModeEnum.OVERALL;
        }

        JsonObject cachedData = ApiCacheManager.getCachedRequest(username);
        if (cachedData != null) {
            PrintChatStats.printChatStats(username, cachedData, gamemode);
        } else {
            UChat.chat("&aFetching Stats for &3" + username + "&a...");
            ApiUtils.hypixelApiRequest(username).thenAccept(jsonObject -> {
                ApiCacheManager.cacheRequest(username, jsonObject);

                PrintChatStats.printChatStats(username, jsonObject, gamemode);
            }).exceptionally(throwable -> {
                throwable.printStackTrace();
                UChat.chat("&cError fetching data for &a" + username + "&c. Did you spell their username correctly?");
                return null;
            });
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            for (NetworkPlayerInfo info : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
                playerNames.add(info.getGameProfile().getName());
            }
            return getListOfStringsMatchingLastWord(args, playerNames);
        } else if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "overall", "4s", "3s", "2s", "1s");
        }
        return null;
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
