package com.lab601.loopicandroid.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigManager {

    private static ConfigManager mInstance;
    private boolean landscape = true;   //横屏
    private boolean sound = false;  //静音
    public static HashMap<Integer, String> preloadMap = new HashMap<>();

    private int startIndex = 0;
    private List<String> text = null;
    private String url = "";

    private ConfigManager() {
        text = new ArrayList<>();
    }

    public static ConfigManager getInstance() {
        if (mInstance == null) {
            mInstance = new ConfigManager();
        }
        return mInstance;
    }

    public boolean isLandscape() {
        return landscape;
    }

    public void setLandscape(boolean landscape) {
        this.landscape = landscape;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public ConfigManager setStartIndex(int startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ConfigManager setUrl(String url) {
        this.url = url;
        return this;
    }

    public List<String> getText() {
        return text;
    }

    public ConfigManager setText(List<String> text) {
        this.text = text;
        return this;
    }
}
