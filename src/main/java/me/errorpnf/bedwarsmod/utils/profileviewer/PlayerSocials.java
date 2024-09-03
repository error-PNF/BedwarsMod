package me.errorpnf.bedwarsmod.utils.profileviewer;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.JsonObject;
import me.errorpnf.bedwarsmod.utils.RenderUtils;
import me.errorpnf.bedwarsmod.utils.StatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PlayerSocials extends GuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static JsonObject jsonObject;
    private static String username;
    private static FontRenderer fontRenderer;

    private static String[] clickMessage = new String[Social.values().length];
    private static String[] urls = new String[7];
    private static boolean[] conditions = new boolean[Social.values().length];
    private static final List<ResourceLocation> textures = new ArrayList<>();
    private static final List<ResourceLocation> texturesPressed = new ArrayList<>();

    private static final float TEXTURE_SIZE = 15F;
    private static final float SEPARATION = 0F;

    private static boolean[] mouseButtonDown = new boolean[7];

    public PlayerSocials(JsonObject jsonObject, String username, FontRenderer fontRenderer) {
        PlayerSocials.jsonObject = jsonObject;
        PlayerSocials.username = username;
        PlayerSocials.fontRenderer = fontRenderer;

        init();
    }

    private void init() {
        for (Social socialType : Social.values()) {
            conditions[socialType.getKey()] = social(socialType);
            textures.add(new ResourceLocation("bedwarsmod", "textures/socials/" + socialType.getName().toLowerCase() + "_logo.png"));
            texturesPressed.add(new ResourceLocation("bedwarsmod", "textures/socials/" + socialType.getName().toLowerCase() + "_logo_pressed.png"));

            if (socialType.getKey() == 2) { // checks if it's discord to make it say copy instead of visit
                clickMessage[socialType.getKey()] = "§7Click to copy §a" + username + "§7's " + socialType.getPrettyName() + ".\n";
            } else {
                clickMessage[socialType.getKey()] = "§7Click to visit §a" + username + "§7's " + socialType.getPrettyName() + ".\n";
            }
        }
    }

    public void drawTextures(float centerX, float centerY, int mouseX, int mouseY) {
        int numTexturesToDraw = 0;

        for (boolean condition : conditions) {
            if (condition) {
                numTexturesToDraw++;
            }
        }

        if (numTexturesToDraw == 0) {
            RenderUtils.drawStringCentered(fontRenderer, RenderUtils.formatText("&cNo Socials"), centerX, centerY + 0.5f, true, 0);
        }

        float totalWidth = (TEXTURE_SIZE * numTexturesToDraw) + (SEPARATION * (numTexturesToDraw - 1));
        float startX = centerX - (totalWidth / 2.0F);

        float x = startX;
        for (int i = 0; i < conditions.length; i++) {
            if (conditions[i]) {
                float y = centerY - (TEXTURE_SIZE / 2.0F);

                if (mouseButtonDown[i]) {
                    mc.getTextureManager().bindTexture(texturesPressed.get(i));
                } else {
                    mc.getTextureManager().bindTexture(textures.get(i));
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                drawTexturedQuad(x, y, TEXTURE_SIZE, TEXTURE_SIZE);

                boolean isHovered = isMouseOver(mouseX, mouseY, x, y, TEXTURE_SIZE, TEXTURE_SIZE);

                if (isHovered) {
                    String temp = clickMessage[i] + "§b" + urls[i];
                    drawTooltip(temp, mouseX, mouseY);

                    if (Mouse.isButtonDown(0)) {
                        if (!mouseButtonDown[i]) {
                            mouseButtonDown[i] = true;
                        }
                    } else {
                        if (mouseButtonDown[i]) {
                            mouseButtonDown[i] = false;
                            if (clickMessage[i].contains("Discord")) {
                                copyToClipboard(urls[i]);
                                UChat.chat("§7Copied §a" + username + "§7's Discord username.");
                            } else {
                                openLink(urls[i]);
                            }
                        }
                    }
                } else {
                    mouseButtonDown[i] = false; // reset if not hovered
                }
                x += TEXTURE_SIZE + SEPARATION;
            }
        }
    }

    private void drawTexturedQuad(float x, float y, float width, float height) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(x, y + height, 0.0F).tex(0.0D, 1.0D).endVertex();
        worldRenderer.pos(x + width, y + height, 0.0F).tex(1.0D, 1.0D).endVertex();
        worldRenderer.pos(x + width, y, 0.0F).tex(1.0D, 0.0D).endVertex();
        worldRenderer.pos(x, y, 0.0F).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
    }

    private boolean isMouseOver(int mouseX, int mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void openLink(String url) {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                }
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URL syntax: " + url);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyToClipboard(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
    }

    private void drawTooltip(String text, int mouseX, int mouseY) {
        FontRenderer fontRenderer = mc.fontRendererObj;
        List<String> lines = fontRenderer.listFormattedStringToWidth(text, 999); // set max line width
        int maxWidth = 0;
        for (String line : lines) {
            int width = fontRenderer.getStringWidth(line);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        float scale = 0.75f; // tooltip scaling
        int scaledMouseX = (int) (mouseX / scale);
        int scaledMouseY = (int) (mouseY / scale);

        int tooltipX = scaledMouseX + 6;
        int tooltipY = scaledMouseY - 22;
        int tooltipHeight = 8 + (lines.size() - 1) * 10;

        int backgroundColor = 0xF0100010;
        int borderColor = 0x505000FF;
        int borderColorGradient = 0x5028007F;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);

        GlStateManager.translate(0.0f, 0.0f, 1000f); // z fighting gets owned

        // draw background
        drawGradientRect(tooltipX - 3, tooltipY - 4, tooltipX + maxWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);

        // draw borders
        drawGradientRect(tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColor, borderColorGradient);
        drawGradientRect(tooltipX + maxWidth + 2, tooltipY - 3 + 1, tooltipX + maxWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColor, borderColorGradient);
        drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + maxWidth + 3, tooltipY - 3 + 1, borderColor, borderColor);
        drawGradientRect(tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + maxWidth + 3, tooltipY + tooltipHeight + 3, borderColorGradient, borderColorGradient);

        // draw the text
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            fontRenderer.drawStringWithShadow(line, tooltipX, tooltipY + i * 10, -1);
        }

        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private boolean social(Social socialType) {
        StatUtils statUtils = new StatUtils(jsonObject);
        String stat = statUtils.getStat("player.socialMedia.links." + socialType.getName());
        if (!stat.isEmpty() && !stat.equals("0")) {
            urls[socialType.getKey()] = stat;
            return true;
        }
        return false;
    }
}
