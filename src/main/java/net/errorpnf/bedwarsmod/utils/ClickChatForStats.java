package net.errorpnf.bedwarsmod.utils;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickChatForStats {

/*    @SubscribeEvent
    public void onChatReceivedEvent(ClientChatReceivedEvent event) {
        IChatComponent lastComponent = event.message.getSiblings().get(event.message.getSiblings().size() - 1);
        String sender =
        if (lastComponent.getChatStyle().getChatClickEvent() != null) {
            if (!lastComponent.getChatStyle().getChatClickEvent().getValue().startsWith("/socialoptions")) {
                return;
            }
        }
        lastComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stats " + event.message.));
        lastComponent.getChatStyle().setChatHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText("§aView the stats of §3" + event.getPlayer().getName())
                )
        );
    }*/
}
