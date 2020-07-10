package com.demon.hadoop.custom.fs_distributed;

import sun.security.action.PutAllAction;

/**
 * 操作日志
 */
public class Log {

    private String nodeName;
    private String fsPath;
    private Operate putOrDelete;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getFsPath() {
        return fsPath;
    }

    public void setFsPath(String fsPath) {
        this.fsPath = fsPath;
    }

    public Operate getPutOrDelete() {
        return putOrDelete;
    }

    public void setPutOrDelete(Operate putOrDelete) {
        this.putOrDelete = putOrDelete;
    }

    @Override
    public String toString() {
        return nodeName + "," + fsPath + "," + putOrDelete;
    }

    public static enum Operate {
        PUT,
        DELETE,

        NONE,
        ;

        public static Operate parse(String name){
            for(Operate o: Operate.values()){
                if(o.name().equals(name)){
                    return o;
                }
            }
            return NONE;
        }
    }
}
