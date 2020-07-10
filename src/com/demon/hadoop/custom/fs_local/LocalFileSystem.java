package com.demon.hadoop.custom.fs_local;

import com.demon.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class LocalFileSystem implements FileSystem {

    @Override
    public void put(String file, String dest) {
        File f = new File(file);
        String fileName = f.getName();
        String destPath = FileSystemUtil.formatPath(dest);

        String destFilePath = destPath + fileName;
        File destFile = new File(destFilePath);
        if(destFile.exists()){
            return;
        }

        // 目录不存在，则创建
        if(!new File(destPath).exists()){
            mkdir(destPath);
        }

        FileUtils.copyFileByChannel(f, destFile);
    }

    @Override
    public void get(String file, String dest) {
        String fsPath = FileSystemUtil.formatPath(file);
        File source = new File(fsPath);

        String destPath = dest + source.getName();

        FileUtils.copyFileByChannel(source, new File(destPath));
    }

    @Override
    public void mkdir(String dir) {
        String c = dir;
        if(c == null || "".equals(c)){
            System.out.println("dir error");
            return;
        }
        if(c.indexOf(FileSystem.root) < 0){
            c = FileSystemUtil.formatPath(c);
        }
        System.out.println(c);
        File current = new File(c);
        File parent = current.getParentFile();

        if(!parent.exists()){
            mkdir(current.getParent());
        }

        boolean mkdir = current.mkdir();
        System.out.println(current.getPath() + " mkdir result: " + mkdir);
    }

    @Override
    public List<String> list(String path) {
        String wholePath = FileSystemUtil.formatPath(path);
        File current = new File(wholePath);
        if(current.isFile()){
            System.out.println("指定的是文件，无法展示字子节点信息");
            return null;
        }else{
            File[] files = current.listFiles();
            List<String> result = new ArrayList<>();
            for(File f: files){
                result.add(f.getAbsolutePath().replace(root, ""));
            }
            return result;
        }
    }
}
