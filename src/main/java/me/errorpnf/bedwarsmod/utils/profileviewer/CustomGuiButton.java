package me.errorpnf.bedwarsmod.utils.profileviewer;

import me.errorpnf.bedwarsmod.utils.RenderUtils;
import me.errorpnf.bedwarsmod.utils.formatting.FormatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class CustomGuiButton {
    public static final ResourceLocation leftButton = new ResourceLocation("bedwarsmod:textures/gui/left_button.png");
    public static final ResourceLocation rightButton = new ResourceLocation("bedwarsmod:textures/gui/right_button.png");
    public static final ResourceLocation leftButtonPressed = new ResourceLocation("bedwarsmod:textures/gui/left_button_pressed.png");
    public static final ResourceLocation rightButtonPressed = new ResourceLocation("bedwarsmod:textures/gui/right_button_pressed.png");
    private boolean wasClicked;
    private boolean mouseButtonDown;

    public boolean getWasClicked() {
        return wasClicked;
    }

    public void setWasClicked(boolean wasClicked) {
        this.wasClicked = wasClicked;
    }

    public CustomGuiButton() {
    }

    public void drawButton(float guiLeft, float guiTop, float x, float y, float width, float height, int mouseX, int mouseY, String text, int button, FontRenderer fontRenderer) {
        boolean isHovered = isMouseOver(mouseX, mouseY, x, y, width, height);

        if (isHovered) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(button == 0 ? leftButtonPressed : rightButtonPressed);

            if (Mouse.isButtonDown(0)) {
                if (!mouseButtonDown) {
                    mouseButtonDown = true;
                }
            } else {
                if (mouseButtonDown) {
                    mouseButtonDown = false;
                    setWasClicked(true);
                }
            }
        } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(button == 0 ? leftButton : rightButton);
            mouseButtonDown = false;
        }

        int sizeX = 430;
        int sizeY = 224;
        RenderUtils.drawTexturedRect(guiLeft, guiTop, sizeX, sizeY, GL11.GL_NEAREST);
        RenderUtils.drawStringCentered(fontRenderer, FormatUtils.format(text), x + (width/ 2), y + (height / 2) + 0.5f, true, 0);
    }

    private boolean isMouseOver(int mouseX, int mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}