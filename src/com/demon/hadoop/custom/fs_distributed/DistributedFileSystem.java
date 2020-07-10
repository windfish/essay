package com.demon.hadoop.custom.fs_distributed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributedFileSystem implements FileSystem {

    String rootPath = "";
    DFSNode root = new DFSNode(rootPath, false);

    // 维护一个HashMap，key 是路径，value 是DFSNode 对象
    Map<String, DFSNode> nodeMap = new HashMap<>();

    /**
     * 构造文件系统时，加载元数据，解析服务器
     */
    public DistributedFileSystem() {
        DFSLog log = new DFSLog();
        nodeMap = log.loadLogByStart();
    }

    @Override
    public void put(String file, String dest) {
        // 上传文件
        IOUtil.copyFileToDFS(file, dest);
    }

    @Override
    public void get(String file, String dest) {

    }

    @Override
    public void mkdir(String dir) {

    }

    @Override
    public List<String> list(String path) {
        return null;
    }
}
