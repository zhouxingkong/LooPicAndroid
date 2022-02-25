package com.lab601.loopicandroid.module

import java.io.File
import java.util.*
import java.util.stream.Collectors

object SoundTagStorage {
    const val SOUND_ROOT = "/sdcard/bb/sounds"

    var soundStorage: MutableMap<String, List<File>> = HashMap()
    fun getSoundFile(tag: String): List<File>? {
        var ret: List<File>? = null
        if (soundStorage.containsKey(tag)) {
            ret = soundStorage[tag]
            return ret
        }
        val soundDirStr = tag.replace("-", "/") //音频路径

//        File soundDir = new File(SOUND_ROOT + "/" + soundDirStr);
////            Log.d(TAG, "onPageChanged: 音频路径:"+SOUND_ROOT+"/"+soundTag);
//        final File[] soundFiles = soundDir.listFiles();
//        List<File> files = new ArrayList<File>(Arrays.asList(soundFiles));
        var files = getFileList(
            ArrayList(),
            SOUND_ROOT + "/" + soundDirStr
        ) //改成递归的搜索文件
        files = files.stream().filter { file: File -> !file.isDirectory }
            .collect(Collectors.toList()) //过滤掉文件夹
        soundStorage[tag] = files
        return files
    }

    /**
     * 获取下一个文件
     *
     * @param tag
     * @return
     */
    fun getNextSoundFile(tag: String): File? {
        val soundFiles = getSoundFile(tag)
        val fileNum = soundFiles!!.size
        if (fileNum < 1) return null
        val rand = (Random().nextFloat() * fileNum).toInt()
        return soundFiles[rand]
    }

    fun getFileList(filelist: MutableList<File>, strPath: String?): List<File> {
        val dir = File(strPath)
        val files = dir.listFiles() // 该文件目录下文件全部放入数组
        if (files != null) {
            for (i in files.indices) {
//                String fileName = files[i].getName();
                if (files[i].isDirectory) { // 判断是文件还是文件夹
                    getFileList(filelist, files[i].absolutePath) // 获取文件绝对路径
                } else {
//                    String strFileName = files[i].getAbsolutePath();
//                    System.out.println("---" + strFileName);
                    filelist.add(files[i])
                }
            }
        }
        return filelist
    }
}