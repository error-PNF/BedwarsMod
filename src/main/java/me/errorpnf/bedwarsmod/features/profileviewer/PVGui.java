package me.errorpnf.bedwarsmod.features.profileviewer;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import me.errorpnf.bedwarsmod.config.BedwarsModConfig;
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
import net.minecraft.client.gui.ScaledResolution;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PVGui extends GuiScreen {
    private static int guiLeft;
    private static int guiTop;

    private static int guiLeftAbsolute;
    private static int guiTopAbsolute;

    public static final ResourceLocation pv_bg = new ResourceLocation("bedwarsmod:textures/gui/background.png");
    public EntityOtherPlayerMP entityPlayer = null;
    private ResourceLocation playerLocationSkin = null;
    private final ResourceLocation playerLocationCape = null;
    private String skinType = null;

    private final String username;
    private final JsonObject playerData;
    private GameModeEnum gamemode;

    private float scaledMouseX;
    private float scaledMouseY;
    private float configScale;

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
        configScale = BedwarsModConfig.pvGuiScaleFactor; // Fetch the scale factor from your config

        int baseWidth = 430;
        int baseHeight = 224;

        int scaledWidth = (int) (baseWidth * configScale);
        int scaledHeight = (int) (baseHeight * configScale);

        guiLeftAbsolute = (mc.displayWidth - scaledWidth) / 2;
        guiTopAbsolute = (mc.displayHeight - scaledHeight) / 2;

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int scaleFactor = scaledResolution.getScaleFactor();
        guiLeft = guiLeftAbsolute / scaleFactor;
        guiTop = guiTopAbsolute / scaleFactor;

        scaledMouseX = mouseX * scaleFactor;
        scaledMouseY = mouseY * scaleFactor;

        // Render the background
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawDefaultBackground();

        GlStateManager.pushMatrix();

        GlStateManager.scale(1.0 / scaleFactor, 1.0 / scaleFactor, 1.0);
        GlStateManager.translate(guiLeftAbsolute, guiTopAbsolute, 0); // Adjust position
        GlStateManager.scale(configScale, configScale, 1.0); // Apply config scale
        GlStateManager.translate(-guiLeftAbsolute, -guiTopAbsolute, 0); // Reset position

        // draw background texture layer
        Minecraft.getMinecraft().getTextureManager().bindTexture(pv_bg);
        RenderUtils.drawTexturedRect(guiLeftAbsolute, guiTopAbsolute, baseWidth, baseHeight, GL11.GL_NEAREST);


        // render player model
        renderingNametag = true;
        renderDrawnEntity(mouseX, mouseY, partialTicks, configScale);
        renderingNametag = false;

        renderTopCard(guiLeftAbsolute + (325f * 2f / 3f), guiTopAbsolute + 20, 2, mouseX, mouseY);

        // TODO Fix mouse scaling with this
        PlayerSocials playerSocials = new PlayerSocials(playerData, username, fontRendererObj);
        playerSocials.drawTextures(
                guiLeftAbsolute + 61.75f,
                guiTopAbsolute + 181.875f,
                mouseX,
                mouseY,
                configScale
        );

        PlayerStatsCard playerStatsCard = new PlayerStatsCard(playerData, fontRendererObj, gamemode);
        playerStatsCard.drawStatsCard(guiLeftAbsolute + (410f * 2f / 3f), guiTopAbsolute + (195 * 1f / 3f), mouseX, mouseY);

        if (leftButton.getWasClicked()) {
            // cycle through category
            Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.5f, 2f);
            cycleCategories();
            leftButton.setWasClicked(false);
        } else if (rightButton.getWasClicked()) {
            // cycle through mode
            Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.5f, 2f);
            cycleModes();
            rightButton.setWasClicked(false);
        }

        String categoryText = FormatUtils.format("&f") + gamemode.getCategory();
        String modeText = FormatUtils.format("&f") + gamemode.getFullName();

        leftButton.drawButton(
                guiLeftAbsolute,
                guiTopAbsolute,
                guiLeftAbsolute + ((370 * 2f / 3f) / 2),
                guiTopAbsolute + ((594 * 2f / 3f) / 2),
                (440f * 2f / 3f) / 2, // 146.667
                (59 * 2f / 3f) / 2, // 19.667...
                scaledMouseX,
                scaledMouseY,
                categoryText,
                0,
                fontRendererObj,
                guiLeftAbsolute + ((370 * 2f / 3f) * configScale / 2),
                guiTopAbsolute + ((594 * 2f / 3f) * configScale / 2),
                (440f * 2f / 3f) * configScale / 2, // 146.667
                (59 * 2f / 3f) * configScale / 2 // 19.667...
        );

        rightButton.drawButton(
                guiLeftAbsolute,
                guiTopAbsolute,
                guiLeftAbsolute + ((830 * 2f / 3f) / 2),
                guiTopAbsolute + ((594 * 2f / 3f) / 2),
                (440f * 2f / 3f) / 2,
                (59 * 2f / 3f) / 2,
                scaledMouseX,
                scaledMouseY,
                modeText,
                1,
                fontRendererObj,
                guiLeftAbsolute + ((830 * 2f / 3f) * configScale / 2),
                guiTopAbsolute + ((594 * 2f / 3f) * configScale / 2),
                (440f * 2f / 3f) * configScale / 2,
                (59 * 2f / 3f) * configScale / 2
        );

        searchBox.draw(
                guiLeftAbsolute,
                guiTopAbsolute,
                guiLeftAbsolute + ((30 * 2f / 3f) / 2) - 0.5f,
                guiTopAbsolute + ((594 * 2f / 3f) / 2) + 0.5f,
                (310f * 2f / 3f) / 2,
                (59 * 2f / 3f) / 2,
                scaledMouseX,
                scaledMouseY,
                fontRendererObj,
                guiLeftAbsolute + ((30 * 2f / 3f) * configScale / 2) - 0.5f,
                guiTopAbsolute + ((594 * 2f / 3f) * configScale / 2) + 0.5f,
                (310f * 2f / 3f) * configScale / 2,
                (59 * 2f / 3f) * configScale / 2
        );
        CornerCard cornerCard = new CornerCard();
        cornerCard.drawCard(guiLeftAbsolute + ((970 * 2f / 3f) / 2), guiTopAbsolute + 20, fontRendererObj, playerData);

        GlStateManager.popMatrix();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
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

    private void renderDrawnEntity(int mouseX, int mouseY, float partialTicks, float scaleFactor) {

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
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int guiScaleFactor = scaledResolution.getScaleFactor();

            float scaledMouseX = mouseX * scaleFactor;
            float scaledMouseY = mouseY * scaleFactor;

            drawEntityOnScreen(
                    guiLeftAbsolute + 61,
                    guiTopAbsolute + 136 + 7,
                    65,
                    guiLeftAbsolute + (61 * scaleFactor) - (scaledMouseX * (guiScaleFactor / scaleFactor)),
                    (guiTopAbsolute + (137 * scaleFactor) - (scaledMouseY * (guiScaleFactor / scaleFactor))),
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
        searchBox.handleMouseInput(scaledMouseX, scaledMouseY, guiLeftAbsolute + ((30 * 2f / 3f) * configScale / 2) - 0.5f, guiTopAbsolute + ((594 * 2f / 3f) * configScale / 2) + 0.5f, (310f * 2f / 3f) * configScale / 2, (59 * 2f / 3f) * configScale / 2, mouseButton);
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
