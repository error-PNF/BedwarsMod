package net.errorpnf.bedwarsmod.commands;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.JsonObject;
import net.errorpnf.bedwarsmod.BedwarsMod;
import net.errorpnf.bedwarsmod.data.GameModeEnum;
import net.errorpnf.bedwarsmod.data.apicache.ApiCacheManager;
import net.errorpnf.bedwarsmod.utils.ApiUtils;
import net.errorpnf.bedwarsmod.utils.StatUtils;
import net.errorpnf.bedwarsmod.utils.formatting.RankUtils;
import net.errorpnf.bedwarsmod.utils.profileviewer.PVGui;
import net.errorpnf.bedwarsmod.utils.profileviewer.PlayerSocials;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class PVCommand extends CommandBase {
    public static final String pfx = "&c[&fBW&c] &r";

    @Override
    public String getCommandName() {
        return "pv";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "does shit";
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

            //UChat.chat("&aUsing cached data for &3" + RankUtils.formatRankAndUsername(displayUsername, cachedData) + "&a.");

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
                UChat.chat("&cError fetching data for &a" + username);
                return null;
            });
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
