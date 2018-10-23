package com.demon.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class FileUtils {  
	
	/**
	 * 返回文件大小
	 * @param size
	 * @return
	 */
	public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
 
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }
	
    /** 
     * the traditional io way  
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    public static byte[] toByteArray(String filename) throws IOException{  
          
        File f = new File(filename);  
        if(!f.exists()){  
            throw new FileNotFoundException(filename);  
        }  
  
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int)f.length());  
        BufferedInputStream in = null;  
        try{  
            in = new BufferedInputStream(new FileInputStream(f));  
            int buf_size = 1024;  
            byte[] buffer = new byte[buf_size];  
            int len = 0;  
            while(-1 != (len = in.read(buffer,0,buf_size))){  
                bos.write(buffer,0,len);  
            }  
            return bos.toByteArray();  
        }catch (IOException e) {  
            e.printStackTrace();  
            throw e;  
        }finally{  
            try{  
                in.close();  
            }catch (IOException e) {  
                e.printStackTrace();  
            }  
            bos.close();  
        }  
    }  
      
      
    /** 
     * NIO way 
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    public static byte[] toByteArray2(String filename)throws IOException{  
          
        File f = new File(filename);  
        if(!f.exists()){  
            throw new FileNotFoundException(filename);  
        }  
          
        FileChannel channel = null;  
        FileInputStream fs = null;  
        try{  
            fs = new FileInputStream(f);  
            channel = fs.getChannel();  
            ByteBuffer byteBuffer = ByteBuffer.allocate((int)channel.size());  
            while((channel.read(byteBuffer)) > 0){  
                // do nothing  
//              System.out.println("reading");  
            }  
            return byteBuffer.array();  
        }catch (IOException e) {  
            e.printStackTrace();  
            throw e;  
        }finally{  
            try{  
                channel.close();  
            }catch (IOException e) {  
                e.printStackTrace();  
            }  
            try{  
                fs.close();  
            }catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
      
      
    /** 
     * Mapped File  way 
     * MappedByteBuffer 可以在处理大文件时，提升性能 
     * @param filename 
     * @return 
     * @throws IOException 
     */  
    @SuppressWarnings("resource")
	public static byte[] toByteArray3(String filename)throws IOException{  
          
        FileChannel fc = null;  
        try{  
            fc = new RandomAccessFile(filename,"r").getChannel();  
            MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();  
            System.out.println(byteBuffer.isLoaded());  
            byte[] result = new byte[(int)fc.size()];  
            if (byteBuffer.remaining() > 0) {  
//              System.out.println("remain");  
                byteBuffer.get(result, 0, byteBuffer.remaining());  
            }  
            return result;  
        }catch (IOException e) {  
            e.printStackTrace();  
            throw e;  
        }finally{  
            try{  
                fc.close();  
            }catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    } 
    
    /**
	 * 把byte 保存为文件
	 * 
	 * @param b
	 * @param outputFile
	 * @return
	 */
	public static File getFileFromBytes(byte[] b, String outputFile)
	{
		BufferedOutputStream stream = null;
		File file = null;
		try
		{
			file = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}
		return file;
	}
	
	/**
	 * 基于java io 的文件拷贝
	 * @param source 源文件
	 * @param dest 目标文件
	 */
	public static void copyFileByStream(File source, File dest){
	    try(FileInputStream fis = new FileInputStream(source);
	            FileOutputStream fos = new FileOutputStream(dest);){
	        byte[] buffer = new byte[10];
	        int length;
	        while((length = fis.read(buffer)) > 0){
	            fos.write(buffer, 0, length);
	        }
	    } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 基于java nio 的transferTo 或transferFrom 实现
	 * @param source 源文件
	 * @param dest 目标文件
	 */
	public static void copyFileByChannel(File source, File dest) {
	    try(FileChannel sourceChannel = new FileInputStream(source).getChannel();
	            FileChannel targetChannel = new FileOutputStream(dest).getChannel();){
	        for(long count = sourceChannel.size(); count > 0; ){
	            long transferTo = sourceChannel.transferTo(sourceChannel.position(), count, targetChannel);
	            count -= transferTo;
	        }
	    } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
}  
