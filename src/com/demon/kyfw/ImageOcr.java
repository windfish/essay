package com.demon.kyfw;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图片下载，切分，合并
 * 
 * @author xuliang
 * @since 2018年1月6日 下午4:10:14
 *
 */
public class ImageOcr {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
	private String imageUrl = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=login&rand=sjrand&0.3424303620122373";
	
	public void getImage() throws Exception{
		//new一个URL对象  
        URL url = new URL(imageUrl);  
        //打开链接  
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
        //设置请求方式为"GET"  
        conn.setRequestMethod("GET");  
        //超时响应时间为5秒  
        conn.setConnectTimeout(5 * 1000);  
        //通过输入流获取图片数据  
        InputStream inStream = conn.getInputStream();  
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性  
        byte[] data = readInputStream(inStream);  
        logger.info("image data:{}", new String(data));
        //new一个文件对象用来保存图片，默认保存当前工程根目录  
        File imageFile = new File("D:\\img\\code.jpg");
        //创建输出流  
        FileOutputStream outStream = new FileOutputStream(imageFile);  
        //写入数据  
        outStream.write(data);  
        //关闭输出流  
        outStream.close();  
	}
	
	private byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        //创建一个Buffer字符串  
        byte[] buffer = new byte[1024];  
        //每次读取的字符串长度，如果为-1，代表全部读取完毕  
        int len = 0;  
        //使用一个输入流从buffer里把数据读取出来  
        while( (len=inStream.read(buffer)) != -1 ){  
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
            outStream.write(buffer, 0, len);  
        }  
        //关闭输入流  
        inStream.close();  
        //把outStream里的数据写入内存  
        return outStream.toByteArray();  
    }  
	
	private static final String BASE_URL = "D:\\img\\";
	public void splitImage(String imgUrl){
	    try{
    	    File file = new File(imgUrl);
    	    FileInputStream fis = new FileInputStream(file);
    	    BufferedImage image = ImageIO.read(fis);
    	    
    	    int imageHeight = image.getHeight();
    	    int imageWidth = image.getWidth();
    	    logger.info("image height:{} width:{}", imageHeight, imageWidth);
    	    
    	    BufferedImage[] imgs = new BufferedImage[9];
    	    int index = 0;
    	    
    	    // 截取头部
    	    int headHeight = 36;
    	    int headWidth = imageWidth;
    	    imgs[index] = new BufferedImage(headWidth / 2, headHeight, image.getType());
    	    Graphics2D gr = imgs[index].createGraphics();
    	    /*
    	     * public abstract boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
                                  int sx1, int sy1, int sx2, int sy2, ImageObserver observer)
                img - 要绘制的指定图像。如果 img 为 null，则此方法不执行任何操作。
                dx1 - 目标矩形第一个角的 x 坐标。
                dy1 - 目标矩形第一个角的 y 坐标。
                dx2 - 目标矩形第二个角的 x 坐标。
                dy2 - 目标矩形第二个角的 y 坐标。
                sx1 - 源矩形第一个角的 x 坐标。
                sy1 - 源矩形第一个角的 y 坐标。
                sx2 - 源矩形第二个角的 x 坐标。
                sy2 - 源矩形第二个角的 y 坐标。
                observer - 当缩放并转换了更多图像时要通知的对象。
    	     */
    	    gr.drawImage(image, 0, 0, headWidth, headHeight, 120, 0, headWidth, headHeight, null);
    	    gr.dispose();
    	    
    	    // 截取图片
    	    int rows = 2;
    	    int cols = 4;
    	    int chunkHeight = (imageHeight - headHeight) / rows;
    	    int chunkWidth = imageWidth / cols;
    	    
    	    index++;
    	    for(int x=0;x<rows;x++){
    	        for(int y=0;y<cols;y++){
    	            imgs[index] = new BufferedImage(chunkWidth, chunkHeight, image.getType());
    	            Graphics2D gra = imgs[index].createGraphics();
    	            gra.drawImage(image, 0, 0, chunkWidth, chunkHeight, 
    	                    chunkWidth * y, chunkHeight * x + headHeight, chunkWidth * y + chunkWidth, chunkHeight * x + headHeight + chunkHeight, null);
    	            gra.dispose();
    	            index++;
    	        }
    	    }
    	    
    	    for(int i=0;i<imgs.length;i++){
    	        ImageIO.write(imgs[i], "jpg", new File(BASE_URL + i + ".jpg"));
    	    }
    	    
	    }catch (Exception e) {
            logger.error("split image exception", e);
        }
	}
	
	public static void main(String[] args) throws Exception {
//		new ImageOcr().getImage();
	    new ImageOcr().splitImage("D:\\img\\code.jpg");
	    
	}
	
}
