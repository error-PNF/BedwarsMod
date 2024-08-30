package net.errorpnf.bedwarsmod.utils.profileviewer;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import cc.polyfrost.oneconfig.libs.universal.wrappers.message.UTextComponent;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.errorpnf.bedwarsmod.data.BedwarsExperience;
import net.errorpnf.bedwarsmod.data.PrestigeList;
import net.errorpnf.bedwarsmod.data.apicache.ApiCacheManager;
import net.errorpnf.bedwarsmod.data.stats.Stats;
import net.errorpnf.bedwarsmod.utils.ApiUtils;
import net.errorpnf.bedwarsmod.utils.JsonUtils;
import net.errorpnf.bedwarsmod.utils.StatUtils;
import net.errorpnf.bedwarsmod.utils.UUIDUtils;
import net.errorpnf.bedwarsmod.utils.formatting.RankUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

public class PVGui extends GuiScreen {
    private final int sizeX = 430;
    private final int sizeY = 224;
    private GuiButton button;
    private static int guiLeft;
    private static int guiTop;
    public static final ResourceLocation pv_bg = new ResourceLocation("bedwarsmod:textures/gui/background.png");
    public EntityOtherPlayerMP entityPlayer = null;
    private ResourceLocation playerLocationSkin = null;
    private ResourceLocation playerLocationCape = null;
    private String skinType = null;


    private String username;
    private JsonObject playerData;

    public PVGui(String username, JsonObject playerData) {
        this.username = username;
        this.playerData = playerData;
    }


    @Override
    public void initGui() {
        super.initGui();
        button = new GuiButton(0, 20, 10, "butts");
        buttonList.add(button);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        guiLeft = (this.width - this.sizeX) / 2;
        guiTop = (this.height - this.sizeY) / 2;

        super.drawScreen(mouseX, mouseY, partialTicks);
        drawDefaultBackground();

        blurBackground();
        renderBlurredBackground(width, height, guiLeft + 2, guiTop + 2, sizeX - 4, sizeY - 4);

        GlStateManager.enableDepth();
        GlStateManager.translate(0, 0, 5);
        GlStateManager.translate(0, 0, -3);

        GlStateManager.disableDepth();
        GlStateManager.translate(0, 0, -2);
        GlStateManager.translate(0, 0, 2);

        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        //GlStateManager.enableBlend();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);

        Minecraft.getMinecraft().getTextureManager().bindTexture(pv_bg);
        drawTexturedRect(guiLeft, guiTop, sizeX, sizeY, GL11.GL_NEAREST);

        renderDrawnEntity(mouseX, mouseY, partialTicks);

        renderTopCard(guiLeft + (365f * 2f/3f), guiTop + 20, 2, mouseX, mouseY);
        PlayerSocials playerSocials = new PlayerSocials(playerData, username);
        playerSocials.drawTextures(guiLeft + 61.75f, guiTop + 181.875f, mouseX, mouseY);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == this.button) {
            UChat.chat("Button has been pressed");
        }
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }


    // The following code has been taken from NEU
    // https://github.com/Moulberry/NotEnoughUpdates/blob/master/src/main/java/io/github/moulberry/notenoughupdates/profileviewer/GuiProfileViewer.java

    public void renderBlurredBackground(int width, int height, int x, int y, int blurWidth, int blurHeight) {
        if (!OpenGlHelper.isFramebufferEnabled()) return;

        float uMin = x / (float) width;
        float uMax = (x + blurWidth) / (float) width;
        float vMin = (height - y) / (float) height;
        float vMax = (height - y - blurHeight) / (float) height;

        blurOutputVert.bindFramebufferTexture();
        GlStateManager.color(1f, 1f, 1f, 1f);
        //Utils.setScreen(width*f, height*f, f);
        drawTexturedRect(x, y, blurWidth, blurHeight, uMin, uMax, vMin, vMax);
        //Utils.setScreen(width, height, f);
        blurOutputVert.unbindFramebufferTexture();
    }


    public static void drawTexturedRect(float x, float y, float width, float height, int filter) {
        drawTexturedRect(x, y, width, height, 0, 1, 0, 1, filter);
    }

    public static void drawTexturedRect(
            float x,
            float y,
            float width,
            float height,
            float uMin,
            float uMax,
            float vMin,
            float vMax
    ) {
        drawTexturedRect(x, y, width, height, uMin, uMax, vMin, vMax, GL11.GL_LINEAR);
    }

    public static void drawTexturedRect(
            float x,
            float y,
            float width,
            float height,
            float uMin,
            float uMax,
            float vMin,
            float vMax,
            int filter
    ) {
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ONE_MINUS_SRC_ALPHA
        );
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer
                .pos(x, y + height, 0.0D)
                .tex(uMin, vMax).endVertex();
        worldrenderer
                .pos(x + width, y + height, 0.0D)
                .tex(uMax, vMax).endVertex();
        worldrenderer
                .pos(x + width, y, 0.0D)
                .tex(uMax, vMin).endVertex();
        worldrenderer
                .pos(x, y, 0.0D)
                .tex(uMin, vMin).endVertex();
        tessellator.draw();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GlStateManager.disableBlend();
    }

    private Matrix4f createProjectionMatrix(int width, int height) {
        Matrix4f projMatrix = new Matrix4f();
        projMatrix.setIdentity();
        projMatrix.m00 = 2.0F / (float) width;
        projMatrix.m11 = 2.0F / (float) (-height);
        projMatrix.m22 = -0.0020001999F;
        projMatrix.m33 = 1.0F;
        projMatrix.m03 = -1.0F;
        projMatrix.m13 = 1.0F;
        projMatrix.m23 = -1.0001999F;
        return projMatrix;
    }

    Shader blurShaderHorz = null;
    Framebuffer blurOutputHorz = null;
    Shader blurShaderVert = null;
    Framebuffer blurOutputVert = null;
    private double lastBgBlurFactor = -1;

    private void blurBackground() {
        if (!OpenGlHelper.isFramebufferEnabled()) return;

        int width = Minecraft.getMinecraft().displayWidth;
        int height = Minecraft.getMinecraft().displayHeight;

        if (blurOutputHorz == null) {
            blurOutputHorz = new Framebuffer(width, height, false);
            blurOutputHorz.setFramebufferFilter(GL11.GL_NEAREST);
        }
        if (blurOutputVert == null) {
            blurOutputVert = new Framebuffer(width, height, false);
            blurOutputVert.setFramebufferFilter(GL11.GL_NEAREST);
        }
        if (blurOutputHorz.framebufferWidth != width || blurOutputHorz.framebufferHeight != height) {
            blurOutputHorz.createBindFramebuffer(width, height);
            blurShaderHorz.setProjectionMatrix(createProjectionMatrix(width, height));
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        }
        if (blurOutputVert.framebufferWidth != width || blurOutputVert.framebufferHeight != height) {
            blurOutputVert.createBindFramebuffer(width, height);
            blurShaderVert.setProjectionMatrix(createProjectionMatrix(width, height));
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        }

        if (blurShaderHorz == null) {
            try {
                blurShaderHorz =
                        new Shader(
                                Minecraft.getMinecraft().getResourceManager(),
                                "blur",
                                Minecraft.getMinecraft().getFramebuffer(),
                                blurOutputHorz
                        );
                blurShaderHorz.getShaderManager().getShaderUniform("BlurDir").set(1, 0);
                blurShaderHorz.setProjectionMatrix(createProjectionMatrix(width, height));
            } catch (Exception ignored) {
            }
        }
        if (blurShaderVert == null) {
            try {
                blurShaderVert = new Shader(
                        Minecraft.getMinecraft().getResourceManager(),
                        "blur",
                        blurOutputHorz,
                        blurOutputVert
                );
                blurShaderVert.getShaderManager().getShaderUniform("BlurDir").set(0, 1);
                blurShaderVert.setProjectionMatrix(createProjectionMatrix(width, height));
            } catch (Exception ignored) {
            }
        }
        if (blurShaderHorz != null && blurShaderVert != null) {
            if (15 != lastBgBlurFactor) {
                blurShaderHorz.getShaderManager().getShaderUniform("Radius").set((float) 15);
                blurShaderVert.getShaderManager().getShaderUniform("Radius").set((float) 15);
                lastBgBlurFactor = 15;
            }
            GL11.glPushMatrix();
            blurShaderHorz.loadShader(0);
            blurShaderVert.loadShader(0);
            GlStateManager.enableDepth();
            GL11.glPopMatrix();

            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
        }
    }

    private void renderLiving(EntityLivingBase ent, float x, float y, float scale, int rotation) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((double) x + (40 * scale), (double) y + (107) * scale, 50.0);
        GlStateManager.scale(-(scale * 50), scale * 50, scale * 50);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        float actualRotation = 360F - rotation;
        ent.renderYawOffset = actualRotation;
        ent.rotationYaw = actualRotation;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager rendermanager = mc.getRenderManager();
        rendermanager.playerViewX = 0f;
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
    }


    private void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float renderYawOffset = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(25, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float) Math.atan(mouseX / 40.0F) * 20.0F;
        ent.rotationYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
        ent.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);

        ent.renderYawOffset = renderYawOffset;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private void renderDrawnEntity(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.color(1f, 1f, 1f, 1f);
        renderLiving(Minecraft.getMinecraft().thePlayer, sizeX, sizeY, sizeX, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.popMatrix();



        if (entityPlayer == null) {
            JsonObject apiReq = playerData;
            StatUtils profile = new StatUtils(apiReq);
            UUID formattedPlayerUUID = UUID.fromString("c1e37905-4760-48e1-8eb0-e487c9108062");
            String profileName = username;

            GameProfile fakeProfile = new GameProfile(formattedPlayerUUID, "EpicDude123");

            if (!profileName.equals("") && !JsonUtils.isEmpty(apiReq)) {
                String uuidString = profile.getStat("player.uuid");

                if (uuidString != null && !uuidString.isEmpty() && UUIDUtils.isUuid(uuidString)) {
                    formattedPlayerUUID = UUIDUtils.fromTrimmed(uuidString);
                    fakeProfile = Minecraft.getMinecraft().getSessionService()
                            .fillProfileProperties(new GameProfile(formattedPlayerUUID, profileName), false);
                } else {
                    System.out.println("Invalid or missing UUID: " + uuidString);
                }
            }

            entityPlayer = new EntityOtherPlayerMP(Minecraft.getMinecraft().theWorld, fakeProfile) {
                public ResourceLocation getLocationSkin() {
                    return playerLocationSkin == null
                            ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID())
                            : playerLocationSkin;
                }

                public ResourceLocation getLocationCape() {
                    return playerLocationCape;
                }

                public String getSkinType() {
                    return skinType == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : skinType;
                }
            };
            entityPlayer.setAlwaysRenderNameTag(false);
            entityPlayer.setCustomNameTag("");
        } else {
            entityPlayer.refreshDisplayName();
            byte b = 0;
            for (EnumPlayerModelParts part : EnumPlayerModelParts.values()) {
                b |= part.getPartMask();
            }
            entityPlayer.getDataWatcher().updateObject(10, b);
        }

        if (entityPlayer != null && playerLocationSkin == null) {
            try {
                Minecraft
                        .getMinecraft()
                        .getSkinManager()
                        .loadProfileTextures(
                                entityPlayer.getGameProfile(),
                                (type, location1, profileTexture) -> {
                                    switch (type) {
                                        case SKIN:
                                            playerLocationSkin = location1;
                                            skinType = profileTexture.getMetadata("model");

                                            if (skinType == null) {
                                                skinType = "default";
                                            }

                                            break;
                                        case CAPE:
                                            playerLocationCape = location1;
                                    }
                                },
                                false
                        );
            } catch (Exception ignored) {
            }
        }

        GlStateManager.color(1, 1, 1, 1);
        if (entityPlayer != null) {
            drawEntityOnScreen(
                    guiLeft + 61,
                    guiTop + 136 + 7,
                    65,
                    guiLeft + 61 - mouseX,
                    guiTop + 137 - mouseY,
                    entityPlayer
            );
        }
    }

    private void renderTopCard(float posX, float posY, int scale, float mouseX, float mouseY) {
        JsonObject apiReq = playerData;
        Stats s = new Stats(apiReq);

        String playerNameFormatted = RankUtils.formatRankAndUsername(username, apiReq);
        String level = "&7Level: " + s.formattedStar;
        String expProgress = "&7EXP Progress: &b" + s.currentLevelExperience + "&7/&a" + s.expReqToLevelUp;
        String progressToNextLevel = s.formattedStar + BedwarsExperience.getProgressBar(s.exp) + s.formattedStarPlusOne;

        drawStringCentered(fontRendererObj,formatText(playerNameFormatted), posX, posY - 5f, true, 0);
        drawStringCentered(fontRendererObj, formatText(level), posX, posY + 7f, true, 0);
        drawStringCentered(fontRendererObj, formatText(expProgress), posX, posY + 19f, true, 0);
        drawStringCentered(fontRendererObj, formatText(progressToNextLevel), posX, posY + 31f, true, 0);

    }


    private String formatText(String text) {
        // Replace color codes with Minecraft's color format
        return text.replace("&", "\u00A7");
    }


    private static void drawStringCentered(FontRenderer fr, String str, float x, float y, boolean shadow, int colour) {
        int strLen = fr.getStringWidth(str);

        float x2 = x - strLen / 2f;
        float y2 = y - fr.FONT_HEIGHT / 2f;

        GL11.glTranslatef(x2, y2, 0);
        fr.drawString(str, 0, 0, colour, shadow);
        GL11.glTranslatef(-x2, -y2, 0);
    }

    public static void drawStringCenteredScaledMaxWidth(
            String str,
            FontRenderer fr,
            float x,
            float y,
            boolean shadow,
            int len,
            int colour
    ) {
        int strLen = fr.getStringWidth(str);
        float factor = len / (float) strLen;
        factor = Math.min(1, factor);
        int newLen = Math.min(strLen, len);

        float fontHeight = 8 * factor;

        drawStringScaled(fr, str, x - newLen / 2, y - fontHeight / 2, shadow, colour, factor);
    }

    public static void drawStringScaled(
            FontRenderer fr,
            String str,
            float x,
            float y,
            boolean shadow,
            int colour,
            float scale
    ) {

        int strLen = fr.getStringWidth(str);

        float x2 = x - strLen / 2f;
        float y2 = y - fr.FONT_HEIGHT / 2f;

        GL11.glTranslatef(x2, y2, 0);
        GlStateManager.scale(scale, scale, 1);
        fr.drawString(str, x / scale, y / scale, colour, shadow);
        GlStateManager.scale(1 / scale, 1 / scale, 1);
        GL11.glTranslatef(-x2, -y2, 0);
    }
}
