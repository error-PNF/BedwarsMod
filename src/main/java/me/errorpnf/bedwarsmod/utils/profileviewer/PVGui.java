package me.errorpnf.bedwarsmod.utils.profileviewer;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import me.errorpnf.bedwarsmod.utils.RenderUtils;
import me.errorpnf.bedwarsmod.utils.StatUtils;
import me.errorpnf.bedwarsmod.utils.UUIDUtils;
import me.errorpnf.bedwarsmod.utils.formatting.RankUtils;
import me.errorpnf.bedwarsmod.data.BedwarsExperience;
import me.errorpnf.bedwarsmod.data.GameModeEnum;
import me.errorpnf.bedwarsmod.data.stats.Stats;
import me.errorpnf.bedwarsmod.utils.JsonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PVGui extends GuiScreen {
    private final int sizeX = 430;
    private final int sizeY = 224;
    private static int guiLeft;
    private static int guiTop;
    public static final ResourceLocation pv_bg = new ResourceLocation("bedwarsmod:textures/gui/background.png");
    public EntityOtherPlayerMP entityPlayer = null;
    private ResourceLocation playerLocationSkin = null;
    private ResourceLocation playerLocationCape = null;
    private String skinType = null;


    private String username;
    private JsonObject playerData;
    private GameModeEnum gamemode;

    public PVGui(String username, JsonObject playerData, GameModeEnum gamemode) {
        this.username = username;
        this.playerData = playerData;
        this.gamemode = gamemode;
    }

    private CustomGuiButton customButton;

    @Override
    public void initGui() {
        super.initGui();

        customButton = new CustomGuiButton(1, guiLeft + ((380 * 2f/3f) / 2f), guiTop + 100, (420 * 2f/3f) / 2, (59 * 2f/3f) / 2, gamemode.getFullName());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        guiLeft = (this.width - this.sizeX) / 2;
        guiTop = (this.height - this.sizeY) / 2;

        super.drawScreen(mouseX, mouseY, partialTicks);
        drawDefaultBackground();

        RenderUtils.blurBackground();
        RenderUtils.renderBlurredBackground(width, height, guiLeft + 2, guiTop + 2, sizeX - 4, sizeY - 4);

        GlStateManager.enableDepth();
        GlStateManager.translate(0, 0, 5);
        GlStateManager.translate(0, 0, -3);

        GlStateManager.disableDepth();
        GlStateManager.translate(0, 0, -2);
        GlStateManager.translate(0, 0, 2);

        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);

        Minecraft.getMinecraft().getTextureManager().bindTexture(pv_bg);
        RenderUtils.drawTexturedRect(guiLeft, guiTop, sizeX, sizeY, GL11.GL_NEAREST);

        renderDrawnEntity(mouseX, mouseY, partialTicks);
        renderTopCard(guiLeft + (365f * 2f/3f), guiTop + 20, 2, mouseX, mouseY);

        PlayerSocials playerSocials = new PlayerSocials(playerData, username, fontRendererObj);
        playerSocials.drawTextures(guiLeft + 61.75f, guiTop + 181.875f, mouseX, mouseY);

        PlayerStatsCard playerStatsCard = new PlayerStatsCard(playerData, fontRendererObj, gamemode);
        playerStatsCard.drawStatsCard(guiLeft + (410f * 2f/3f), guiTop + (195 * 1f/3f), mouseX, mouseY);

        customButton.drawButton(mc, mouseX, mouseY);
    }



    @Override
    public boolean doesGuiPauseGame() {
        return false;
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
                    return playerLocationCape
                            ;
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
        Stats s = new Stats(apiReq, gamemode);

        String playerNameFormatted = RankUtils.formatRankAndUsername(username, apiReq);
        String level = "&7Level: " + s.formattedStar;
        String expProgress = "&7EXP Progress: &b" + s.currentLevelExperience + "&7/&a" + s.expReqToLevelUp;
        String progressToNextLevel = s.formattedStar + BedwarsExperience.getProgressBar(s.exp) + s.formattedStarPlusOne;

        RenderUtils.drawStringCentered(fontRendererObj,formatText(playerNameFormatted), posX, posY - 5f, true, 0);
        RenderUtils.drawStringCentered(fontRendererObj, formatText(level), posX, posY + 7f, true, 0);
        RenderUtils.drawStringCentered(fontRendererObj, formatText(expProgress), posX, posY + 19f, true, 0);
        RenderUtils.drawStringCentered(fontRendererObj, formatText(progressToNextLevel), posX, posY + 31f, true, 0);
    }

    public static String formatText(String text) {
        // Replace color codes with Minecraft's color format
        return text.replace("&", "\u00A7");
    }

    private static String unformatText(String formattedString) {
        Pattern FORMAT_CODE_PATTERN = Pattern.compile("§[0-9a-fk-or]");
        Matcher matcher = FORMAT_CODE_PATTERN.matcher(formattedString);
        return matcher.replaceAll(""); // Remove all occurrences of formatting codes
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
