package net.errorpnf.bedwarsmod.utils;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import net.errorpnf.bedwarsmod.config.BedwarsModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FinalKillHearts {

    private static ResourceLocation ICONS = new ResourceLocation("textures/gui/icons.png");

    public static boolean isFinalKill = false;

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (BedwarsModConfig.shouldHardcore && isFinalKill) {
            if (event.type == RenderGameOverlayEvent.ElementType.HEALTH) {
                GlStateManager.pushMatrix();
                mc.getTextureManager().bindTexture(ICONS);

                // render the hearts using hardcore texture part
                renderHardcoreHearts(event, mc);
                GlStateManager.popMatrix();

                // cancel the original event to prevent double rendering
                event.setCanceled(true);
            } else if (event.type == RenderGameOverlayEvent.ElementType.ARMOR) {
                event.setCanceled(true);

                // Render the armor bar above the health bar
                renderArmorBar(event, mc);
            }
        }
    }

    private void renderHardcoreHearts(RenderGameOverlayEvent.Pre event, Minecraft mc) {
        Gui gui = mc.ingameGUI;
        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();
        int updateCounter = mc.ingameGUI.getUpdateCounter();
        boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

        if (highlight) {
            GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);
        } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

        float health = mc.thePlayer.getHealth();
        float maxHealth = (float)mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
        float absorb = mc.thePlayer.getAbsorptionAmount();

        int totalHearts = MathHelper.ceiling_float_int((maxHealth + absorb) / 2.0F);
        int healthRows = MathHelper.ceiling_float_int((maxHealth + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);

        for (int i = totalHearts - 1; i >= 0; --i) {
            int row = MathHelper.ceiling_float_int((float)(i + 1) / 10.0F) - 1;
            int x = width / 2 - 91 + i % 10 * 8;
            int y = height - 39 - row * rowHeight;

            // Determine the texture coordinates for the current heart
            int u = 52; // Default texture coordinates for regular hearts
            int v = 45;

            // Adjust texture for absorption hearts
            if (i >= MathHelper.ceiling_float_int(maxHealth / 2.0F)) {
                // Absorption hearts
                u = 160;
                v = 45;
            }

            // Adjust position if health is low
            if (health <= 4) {
                y += updateCounter % 2 == 0 ? 1 : 0;
            }

            // Draw the hearts
            gui.drawTexturedModalRect(x, y, u, v, 9, 9);
        }
    }


    private void renderArmorBar(RenderGameOverlayEvent.Pre event, Minecraft mc) {
        Gui gui = mc.ingameGUI;
        int width = event.resolution.getScaledWidth();
        int height = event.resolution.getScaledHeight();
        int left = width / 2 - 91;

        // Calculate the height of the health bar considering health and absorption
        float health = mc.thePlayer.getHealth();
        float maxHealth = (float)mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
        float absorb = mc.thePlayer.getAbsorptionAmount();
        int healthRows = MathHelper.ceiling_float_int((maxHealth + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        int healthBarHeight = 39 + (healthRows - 1) * rowHeight;  // Bottom of the health bar

        int top = height - healthBarHeight - 10;  // Position the armor bar above the health bar

        int level = mc.thePlayer.getTotalArmorValue();

        for (int i = 0; i < 10; ++i) {
            if (level > 0) {
                int x = left + i * 8;
                int y = top;

                if (i * 2 + 1 < level) {
                    gui.drawTexturedModalRect(x, y, 34, 9, 9, 9);
                }
                if (i * 2 + 1 == level) {
                    gui.drawTexturedModalRect(x, y, 25, 9, 9, 9);
                }
                if (i * 2 + 1 > level) {
                    gui.drawTexturedModalRect(x, y, 16, 9, 9, 9);
                }
            }
        }
    }

    @SubscribeEvent
    public void processCommand(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc == null) {
            return;
        } else if (mc.thePlayer == null) {
            return;
        } else if (mc.theWorld == null) {
            return;
        }

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            return;
        }

        ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);
        if (sidebar != null) {
            List<Score> scores = new ArrayList<>(scoreboard.getScores());
            scores.sort(Comparator.comparingInt(Score::getScorePoints).reversed());

            List<String> found = scores.stream()
                    .filter(score -> score.getObjective().getName().equals(sidebar.getName()))
                    .map(score -> score.getPlayerName() + getSuffixFromContainingTeam(scoreboard, score.getPlayerName()))
                    .collect(Collectors.toList());

            for (int i = 0; i < found.size(); i++) {
                Pattern pattern = Pattern.compile("§a[0-9]§7 YOU");
                Matcher matcher = pattern.matcher(found.get(i));

                if (found.get(i).endsWith("§a§l✓§7 YOU")) {
                    isFinalKill = false;
                    return;
                } else if (matcher.find()) {
                    isFinalKill = true;
                    return;
                } else {
                    isFinalKill = false;
                }
            }
        }
    }

    private String getSuffixFromContainingTeam(Scoreboard scoreboard, String playerName) {
        for (ScorePlayerTeam team : scoreboard.getTeams()) {
            if (team != null && team.getMembershipCollection().contains(playerName)) {
                return team.getColorPrefix() + team.getColorSuffix();
            }
        }
        return "";
    }
}
