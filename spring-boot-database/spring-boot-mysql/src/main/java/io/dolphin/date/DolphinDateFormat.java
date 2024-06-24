package io.dolphin.date;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DolphinDateFormat {
    public static void main(String[] args) throws ParseException, IOException {
        String dateStr = "Web Nov 01 09:00:00 CST 2023";
        SimpleDateFormat cst = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat gmt = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
//        Date dateTime = gmt.parse(dateStr);
//
//        String dateString = cst.format(dateTime);
//        System.out.println(dateString);

        Date date = new Date();
        System.out.println(date);
        String format = cst.format(date);
        System.out.println(format);

        Path pathPdf = Paths.get("D:\\SoftwareCache\\WeiChatCache\\WeChat Files\\ql1174021481\\FileStorage\\File\\2023-11\\深圳地铁2035规划图.pdf");
        byte[] pdf = Files.readAllBytes(pathPdf);
        System.out.println(pdf);
        String pdfCode = Base64.encodeBase64String(pdf);
        System.out.println(pdfCode);
        byte[] pdfBytes = Base64.decodeBase64(pdfCode);
        System.out.println(pdfBytes);

        File file = new File("D:\\SoftwareCache\\WeiChatCache\\WeChat Files\\ql1174021481\\FileStorage\\File\\2023-11\\深圳地铁2035规划图.pdf");
        InputStream inputStream = new FileInputStream(file);
        // 创建一个输入流资源
        InputStreamResource resource = new InputStreamResource(inputStream);
    }
}
