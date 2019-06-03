package com.lab601.loopicandroid.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.lab601.loopicandroid.activity.MainActivity.SOUND_ROOT;

public class SoundTagStorage {

    public static Map<String, List<File>> soundStorage = new HashMap<>();

    public static List<File> getSoundFile(String tag) {
        List<File> ret = null;
        if (soundStorage.containsKey(tag)) {
            ret = soundStorage.get(tag);
            return ret;
        }

        String soundDirStr = tag.replace("-", "/");    //音频路径

        File soundDir = new File(SOUND_ROOT + "/" + soundDirStr);
//            Log.d(TAG, "onPageChanged: 音频路径:"+SOUND_ROOT+"/"+soundTag);
        final File[] soundFiles = soundDir.listFiles();
        List<File> files = new ArrayList<File>(Arrays.asList(soundFiles));
        soundStorage.put(tag, files);
        return files;

    }

    public static File getNextSoundFile(String tag) {
        List<File> soundFiles = getSoundFile(tag);
        int fileNum = soundFiles.size();
        if (fileNum < 1) return null;
        int rand = ((int) (new Random().nextFloat() * (fileNum - 1)));

        return soundFiles.get(rand);

    }

}
