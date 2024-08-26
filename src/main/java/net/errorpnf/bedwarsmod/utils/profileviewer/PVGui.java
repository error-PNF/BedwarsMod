package net.errorpnf.bedwarsmod.utils.profileviewer;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import com.mojang.authlib.GameProfile;
import net.errorpnf.bedwarsmod.utils.ApiUtils;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

public class PVGui extends GuiScreen {
    private int sizeX = 430;
    private int sizeY = 224;
    private GuiButton button;
    private static int guiLeft;
    private static int guiTop;
    public static final ResourceLocation pv_bg = new ResourceLocation("bedwarsmod:textures/gui/background.png");
    public EntityOtherPlayerMP entityPlayer = null;
    private ResourceLocation playerLocationSkin = null;
    private ResourceLocation playerLocationCape = null;
    private String skinType = null;
    private boolean loadingProfile = false;

    public String username = "LuckyBlockBW";



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
        fontRendererObj.drawString("Profile Viewer", 130, 45, 1);

        renderDrawnEntity(mouseX, mouseY, partialTicks);
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
            CompletableFuture<UUID> playerUUID = ApiUtils.getPlayerUUIDAsync(username);
            GameProfile fakeProfile = Minecraft
                    .getMinecraft()
                    .getSessionService()
                    .fillProfileProperties(new GameProfile(playerUUID.getNow(UUID.fromString("0c063bfd-3521-413d-a766-50be1d71f00e")), "CoolGuy123"), false);
            entityPlayer =
                    new EntityOtherPlayerMP(Minecraft.getMinecraft().theWorld, fakeProfile) {
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
}
