package com.demon.hadoop.custom.fs_distributed;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件系统中的一个节点
 */
public class DFSNode {

    // 节点名称
    private String nodeName;
    private boolean isFile = false; // 简单实现，目录或文件

    // 父节点，一定是目录
    private DFSNode parentNode = null;
    // 子节点列表，可能是目录或文件
    private LinkedHashMap<String, DFSNode> children = new LinkedHashMap<>();

    public DFSNode(String nodeName, boolean isFile) {
        this.nodeName = nodeName;
        this.isFile = isFile;
    }

    /**
     * 获取子节点列表
     */
    public LinkedHashMap<String, DFSNode> getChildren(){
        return children;
    }

    /**
     * 给某个节点增加子节点
     */
    public DFSNode addChild(String path){
        if(this.isFile()){
            System.out.println("file can't has child node.");
            return null;
        }
        File file = new File(path);
        if(file.isFile()){
            DFSNode dfsNode = new DFSNode(path, true);
            children.put(path, dfsNode);
            return dfsNode;
        }else{
            DFSNode dfsNode = new DFSNode(path, false);
            children.put(path, dfsNode);
            return dfsNode;
        }
    }

    public boolean hasChild(){
        return this.children.isEmpty();
    }

    /**
     * 移除一个子节点，若该子节点下还有子节点，则不能移除
     */
    public DFSNode removeChildNode(String nodeName){
        if(!this.hasChild()){
            System.out.println("当前节点没有子节点");
            return null;
        }
        if(children.get(nodeName).hasChild()){
            System.out.println("子节点【" + nodeName + "】下还有子节点，不能移除");
            return null;
        }
        DFSNode remove = children.remove(nodeName);
        return remove;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }
}
