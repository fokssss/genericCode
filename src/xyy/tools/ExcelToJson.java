package xyy.tools;

import org.json.JSONArray;
import xyy.tools.excel.ExcelReader;
import xyy.tools.excel.XTable;

import java.io.*;
import java.util.Map;

/**
 * Created by xyy on 2017/4/16.
 */
public class ExcelToJson {

    public static void main(String[] args) {
        try {
            // 对读取Excel表格内容测试
            InputStream is2 = new FileInputStream("/Users/xyy/temp/test.xls");
            XTable table = new ExcelReader().toJson(is2);
            JSONArray result = table.toJson();
            System.out.println("获得Excel表格的内容:");
            System.out.println(result.toString());
            is2.close();

            FileOutputStream out = new FileOutputStream("/Users/xyy/temp/test.json");
            out.write(result.toString().getBytes("UTF-8"));
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}