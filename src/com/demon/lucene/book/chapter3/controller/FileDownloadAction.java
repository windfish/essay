package com.demon.lucene.book.chapter3.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author xuliang
 * @since 2019年8月14日 下午2:00:47
 *
 */
@Controller
public class FileDownloadAction {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(String filename, HttpServletRequest request) {
        logger.info("download file: {}", filename);
        String filePath = request.getSession().getServletContext().getRealPath("/files") + "/" + filename;
        InputStream in = null;
        try {
           File file = new File(filePath);
           String fileName = new String(filename.getBytes("utf-8"),"iso-8859-1");
            
           HttpHeaders headers = new HttpHeaders();//设置响应头
           headers.add("Content-Disposition", "attachment;filename="+fileName);
           headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
           ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
           return response;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(in != null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
}
