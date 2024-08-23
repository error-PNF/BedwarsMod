package net.errorpnf.bedwarsmod.utils;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickChatForStats {

    // borrowed from the legends at this forum post because i couldn't figure out how to make a working regex pattern:
    // https://hypixel.net/threads/solved-is-there-a-way-to-see-a-chat-messages-sender-in-a-forge-mod-1-8-9.5487741/#post-38776127
    public static final Pattern CHAT_PATTERN = Pattern.compile("^(?:\\[[^\\]]+\\] *|)(?:\\[[^\\]]+\\] *|)(?<sender>[0-9A-Za-z_]{1,16}): (?<message>.+)$");
    private static final Pattern FORMAT_PATTERN = Pattern.compile("§.");

    /*@SubscribeEvent
    public void onChatReceivedEvent(ClientChatReceivedEvent event) {
        List<IChatComponent> siblings = event.message.getSiblings();

//        if (event.message.getSiblings().isEmpty()) {
//            return; // Avoid accessing an empty list
//        }

        System.out.println("Chat message has " + siblings.size() + " sibling(s):");
        for (int i = 0; i < siblings.size(); i++) {
            IChatComponent sibling = siblings.get(i);
            System.out.println("Sibling " + (i + 1) + ": " + sibling.getUnformattedText());
            if (sibling.getChatStyle().getChatClickEvent() != null) {
                System.out.println("Chat click?:" + sibling.getChatStyle().getChatClickEvent().getValue());
            }
            if (sibling.getChatStyle().getChatHoverEvent() != null) {
                System.out.println("Chat Hover?:" + sibling.getChatStyle().getChatHoverEvent().getValue());
            }
        }

        IChatComponent lastComponent = event.message.getSiblings().get(event.message.getSiblings().size() - 1);

        if (lastComponent.getChatStyle().getChatClickEvent() != null) {
            if (lastComponent.getChatStyle().getChatClickEvent().getValue().startsWith("/socialoptions")) {
                lastComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bws " + getUsername(event.message.getUnformattedText())));
                lastComponent.getChatStyle().setChatHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentText("§cView the stats of §e" + getUsername(event.message.getUnformattedText()))
                        )
                );
            }
        }
    }*/

    @SubscribeEvent
    public void onChatReceivedEvent(ClientChatReceivedEvent event) {
        if (event.message.getSiblings().isEmpty()) {
            return; // Avoid accessing an empty list
        }

        IChatComponent lastComponent = event.message.getSiblings().get(event.message.getSiblings().size() - 1);
        ChatStyle style = lastComponent.getChatStyle();
        ClickEvent clickEvent = lastComponent.getChatStyle().getChatClickEvent();

        if (clickEvent != null) {
            if (!clickEvent.getValue().startsWith("/socialoptions")) {
                return;
            }
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
