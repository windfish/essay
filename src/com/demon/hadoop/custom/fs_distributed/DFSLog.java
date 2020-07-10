package com.demon.hadoop.custom.fs_distributed;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DFSLog {

    private PrintWriter out = null;

    public DFSLog() {
        try {
            out = new PrintWriter(new File(PropertiesUtil.getProperty("namespaceLogFile")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录元数据
     */
    public void appendLod(Log log){
        out.println(log.toString());
    }

    /**
     * 读取日志，加载元数据
     */
    public Map<String, DFSNode> loadLogByStart(){
        Map<String, DFSNode> map = new HashMap<>();

        File logFile = new File(PropertiesUtil.getProperty("namespaceLogFile"));
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(logFile));

            String line = null;
            while((line = reader.readLine()) != null){
                String[] fields = line.split(",");
                String nodeName = fields[0];
                String fsPath = fields[1];
                Log.Operate operate = Log.Operate.parse(fields[2]);

                // 解析元数据存入内存
                File file = new File(fsPath);
                if(Log.Operate.PUT.equals(operate)){
                    map.put(fsPath, new DFSNode(nodeName, file.isFile()? true: false));
                }else if(Log.Operate.DELETE.equals(operate)){
                    map.remove(fsPath);
                }else{
                    System.out.println("operate [" + operate + "] do nothing.");
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("元数据日志丢失");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("读取文件内容异常");
            e.printStackTrace();
        }
        return map;
    }
}
