package com.net.user.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    public static boolean checkPasswordValid(String newPassword) {
        // 大小写英文字母、数字、特殊字符（@、#、$、%、^、&、*），至少包含两种，长度为8到14个字符；
        String regex = "^(?=.*[a-z])(?=.*[A-Z0-9@#$%^&*])[a-zA-Z0-9@#$%^&*]{8,14}$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建匹配器
        Matcher matcher = pattern.matcher(newPassword);
        // 进行匹配
        return matcher.matches();
    }
}
