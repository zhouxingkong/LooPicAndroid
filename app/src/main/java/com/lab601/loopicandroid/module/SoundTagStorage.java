package com.lab601.loopicandroid.module;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.lab601.loopicandroid.module.SourceManager.SOUND_ROOT;


public class SoundTagStorage {

    public static Map<String, List<File>> soundStorage = new HashMap<>();


    public static List<File> getSoundFile(String tag) {
        List<File> ret = null;
        if (soundStorage.containsKey(tag)) {
            ret = soundStorage.get(tag);
            return ret;
        }

        String soundDirStr = tag.replace("-", "/");    //音频路径

//        File soundDir = new File(SOUND_ROOT + "/" + soundDirStr);
////            Log.d(TAG, "onPageChanged: 音频路径:"+SOUND_ROOT+"/"+soundTag);
//        final File[] soundFiles = soundDir.listFiles();
//        List<File> files = new ArrayList<File>(Arrays.asList(soundFiles));
        List<File> files = getFileList(new ArrayList<>(), SOUND_ROOT + "/" + soundDirStr);  //改成递归的搜索文件

        files = files.stream().filter(file -> !file.isDirectory()).collect(Collectors.toList());    //过滤掉文件夹
        soundStorage.put(tag, files);
        return files;

    }

    /**
     * 获取下一个文件
     *
     * @param tag
     * @return
     */
    public static File getNextSoundFile(String tag) {
        List<File> soundFiles = getSoundFile(tag);
        int fileNum = soundFiles.size();
        if (fileNum < 1) return null;
        int rand = ((int) (new Random().nextFloat() * fileNum));

        return soundFiles.get(rand);

    }

    public static List<File> getFileList(List<File> filelist, String strPath) {

        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
//                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(filelist, files[i].getAbsolutePath()); // 获取文件绝对路径
                } else {
//                    String strFileName = files[i].getAbsolutePath();
//                    System.out.println("---" + strFileName);
                    filelist.add(files[i]);
                }
            }

        }
        return filelist;
    }

}
