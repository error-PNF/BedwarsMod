package me.errorpnf.bedwarsmod.features.profileviewer;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import me.errorpnf.bedwarsmod.data.BedwarsExperience;
import me.errorpnf.bedwarsmod.data.GameModeEnum;
import me.errorpnf.bedwarsmod.data.stats.Stats;
import me.errorpnf.bedwarsmod.utils.JsonUtils;
import me.errorpnf.bedwarsmod.utils.RenderUtils;
import me.errorpnf.bedwarsmod.utils.StatUtils;
import me.errorpnf.bedwarsmod.utils.UUIDUtils;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import me.errorpnf.bedwarsmod.utils.formatting.RankUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PVGui extends GuiScreen {
    private static int guiLeft;
    private static int guiTop;
    public static final ResourceLocation pv_bg = new ResourceLocation("bedwarsmod:textures/gui/background.png");
    public EntityOtherPlayerMP entityPlayer = null;
    private ResourceLocation playerLocationSkin = null;
    private final ResourceLocation playerLocationCape = null;
    private String skinType = null;

    private final String username;
    private final JsonObject playerData;
    private GameModeEnum gamemode;

    public static boolean renderingNametag = false;

    public PVGui(String username, JsonObject playerData, GameModeEnum gamemode) {
        this.username = username;
        this.playerData = playerData;
        this.gamemode = gamemode;
    }

    private CustomGuiButton leftButton;
    private CustomGuiButton rightButton;
    private SearchForPlayer searchBox;

    @Override
    public void initGui() {
        super.initGui();
        leftButton = new CustomGuiButton();
        rightButton = new CustomGuiButton();
        searchBox = new SearchForPlayer(Minecraft.getMinecraft());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int sizeX = 430;
        guiLeft = (this.width - sizeX) / 2;
        int sizeY = 224;
        guiTop = (this.height - sizeY) / 2;

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

        // render player model
        renderingNametag = true;
        renderDrawnEntity(mouseX, mouseY, partialTicks);
        renderingNametag = false;

        renderTopCard(guiLeft + (325f * 2f/3f), guiTop + 20, 2, mouseX, mouseY);

        PlayerSocials playerSocials = new PlayerSocials(playerData, username, fontRendererObj);
        playerSocials.drawTextures(guiLeft + 61.75f, guiTop + 181.875f, mouseX, mouseY);

        PlayerStatsCard playerStatsCard = new PlayerStatsCard(playerData, fontRendererObj, gamemode);
        playerStatsCard.drawStatsCard(guiLeft + (410f * 2f/3f), guiTop + (195 * 1f/3f), mouseX, mouseY);

        if (leftButton.getWasClicked()) {
            // cycle through category
            cycleCategories();
            leftButton.setWasClicked(false);
        } else if (rightButton.getWasClicked()) {
            // cycle through mode
            cycleModes();
            rightButton.setWasClicked(false);
        }

        String categoryText = FormatUtils.format("&f") + gamemode.getCategory();
        String modeText = FormatUtils.format("&f") + gamemode.getFullName();

        leftButton.drawButton(
                guiLeft,
                guiTop,
                guiLeft + ((370 * 2f/3f) / 2),
                guiTop + ((594 * 2f/3f) / 2),
                (440f * 2f/3f) / 2,
                (59 * 2f/3f) / 2,
                mouseX,
                mouseY,
                categoryText,
                0,
                fontRendererObj
        );

        rightButton.drawButton(
                guiLeft,
                guiTop,
                guiLeft + ((830 * 2f/3f) / 2),
                guiTop + ((594 * 2f/3f) / 2),
                (440f * 2f/3f) / 2,
                (59 * 2f/3f) / 2,
                mouseX,
                mouseY,
                modeText,
                1,
                fontRendererObj
        );

        searchBox.draw(guiLeft + ((30 * 2f/3f) / 2) - 0.5f, guiTop + ((594 * 2f/3f) / 2) + 0.5f, (310f * 2f/3f) / 2, (59 * 2f/3f) / 2, mouseX, mouseY, fontRendererObj, guiLeft, guiTop);
        CornerCard cornerCard = new CornerCard();
        cornerCard.drawCard(guiLeft + ((970 * 2f/3f) / 2), guiTop + 20, fontRendererObj, playerData);
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

        if (entityPlayer == null) {
            JsonObject apiReq = playerData;
            StatUtils profile = new StatUtils(apiReq);
            UUID formattedPlayerUUID = UUID.fromString("c1e37905-4760-48e1-8eb0-e487c9108062");

            GameProfile fakeProfile = new GameProfile(formattedPlayerUUID, "EpicDude123");

            if (!username.equals("") && !JsonUtils.isEmpty(apiReq)) {
                String uuidString = profile.getStat("player.uuid");

                if (uuidString != null && !uuidString.isEmpty() && UUIDUtils.isUuid(uuidString)) {
                    formattedPlayerUUID = UUIDUtils.fromTrimmed(uuidString);
                    fakeProfile = Minecraft.getMinecraft().getSessionService()
                            .fillProfileProperties(new GameProfile(formattedPlayerUUID, ""), false);
                } else {
                    System.out.println("Invalid or missing UUID: " + uuidString);
                }
            }

            entityPlayer = new EntityOtherPlayerMP(Minecraft.getMinecraft().theWorld, fakeProfile) {
                @Override
                public ResourceLocation getLocationCape() {
                    return playerLocationCape == null ? super.getLocationCape() : playerLocationCape;
                }

                @Override
                public ResourceLocation getLocationSkin() {
                    return playerLocationSkin == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : playerLocationSkin;
                }

                @Override
                public String getSkinType() {
                    return skinType == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : skinType;
                }
            };
        } else {
            byte b = 0;
            for (EnumPlayerModelParts part : EnumPlayerModelParts.values()) {
                if (part == EnumPlayerModelParts.CAPE) {
                    continue; // skip cape
                }
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

                                            // No break here, so it will fall through to the next case
                                        case CAPE:
//                                            playerLocationCape = location1;
                                            // Code for CAPE will be executed after SKIN if type is SKIN
                                            break;
                                    }
                                },
                                false
                        );
            } catch (Exception ignored) {
            }
        }


        int sneakKeycode = mc.gameSettings.keyBindSneak.getKeyCode();
        boolean sneakPressed = Keyboard.isKeyDown(sneakKeycode);

        entityPlayer.setSneaking(sneakPressed);
        entityPlayer.setAlwaysRenderNameTag(false);

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

        RenderUtils.drawStringCentered(fontRendererObj, FormatUtils.format(playerNameFormatted), posX, posY - 5f, true, 0);
        RenderUtils.drawStringCentered(fontRendererObj, FormatUtils.format(level), posX, posY + 7f, true, 0);
        RenderUtils.drawStringCentered(fontRendererObj, FormatUtils.format(expProgress), posX, posY + 19f, true, 0);
        RenderUtils.drawStringCentered(fontRendererObj, FormatUtils.format(progressToNextLevel), posX, posY + 31f, true, 0);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        searchBox.handleKeyboardInput(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        searchBox.handleMouseInput(mouseX, mouseY, guiLeft + ((30 * 2f/3f) / 2) - 0.5f, guiTop + ((594 * 2f/3f) / 2) + 0.5f, (310f * 2f/3f) / 2, (59 * 2f/3f) / 2, mouseButton);
    }

    public void cycleCategories() {
        List<GameModeEnum> categories = getUniqueCategories();
        String currentCategory = gamemode.getCategory();
        int currentIndex = categories.indexOf(GameModeEnum.getModesByCategory(currentCategory).get(0));

        if (currentIndex < 0) return; // no valid index catch

        int nextIndex = (currentIndex + 1) % categories.size();
        String nextCategory = categories.get(nextIndex).getCategory();

        gamemode = GameModeEnum.getModesByCategory(nextCategory).get(0);
    }

    public void cycleModes() {
        List<GameModeEnum> modesInCategory = GameModeEnum.getModesByCategory(gamemode.getCategory());
        int currentIndex = modesInCategory.indexOf(gamemode);

        if (currentIndex < 0) return; // no valid index catch

        int nextIndex = (currentIndex + 1) % modesInCategory.size();
        gamemode = modesInCategory.get(nextIndex);
    }

    private List<GameModeEnum> getUniqueCategories() {
        return Arrays.stream(GameModeEnum.values())
                .map(GameModeEnum::getCategory)
                .distinct()
                .map(cat -> GameModeEnum.getModesByCategory(cat).get(0))
                .collect(Collectors.toList());
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        searchBox.handleKeyRelease(Keyboard.KEY_BACK);
        searchBox.update();
    }
}
