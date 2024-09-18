package me.errorpnf.bedwarsmod.commands;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.JsonObject;
import me.errorpnf.bedwarsmod.utils.ApiUtils;
import me.errorpnf.bedwarsmod.BedwarsMod;
import me.errorpnf.bedwarsmod.data.GameModeEnum;
import me.errorpnf.bedwarsmod.data.apicache.ApiCacheManager;
import me.errorpnf.bedwarsmod.utils.StatUtils;
import me.errorpnf.bedwarsmod.features.profileviewer.PVGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PVCommand extends CommandBase {
    public static final String pfx = BedwarsMod.prefix;

    @Override
    public String getCommandName() {
        return "pv";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("profileviewer", "bedwarsprofileviewer", "bwprofileviewer");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/pv <username>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        String username;
        if (args.length < 1) {
            username = Minecraft.getMinecraft().thePlayer.getName();
        } else {
            username = args[0];
        }

        GameModeEnum gamemode = GameModeEnum.OVERALL;

        JsonObject cachedData = ApiCacheManager.getCachedRequest(username);
        if (cachedData != null) {
            StatUtils s = new StatUtils(cachedData);
            String displayUsername = s.getStat("player.displayname");

            BedwarsMod.INSTANCE.openGui = new PVGui(displayUsername, cachedData, gamemode);
        } else {
            UChat.chat("&aFetching stats for &3" + username + "&a...");
            ApiUtils.hypixelApiRequest(username).thenAccept(jsonObject -> {
                if (jsonObject != null) {
                    StatUtils s = new StatUtils(jsonObject);
                    if (!s.getStat("player.displayname").equals("Stat not found")) {
                        ApiCacheManager.cacheRequest(username, jsonObject);

                        String displayUsername = s.getStat("player.displayname");

                        BedwarsMod.INSTANCE.openGui = new PVGui(displayUsername, jsonObject, gamemode);
                    } else {
                        UChat.chat("&cError fetching data for &a" + username + "&c. Did you spell their username correctly?");
                    }
                }
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
        }
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
