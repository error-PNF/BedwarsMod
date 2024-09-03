package me.errorpnf.bedwarsmod.utils.profileviewer;

public enum Social {
    TIKTOK("TIKTOK", "TikTok", 0),
    TWITCH("TWITCH", "Twitch", 1),
    DISCORD("DISCORD", "Discord", 2),
    HYPIXEL("HYPIXEL", "Hypixel", 3),
    TWITTER("TWITTER", "Twitter", 4),
    YOUTUBE("YOUTUBE", "YouTube", 5),
    INSTAGRAM("INSTAGRAM", "Instagram", 6);

    private String name;
    private String prettyName;
    private int key;

    Social(String name, String prettyName, int key) {
        this.name = name;
        this.prettyName = prettyName;
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    public int getKey() {
        return this.key;
    }
}