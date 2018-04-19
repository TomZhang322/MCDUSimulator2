package com.adcc.mcdu.simulator.util;

import java.io.File;

/**
 * 文件工具类
 */
public class FileUtil {

    /**
     * 创建文件夹
     * @param path
     */
    public static boolean mkdir(String path){
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdir();
        }else{
            return true;
        }
    }

    /**
     * 文件或文件夹是否存在
     * @param path
     * @return
     */
    public static boolean isExist(String path){
        File file = new File(path);
        return file.exists();
    }
}
