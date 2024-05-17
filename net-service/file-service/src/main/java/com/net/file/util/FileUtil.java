package com.net.file.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class FileUtil {
    /**
     * 获取一个文件的md5值(可处理大文件)
     *
     * @return md5 value
     */
    public static FileMetaData getMetaData(InputStream inputStream) {
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length;
            Long totLen=0L;
            while ((length = inputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
                totLen+=length;
            }
            return new FileMetaData(new String(Hex.encodeHex(MD5.digest())),totLen);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Data
    @AllArgsConstructor
    public static class FileMetaData{
        private String md5;
        private Long fileSize;
    }
}
