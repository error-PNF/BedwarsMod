package net.errorpnf.bedwarsmod.commands;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import net.errorpnf.bedwarsmod.utils.ApiUtils;
import net.errorpnf.bedwarsmod.utils.StatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class BedwarsChatStats extends CommandBase {
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

        if (args.length < 1) {
            throw new CommandException("Usage: " + getCommandUsage(sender));
        }

        String username = args[0];
        UChat.chat("&aFetching Stats for &3" + username + "&a...");

        ApiUtils.hypixelApiRequest(username).thenAccept(jsonObject -> {
            StatUtils s = new StatUtils(jsonObject);

            UChat.chat("&aYou are viewing &3" + username + "'s &astats!");
            UChat.chat("&aBedwars Level: &3" + s.getStat("player.achievements.bedwars_level") + "✫");
            UChat.chat("&aBedwars Kills: &3" + s.getStat("player.stats.Bedwars.kills_bedwars"));
            UChat.chat("&aBedwars Deaths: &3" + s.getStat("player.stats.Bedwars.deaths_bedwars"));
            UChat.chat("&aBedwars Wins: &3" + s.getStat("player.achievements.bedwars_wins"));
            UChat.chat("&aBedwars Losses: &3" + s.getStat("player.stats.Bedwars.losses"));

        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });




//        CompletableFuture<UUID> futureUUID = ApiUtils.getPlayerUUIDAsync(username);
//        futureUUID.thenAccept(uuid -> {
//            UChat.chat("&aUUID for " + username + ": " + uuid.toString());
//        }).exceptionally(throwable -> {
//            UChat.chat("Failed to fetch UUID for " + username + ": " + throwable.getMessage());
//            return null;
//        });
















//        uuidFuture.thenCompose(playerUuid -> {
//            return CompletableFuture.supplyAsync(() -> {
//                try {
//                    return ApiUtils.API.getPlayerByUuid(playerUuid).get();
//                } catch (ExecutionException | InterruptedException e) {
//                    throw new CompletionException(e);
//                }
//            });
//        }).thenAccept(apiReply -> {
//            PlayerReply.Player player = apiReply.getPlayer();
//            if (!player.exists()) {
//                UChat.chat("Player not found!");
//                return;
//            }
//
//            UChat.chat("You are viewing " + player.getName() + "'s stats!");
//            UChat.chat("");
//            UChat.chat("UUID: " + player.getUuid());
//            UChat.chat("Bedwars Level: " + player.getIntProperty("achievements.bedwars_level", 0) + "✫");
//            UChat.chat("Bedwars Kills: " + player.getIntProperty("stats.Bedwars.kills_bedwars", 0));
//            UChat.chat("Bedwars Deaths: " + player.getIntProperty("stats.Bedwars.deaths_bedwars", 0));
//            UChat.chat("Bedwars Wins: " + player.getIntProperty("achievements.bedwars_wins", 0));
//            UChat.chat("Bedwars Losses: " + player.getIntProperty("stats.Bedwars.wins_bedwars", 0));
//
//            UChat.chat("Rate Limit: " + apiReply.getRateLimit());
//        }).exceptionally(ex -> {
//            UChat.chat("Oh no, something went wrong!");
//            ex.printStackTrace();
//            return null;
//        });
    }
}
