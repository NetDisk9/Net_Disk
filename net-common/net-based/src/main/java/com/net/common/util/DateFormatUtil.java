package com.net.common.util;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.SplittableRandom;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(temp);
    }
    public static LocalDateTime string2LocalDateTime(String str){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(str);
        return LocalDateTime.parse(str, formatter);
    }
    /**
     * @Author:chenjunjie
     * 得到当前日期的字符串的方法
     */
    public static String getNow(){
        LocalDateTime localDateTime= LocalDateTime.now();
        return format(localDateTime);
    }

    /**
     *
     * @Author:CheeseLongan
     * 在当前日期加上一个持续时间得到的结束时间
     */
    public static String addDays(String currentDateString, int durationDays) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentDateTime = LocalDateTime.parse(currentDateString, formatter);
        LocalDateTime futureDateTime = currentDateTime.plusDays(durationDays);
        return futureDateTime.format(formatter);
    }
    /**
     *
     * @Author:CheeseLongan
     * 比较前者的日期是否小于后者
     *
     */
    public static boolean isBefore(String date1, String date2) {
        try {
            return trans(date1).before(trans(date2));
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
