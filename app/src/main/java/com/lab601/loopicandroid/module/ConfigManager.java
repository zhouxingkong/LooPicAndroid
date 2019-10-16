package com.lab601.loopicandroid.module;

public class ConfigManager {

    private static ConfigManager mInstance;
    private boolean landscape = true;   //横屏
    private boolean sound = false;  //静音


    private int startIndex = 0;

    private ConfigManager() {

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
}
