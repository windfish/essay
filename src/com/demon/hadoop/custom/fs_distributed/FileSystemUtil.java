package com.demon.hadoop.custom.fs_distributed;

import com.demon.hadoop.custom.fs_local.FileSystem;

import java.io.File;

public class FileSystemUtil {

    public static String formatPath(String path){
        String wholePath = FileSystem.root + File.separator + path;
        return wholePath.replace("/", File.separator)
                .replace("//", File.separator);
    }

    public static String formatPath(File file){
        return FileSystem.root + File.separator + file.toString();
    }

    public static String formatPath(String dir, String fileName){
        String wholePath = FileSystem.root + File.separator + dir + File.separator + fileName;
        return wholePath.replace("/", File.separator)
                .replace("//", File.separator);
    }

    public static String getFileSystemPath(String path){
        return path.substring(FileSystem.root.length());
    }

    public static String getFileSystemPath(File file){
        return file.toString().substring(FileSystem.root.length());
    }
}
