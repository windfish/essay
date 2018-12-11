package com.demon.test.verifycode;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * 拼图验证码
 * 
 * @author xuliang
 * @since 2018年12月10日 上午11:29:35
 *
 */
public class JigsawCode {

    private final static int PIC_LENGTH = 300;
    private final static int PIC_WIDTH = 200;
    
    private final static Random r = new Random();
    
    private final static int SQUARE_SIDE = 30;
    /**
     * <pre>
     * 随机正方形的区域
     * 
     * 轮廓使用二维数组表示，轮廓内像素点值为1，轮廓外像素点值为0
     * </pre>
     */
    public int[][] getSquareData(Square square){
        int[][] data = new int[PIC_LENGTH][PIC_WIDTH];
        
        int xEnd = square.xBegin + SQUARE_SIDE;
        int yEnd = square.yBegin + SQUARE_SIDE;
        
        for(int i=0;i<PIC_LENGTH;i++){
            for(int j=0;j<PIC_WIDTH;j++){
                if(i>=square.xBegin && i<xEnd
                        && j>=square.yBegin && j<yEnd){
                    data[i][j] = 1;
                }else{
                    data[i][j] = 0;
                }
            }
        }
        return data;
    }
    
    public Square generator(){
        int xRegion = PIC_LENGTH - SQUARE_SIDE - 2;
        int yRegion = PIC_WIDTH - SQUARE_SIDE - 2;
        
        // 随机正方形的左上定点
        int xBegin = r.nextInt(xRegion) + 1;
        int yBegin = r.nextInt(yRegion) + 1;
        
        return new Square(xBegin, yBegin, SQUARE_SIDE);
    }
    
    public BufferedImage cutPic(BufferedImage oriImg, Square square){
        return oriImg.getSubimage(square.xBegin, square.yBegin, square.side, square.side);
    }
    
    Color color = new Color(160, 146, 154);
    int rgb = color.getRGB();
    public BufferedImage ashingPic(BufferedImage oriImg, int[][] ashingPic){
        for(int i=0;i<PIC_LENGTH;i++){
            for(int j=0;j<PIC_WIDTH;j++){
                int flag = ashingPic[i][j];
                if(flag == 1){
                    oriImg.setRGB(i, j, rgb);
                }
            }
        }
        return oriImg;
    }
    
    public static JigsawCode instance(){
        return new JigsawCode();
    }
    
    public static void main(String[] args) throws Exception {
        FileInputStream fis = new FileInputStream(new File("D:\\work\\git\\essay\\src\\com\\demon\\test\\verifycode\\2.jpg"));
        BufferedImage image = ImageIO.read(fis);
//        System.out.println(image.getRGB(188, 190));
//        System.out.println(image.getRGB(189, 190));
//        System.out.println(image.getRGB(190, 190));
//        System.out.println(image.getRGB(0, 0));
        
        JigsawCode instance = instance();
        Square square = instance.generator();
        int[][] ashingPic = instance.getSquareData(square);
        BufferedImage cutPic = instance.cutPic(image, square);
        ImageIO.write(cutPic, "jpg", new File("D:\\cut.jpg"));
        
        BufferedImage fullImg = instance.ashingPic(image, ashingPic);
        ImageIO.write(fullImg, "jpg", new File("D:\\full.jpg"));
    }
    
    static class Square {
        public int xBegin;
        public int yBegin;
        public int side;
        
        public Square() {
        }

        public Square(int xBegin, int yBegin, int side) {
            super();
            this.xBegin = xBegin;
            this.yBegin = yBegin;
            this.side = side;
        }
    }
    
}
