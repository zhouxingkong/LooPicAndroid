package com.lab601.loopicandroid.module;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 资源管理类
 */
public class SourceManager {

    public static final String ROOT_PATH = "/sdcard/bb/output"; //资源的总路径
    public static final String PICTURE_PATH = ROOT_PATH + "/imgs";
    public static final String SOUND_ROOT = "/sdcard/bb/sounds";
    private static SourceManager mInstance;
    boolean inited = false;
    private List<DisplayMenu> displayMenus;
    private InitialCallback initialCallback;

    private SourceManager() {

    }

    public static SourceManager getInstance() {
        if (mInstance == null) {
            mInstance = new SourceManager();
        }
        return mInstance;
    }

    /**
     * 读取输入文件
     *
     * @param path
     */
    public void readConfigFile(String path) {
        if (inited) {   //只允许初始化一次,避免出现并发问题
            return;
        }
        inited = true;
        try {
            FileReader fr = new FileReader(path);
            BufferedReader bf = new BufferedReader(fr);
            String str;

            displayMenus = new ArrayList<>();

            /*第一行:读取源文件路径*/
            while ((str = bf.readLine()) != null) {
                DisplayMenu oneDesc = new DisplayMenu();
                oneDesc.fromString(str);
                displayMenus.add(oneDesc);
            }

            bf.close();
            fr.close();

            /*成功回调*/
            if (initialCallback != null) {
                initialCallback.onSuccess();
            }
        } catch (IOException e) {
            e.printStackTrace();
            /*失败回调*/
            if (initialCallback != null) {
                initialCallback.onFail();
            }
        }
    }

    public List<DisplayMenu> getDisplayMenus() {
        return displayMenus;
    }

    public void setDisplayMenus(List<DisplayMenu> displayMenus) {
        this.displayMenus = displayMenus;
    }

    public void setInitialCallback(InitialCallback initialCallback) {
        this.initialCallback = initialCallback;
    }
}
