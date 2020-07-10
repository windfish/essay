package com.demon.hadoop.custom.fs_distributed;

import java.io.*;
import java.util.Date;
import java.util.List;

public class IOUtil {

    /**
     * 数据上传操作，将一个大文件拆分成多个小文件，上传到不同服务器，并且复制多个副本
     * @param inputFile
     * @param outputDir
     */
    public static void copyFileToDFS(String inputFile, String outputDir){
        // 选择服务器
        BlockManager blockManager = new BlockManager();

        // 文件大小
        File file = new File(inputFile);
        long length = file.length();

        // 总共多少个数据块
        int blockSize = Integer.parseInt(PropertiesUtil.getProperty("block_size"));
        double blocks = length * 1D / blockSize;
        int actualBlocks = (int) Math.ceil(blocks);

        // 文件输入流
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 依次处理每个数据块
        for(int i=0; i<actualBlocks; i++){
            List<Server> servers = blockManager.choseServer(blockManager.loadServers());

            // 计算出一个数据块在一台服务器上的一个路径
            String blockId = "block_" + new Date().getTime();
            Server server = servers.get(0);
            String serverPath = server.getServerPath();
            String parentPath = (serverPath + "/" + outputDir).replace("//", "/").replace("/", "/");
            File parenrFile = new File(parentPath);
            if(!parenrFile.exists()){
                mkdir(parentPath);
            }
            // 第一个数据块的第一个副本在对应的服务器上的存储路径
            String perfectServerPath = serverPath + "/" + outputDir + "/" + blockId;
            perfectServerPath = perfectServerPath.replace("//", "/").replace("/", "/");
            System.out.println("block 路径：" + perfectServerPath);

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(perfectServerPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // 拷贝文件搭配服务器
            copyFile(in, out, blockSize);

            // 处理数据块的副本，将数据块从第一个服务器复制到其余的服务器上
            copyReplication(servers, perfectServerPath, outputDir, blockId);

            try{
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(InputStream in, OutputStream out, int blockSize){
        try {
            // 一次读4096 字节数据，计算总的读取次数
            int buffer = 4096;
            int total = blockSize / buffer;
            byte[] byteArray = new byte[buffer];

            int length = 0; // 实际读取的数据大小
            int counter = 0;

            while((length = in.read(byteArray)) > 0){
                out.write(byteArray);
                counter++;
                if(counter == total){
                    break;
                }
            }
        }catch (FileNotFoundException e){
            System.out.println("文件不存在！");
            e.printStackTrace();
        }catch (IOException e) {
            System.out.println("读写文件异常！");
            e.printStackTrace();
        }
    }

    public static void copyReplication(List<Server> servers, String path, String outDir, String blockId){
        if(servers.size() == 1){
            System.out.println("单机模式，不存储副本");
            return;
        }
        for(int i=1; i<servers.size(); i++){
            String inputPath = path;
            String serverPath = servers.get(i).getServerPath();

            String outputPath = serverPath + "/" + outDir + "/" + blockId;
            outputPath.replace("//", "/").replace("/", "/");
            System.out.println("副本存放路径：" + outputPath);

            File current = new File(outputPath);
            File parent = current.getParentFile();
            if(!parent.exists()){
                mkdir(current.getParent());
            }

            copyFile(inputPath, outputPath);
        }
    }

    public static void copyFile(String input, String output){
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(input);
            out = new FileOutputStream(output);

            byte[] byteArray = new byte[4096];
            int length = 0;
            while((length = in.read(byteArray)) > 0){
                out.write(byteArray);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void mkdir(String path){
        File current = new File(path);
        File parent = current.getParentFile();

        if(!parent.exists()){
            mkdir(current.getParent());
        }

        current.mkdir();
    }
}
