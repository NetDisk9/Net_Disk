package com.net.file.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    public static boolean checkImageValid(String newPassword) {
        String regex = "\\.(jpg|jpeg|png|gif|bmp)$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建匹配器
        Matcher matcher = pattern.matcher(newPassword);
        // 进行匹配
        return matcher.matches();
    }
}
