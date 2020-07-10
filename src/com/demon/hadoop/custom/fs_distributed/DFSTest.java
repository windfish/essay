package com.demon.hadoop.custom.fs_distributed;

public class DFSTest {

    public static void main(String[] args) {
        FileSystem fs = new DistributedFileSystem();

        String file = "/Users/xuliang/data/tools/apache-maven-3.6.3-bin.tar.gz";
        String destDir = "/a/b";

        fs.put(file, destDir);
    }
}
