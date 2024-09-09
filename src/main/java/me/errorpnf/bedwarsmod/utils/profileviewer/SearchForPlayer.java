package me.errorpnf.bedwarsmod.utils.profileviewer;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.google.gson.JsonObject;
import me.errorpnf.bedwarsmod.BedwarsMod;
import me.errorpnf.bedwarsmod.data.GameModeEnum;
import me.errorpnf.bedwarsmod.data.apicache.ApiCacheManager;
import me.errorpnf.bedwarsmod.utils.ApiUtils;
import me.errorpnf.bedwarsmod.utils.RenderUtils;
import me.errorpnf.bedwarsmod.utils.StatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class SearchForPlayer {
    private String text = "";
    private boolean isActive = false;
    private boolean cursorVisible = true;
    private long lastBlinkTime = 0;
    private int cursorPos = 0;
    private static final int CURSOR_BLINK_RATE = 500;
    private static final int CURSOR_WIDTH = 1;
    private long lastKeyPressTime = 0;
    private static final int MAX_CHARACTERS = 16;
    public static final ResourceLocation searchBar = new ResourceLocation("bedwarsmod:textures/gui/search_bar.png");
    public static final ResourceLocation searchBarActive = new ResourceLocation("bedwarsmod:textures/gui/search_bar_active.png");

    public SearchForPlayer(Minecraft mc) {
        this.lastBlinkTime = System.currentTimeMillis();
    }

    public void draw(float x, float y, float width, float height, int mouseX, int mouseY, FontRenderer fontRenderer, float guiLeft, float guiTop) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(isActive || isMouseOver(mouseX, mouseY, x, y, width, height) ? searchBarActive : searchBar);
        int sizeY = 224;
        int sizeX = 430;
        RenderUtils.drawTexturedRect(guiLeft, guiTop, sizeX, sizeY, GL11.GL_NEAREST);

        String displayText = text.isEmpty() && !isActive ? "§7Search..." : text;
        RenderUtils.drawString(fontRenderer, displayText, x + 4, y + (height / 2 - (float) fontRenderer.FONT_HEIGHT / 2), 0xFFFFFFFF, true); // White text

        if (isActive) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBlinkTime >= CURSOR_BLINK_RATE) {
                cursorVisible = !cursorVisible;
                lastBlinkTime = currentTime;
            }

            if (cursorVisible) {
                int cursorX = (int) (x + 4 + fontRenderer.getStringWidth(text.substring(0, cursorPos)));
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                RenderUtils.drawRect(cursorX, y + 4, cursorX + CURSOR_WIDTH, y + height - 4, 0xFFFFFFFF);
            }
        }
    }

    public void handleMouseInput(int mouseX, int mouseY, float x, float y, float width, float height, int mouseButton) {
        if (mouseButton == 0 && isMouseOver(mouseX, mouseY, x, y, width, height)) {
            isActive = true;
            if (text.equals("§cInvalid Name") || text.isEmpty() || text.equals("§cRate Limit")) {
                text = "";
                cursorPos = 0;
            }
        } else if (mouseButton == 0) {
            isActive = false;
        }
    }

    private boolean backspaceHeld = false;
    private long backspaceHoldStartTime = 0;
    private static final int BACKSPACE_INITIAL_DELAY = 500;
    private static final int BACKSPACE_REPEAT_RATE = 50;

    public void handleKeyboardInput(char typedChar, int keyCode) {
        if (isActive) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (!backspaceHeld) {
                    deleteCharacter();
                    backspaceHeld = true;
                    backspaceHoldStartTime = System.currentTimeMillis();
                }
            } else if (keyCode == Keyboard.KEY_RETURN) {
                performSearch();
            } else if (Character.isLetterOrDigit(typedChar) || Character.isWhitespace(typedChar) || typedChar == '_') {
                if (text.length() < MAX_CHARACTERS) { // enforce character limit
                    insertCharacter(typedChar);
                }
            }
        }
    }

    public void handleKeyRelease(int keyCode) {
        if (keyCode == Keyboard.KEY_BACK) {
            backspaceHeld = false;
        }
    }

    public void update() {
        if (backspaceHeld) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - backspaceHoldStartTime >= BACKSPACE_INITIAL_DELAY) {
                if (currentTime - lastKeyPressTime >= BACKSPACE_REPEAT_RATE) {
                    deleteCharacter();
                    lastKeyPressTime = currentTime;
                }
            }
        }
    }

    private void deleteCharacter() {
        if (cursorPos > 0) {
            text = text.substring(0, cursorPos - 1) + text.substring(cursorPos);
            cursorPos--;
        }
    }

    private void insertCharacter(char typedChar) {
        if (text.length() < MAX_CHARACTERS) { // Check if the text length is within the limit
            text = text.substring(0, cursorPos) + typedChar + text.substring(cursorPos);
            cursorPos++;
        }
    }

    private boolean isMouseOver(int mouseX, int mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private long lastSearchTime = 0; // Tracks the time of the last search
    private static final long SEARCH_COOLDOWN = 5000; // 5 seconds cooldown in milliseconds

    private void performSearch() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastSearchTime < SEARCH_COOLDOWN) {
            UChat.chat("&cPlease wait before making another search.");
            return;
        }

        lastSearchTime = currentTime; // Update the last search time

        JsonObject cachedData = ApiCacheManager.getCachedRequest(text);
        GameModeEnum gamemode = GameModeEnum.OVERALL;
        if (cachedData != null) {
            StatUtils s = new StatUtils(cachedData);
            String displayUsername = s.getStat("player.displayname");

            BedwarsMod.INSTANCE.openGui = new PVGui(displayUsername, cachedData, gamemode);
        } else {
            ApiUtils.hypixelApiRequest(text).thenAccept(jsonObject -> {
                if (jsonObject != null) {
                    StatUtils s = new StatUtils(jsonObject);
                    if (!s.getStat("player.displayname").equals("Stat not found")) {
                        ApiCacheManager.cacheRequest(text, jsonObject);

                        String displayUsername = s.getStat("player.displayname");

                        BedwarsMod.INSTANCE.openGui = new PVGui(displayUsername, jsonObject, gamemode);
                    }
                }
            }).exceptionally(throwable -> {
                if (throwable.toString().contains("protocol=h2, code=429")) {
                    text = "§cRate Limit";
                    UChat.chat("&cYou are being rate limited. Please slow down and try again in 5 seconds.");
                    return null;
                }
                throwable.printStackTrace();
                //UChat.chat("&cError fetching data for &a" + text + "&c. Did you spell their username correctly?");
                text = "§cInvalid Name";
                return null;
            });
        }

        cursorPos = 0;
        isActive = false;
    }
}