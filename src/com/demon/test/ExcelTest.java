package com.demon.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author xuliang
 * @since 2020年4月17日 下午3:16:37
 *
 */
public class ExcelTest {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String dir = "/data/eyun_desktop/";
        String filename = "202001";
        String ext = ".txt";
        
        int dataCalc = 0;
        int noDataLoopNum = 0;
        Map<String, String> map = new HashMap<>();
        String dataKey = "";
        
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(new File(dir+filename+ext)));
            String line;
            while(true){
                line = reader.readLine();
                System.out.println(line);
                if(line == null || "".equals(line.trim())){
                    dataCalc = 0;
                    noDataLoopNum += 1;
                }else{
                    dataCalc += 1;
                    noDataLoopNum = 0;
                }
                if(dataCalc == 1){
                    dataKey = line;
                    continue;
                }
                if(dataCalc == 0 && noDataLoopNum >= 2){
                    break;
                }
                if(dataCalc == 0){
                    continue;
                }
                map.put(dataKey, line);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(reader != null){
                reader.close();
            }
        }
        System.out.println(JSON.toJSONString(map));
        
        
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet=wb.createSheet("stat");
        HSSFRow row1=sheet.createRow(0);
        
        row1.createCell(0).setCellValue("日期");  
        row1.createCell(1).setCellValue("网吧数");      
        row1.createCell(2).setCellValue("日活");  
        row1.createCell(3).setCellValue("终端数");
        row1.createCell(4).setCellValue("UV数");
        
        int i = 0;
        for(Map.Entry<String, String> entry: map.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            JSONObject jsonObj = JSON.parseObject(value);
            
            HSSFRow row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(key);  
            row.createCell(1).setCellValue(jsonObj.getString("real_time_online_netbars_valid"));      
            row.createCell(2).setCellValue(jsonObj.getString("total_pc_active_today"));  
            row.createCell(3).setCellValue(jsonObj.getString("real_time_online_clients"));
            row.createCell(4).setCellValue(jsonObj.getString("total_pc_uv_today"));
            
            i++;
        }
        
        wb.write(new FileOutputStream(new File("/data/eyun_desktop/stat"+filename+".xls")));
        wb.close();
    }
    
}
