package com.demon.hadoop.custom.fs_local;

import java.util.List;

/**
 * 本地简易文件系统
 */
public interface FileSystem {

    // 文件系统的磁盘映射根目录
    String root = "/Users/xuliang/data/fs";

    // 上传文件到指定目录
    void put(String file, String dest);

    // 下载文件到指定目录
    void get(String file, String dest);

    // 创建目录
    void mkdir(String dir);

    // 展示某个目录下的所有文件
    List<String> list(String path);
}
