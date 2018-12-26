package com.demon.kyfw;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Tesseract;

public class TesseractOcr {

    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File("D:\\en.png"));
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("D:\\data\\tessdata");
        String result = tesseract.doOCR(image);
        System.out.println(result);
    }
    
}
