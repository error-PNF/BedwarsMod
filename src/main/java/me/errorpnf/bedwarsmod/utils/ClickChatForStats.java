package me.errorpnf.bedwarsmod.utils;

import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickChatForStats {

    // borrowed from the legends at this forum post because i couldn't figure out how to make a working regex pattern:
    // https://hypixel.net/threads/solved-is-there-a-way-to-see-a-chat-messages-sender-in-a-forge-mod-1-8-9.5487741/#post-38776127
    public static final Pattern CHAT_PATTERN = Pattern.compile("^(?:\\[[^\\]]+\\] *|)(?:\\[[^\\]]+\\] *|)(?<sender>[0-9A-Za-z_]{1,16}): (?<message>.+)$");
    private static final Pattern FORMAT_PATTERN = Pattern.compile("§.");

    @SubscribeEvent
    public void onChatReceivedEvent(ClientChatReceivedEvent event) {
        if (!HypixelUtils.INSTANCE.isHypixel()) return;
        if (HypixelLocraw.rawGameType.isEmpty() || !HypixelLocraw.rawGameType.contains("BEDWARS") || !HypixelLocraw.gamemode.contains("lobby") || event.message.getSiblings().isEmpty()) {
            return;
        }

        IChatComponent lastComponent = event.message.getSiblings().get(event.message.getSiblings().size() - 1);
        ChatStyle style = lastComponent.getChatStyle();
        ClickEvent clickEvent = lastComponent.getChatStyle().getChatClickEvent();

        if (clickEvent != null) {
            if (!clickEvent.getValue().startsWith("/socialoptions")) {
                return;
            }
        }

        if (getUsername(event.message.getUnformattedText()) == null) {
            return;
        }

        style.setChatClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/bws " + getUsername(event.message.getUnformattedText())));

        style.setChatHoverEvent(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText("§aView the stats of §3" + getUsername(event.message.getUnformattedText()))));
    }

    public static String getUsername(String chatMessage) {
        String msg = chatMessage.replaceAll(FORMAT_PATTERN.pattern(), "");
        Matcher matcher = CHAT_PATTERN.matcher(msg);
        if (matcher.matches()) {
            String sender = matcher.group("sender");
            String message = matcher.group("message");
            return sender;
        }
        return null;
    }
}
