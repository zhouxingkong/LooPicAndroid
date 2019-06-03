package com.lab601.loopicandroid.module;


import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 输出文本的一行配置
 */
public class DisplayMenu {
    String picFileName = "#";   //图片文件名
    String soundTag = "#";     //声音文件标签
    String soundMode = "#";     //音频播放模式
    String text = "#";      //文字描述

    List<String> soundTagList = new ArrayList<>();
    List<File> soundList = new ArrayList<>();

    @Override
    public String toString() {
        String ret = picFileName + " " + soundTag + " " + soundMode + " " + text;
        return ret;
    }

    public String getPicFileName() {
        return picFileName;
    }

    public DisplayMenu setPicFileName(String picFileName) {
        this.picFileName = picFileName;
        return this;
    }

    public String getSoundTag() {
        return soundTag;
    }

    public DisplayMenu setSoundTag(String soundTag) {
        this.soundTag = soundTag;
        return this;
    }

    public String getSoundMode() {
        return soundMode;
    }

    public DisplayMenu setSoundMode(String soundMode) {
        this.soundMode = soundMode;
        return this;
    }

    public String getText() {
        return text;
    }

    public DisplayMenu setText(String text) {
        this.text = text;
        return this;
    }

    public List<String> getSoundTagList() {
        return soundTagList;
    }

    public void setSoundTagList(List<String> soundTagList) {
        this.soundTagList = soundTagList;
    }

    public List<File> getSoundList() {
        return soundList;
    }

    public void setSoundList(List<File> soundList) {
        this.soundList = soundList;
    }

    /**
     * 从string 中还原结构内容
     *
     * @param str
     */
    public void fromString(String str) {
        String[] nameSplitSpace = str.split("\\ ");    //空格分开不同的部分
        picFileName = nameSplitSpace[0];

        if (nameSplitSpace.length > 2) {
            soundTag = nameSplitSpace[1];
            soundMode = nameSplitSpace[2];
        }
        /*生成音频列表*/
        if (!soundMode.equals("#") && soundMode.length() > 0 && !soundTag.equals("#") && soundTag.length() > 0) {
            int soundLoop = 0;
            try {
                soundLoop = Integer.parseInt(soundMode);
            } catch (Exception e) {
                Log.e("xingkong", "fromString:解析音频个数出错", e);
            }

            String[] split = soundTag.split("&");
            for (String s : split) {
                soundTagList.add(s);
            }

            for (int i = 0; i < soundLoop; i++) {
                for (String tag : soundTagList) {

                    File soundFile = SoundTagStorage.getNextSoundFile(tag);
                    if (soundFile != null) {    //是null就不放进去
                        soundList.add(soundFile);
                    }


                }
            }
        }



        if (nameSplitSpace.length > 3) {
            text = nameSplitSpace[3];
        }
    }


}
