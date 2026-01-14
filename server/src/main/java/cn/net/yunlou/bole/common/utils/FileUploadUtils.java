package cn.net.yunlou.bole.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * FileName: FileUploadUtils Description: Created By laughtiger Created At 2026/1/10 02:16 Modified
 * By Modified At
 */
@Slf4j
@Component
public class FileUploadUtils {

    public FileUploadUtils() {
        log.debug("FileUploadUtils initialized");
    }

    /** 获取Content-Type */
    public static String getContentType(String fileName) {
        String lowerCaseName = fileName.toLowerCase();

        if (lowerCaseName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerCaseName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (lowerCaseName.endsWith(".doc")) {
            return "application/msword";
        } else if (lowerCaseName.endsWith(".html") || lowerCaseName.endsWith(".htm")) {
            return "text/html";
        } else if (lowerCaseName.endsWith(".txt")) {
            return "text/plain";
        } else {
            return "application/octet-stream";
        }
    }
}
