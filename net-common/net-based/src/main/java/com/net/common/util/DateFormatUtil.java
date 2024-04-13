package com.net.common.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @Author:chenjunjie
 * 日期格式化类
 */
public class DateFormatUtil {
    /**
     * @Author:chenjunjie
     * 将localDateTime转换为特定格式的String
     */
    public static String format(LocalDateTime localDateTime){
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(fmt);
    }
    /**
     * @Author:chenjunjie
     * 将字符串转换为Date
     */
    public static Date trans(String temp) throws Exception{
        if(temp==null||temp.isEmpty())
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(temp);
    }
    /**
     * @Author:chenjunjie
     * 得到当前日期的字符串的方法
     */
    public static String getNow(){
        LocalDateTime localDateTime=LocalDateTime.now();
        return localDateTime.getYear()+"-"+localDateTime.getMonthValue()+"-"+localDateTime.getDayOfMonth()+" "+localDateTime.getHour()+":"+localDateTime.getMinute()+":"+localDateTime.getSecond();
    }
}