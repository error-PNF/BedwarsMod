package net.errorpnf.bedwarsmod.utils.profileviewer;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.JsonObject;
import net.errorpnf.bedwarsmod.utils.StatUtils;
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
import java.util.List;

public class PlayerSocials extends GuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static JsonObject jsonObject;
    private static String username;

    public PlayerSocials(JsonObject jsonObject, String username) {
        PlayerSocials.jsonObject = jsonObject;
        PlayerSocials.username = username;

        clickMessage = new String[]{
                "§7Click to visit §a" + username + "§7's TikTok.\n",
                "§7Click to visit §a" + username + "§7's Twitch.\n",
                "§7Click to copy §a" + username + "§7's Discord.\n",
                "§7Click to visit §a" + username + "§7's Hypixel Forums.\n",
                "§7Click to visit §a" + username + "§7's Twitter.\n",
                "§7Click to visit §a" + username + "§7's YouTube.\n",
                "§7Click to visit §a" + username + "§7's Instagram.\n"
        };
    }


    private static final ResourceLocation[] textures = {
            new ResourceLocation("bedwarsmod", "textures/socials/tiktok_logo.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/twitch_logo.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/discord_logo.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/hypixel_logo.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/twitter_logo.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/youtube_logo.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/instagram_logo.png")
    };

    private static final ResourceLocation[] texturesPressed = {
            new ResourceLocation("bedwarsmod", "textures/socials/tiktok_logo_pressed.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/twitch_logo_pressed.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/discord_logo_pressed.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/hypixel_logo_pressed.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/twitter_logo_pressed.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/youtube_logo_pressed.png"),
            new ResourceLocation("bedwarsmod", "textures/socials/instagram_logo_pressed.png")
    };

    private static String[] clickMessage = {};

    private boolean[] conditions = {
            tiktok(),
            twitch(),
            discord(),
            hypixel(),
            twitter(),
            youtube(),
            instagram()
    };


    private static String[] urls = new String[7];

    private static final float TEXTURE_SIZE = 15F;
    private static final float SEPARATION = 0F;

    private static boolean[] mouseButtonDown = new boolean[7];


    public void drawTextures(float centerX, float centerY, int mouseX, int mouseY) {
        int numTexturesToDraw = 0;

        for (boolean condition : conditions) {
            if (condition) {
                numTexturesToDraw++;
            }
        }

        float totalWidth = (TEXTURE_SIZE * numTexturesToDraw) + (SEPARATION * (numTexturesToDraw - 1));
        float startX = centerX - (totalWidth / 2.0F);

        float x = startX;
        for (int i = 0; i < textures.length; i++) {
            if (conditions[i]) {
                float y = centerY - (TEXTURE_SIZE / 2.0F);

                if (mouseButtonDown[i]) {
                    mc.getTextureManager().bindTexture(texturesPressed[i]);
                } else {
                    mc.getTextureManager().bindTexture(textures[i]);
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
            // Ensure URL starts with "https://"
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            // Check if the desktop API is supported and if browsing is supported
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


    private boolean tiktok() {
        StatUtils s = new StatUtils(jsonObject);
        String stat = s.getStat("player.socialMedia.links.TIKTOK");
        if (!stat.isEmpty() && !stat.equals("0")) {
            urls[0] = stat;
            return true;
        }
        return false;
    }

    private boolean twitch() {
        StatUtils s = new StatUtils(jsonObject);
        String stat = s.getStat("player.socialMedia.links.TWITCH");
        if (!stat.isEmpty() && !stat.equals("0")) {
            urls[1] = stat;
            return true;
        }
        return false;
    }

    private boolean discord() {
        StatUtils s = new StatUtils(jsonObject);
        String stat = s.getStat("player.socialMedia.links.DISCORD");
        if (!stat.isEmpty() && !stat.equals("0")) {
            urls[2] = stat;
            return true;
        }
        return false    ;
    }

    private boolean hypixel() {
        StatUtils s = new StatUtils(jsonObject);
        String stat = s.getStat("player.socialMedia.links.HYPIXEL");
        if (!stat.isEmpty() && !stat.equals("0")) {
            urls[3] = stat;
            return true;
        }
        return false;
    }

    private boolean twitter() {
        StatUtils s = new StatUtils(jsonObject);
        String stat = s.getStat("player.socialMedia.links.TWITTER");
        if (!stat.isEmpty() && !stat.equals("0")) {
            urls[4] = stat;
            return true;
        }
        return false;
    }

    private boolean youtube() {
        StatUtils s = new StatUtils(jsonObject);
        String stat = s.getStat("player.socialMedia.links.YOUTUBE");
        if (!stat.isEmpty() && !stat.equals("0")) {
            urls[5] = stat;
            return true;
        }
        return false;
    }

    private boolean instagram() {
        StatUtils s = new StatUtils(jsonObject);
        String stat = s.getStat("player.socialMedia.links.INSTAGRAM");
        if (!stat.isEmpty() && !stat.equals("0")) {
            urls[6] = stat;
            return true;
        }
        return false;
    }
}
