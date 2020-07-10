package com.demon.hadoop.custom.fs_distributed;

import java.util.List;

/**
 * 本地简易文件系统
 */
public interface FileSystem {

    // 上传文件到指定目录
    void put(String file, String dest);

    // 下载文件到指定目录
    void get(String file, String dest);

    // 创建目录
    void mkdir(String dir);

    // 展示某个目录下的所有文件
    List<String> list(String path);
}
