package com.net.file.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

public class HttpHeaderUtil {
    private static final String RANGE_EXPR="^bytes\\s*=\\s*\\d+\\s*-\\s*\\d+\\s*$";
    private static final String BYTES="bytes";
    public static Range parseRange(String range){
        if(!range.matches(RANGE_EXPR)){
            throw new RuntimeException();
        }
        int begPos=range.indexOf("="),endPos=range.indexOf("-");
        Long rangeBegin=Long.parseLong(range.substring(begPos+1,endPos).strip());
        Long rangeEnd=Long.parseLong(range.substring(endPos+1).strip());
        if(rangeEnd<rangeBegin){
            throw new RuntimeException();
        }
        return new Range(rangeBegin,rangeEnd);
    }
    public static String buildContentRange(Range range,Long size){
        StringBuilder sb=new StringBuilder();
        sb.append(BYTES);
        sb.append(" ");
        sb.append(range.getBegin());
        sb.append("-");
        sb.append(range.getEnd());
        sb.append("/");
        sb.append(size.toString());
        return sb.toString();
    }
    @Data
    @AllArgsConstructor
    @ToString
    public static class Range{
        Long begin;
        Long end;
        public Long getLength(){
            return end-begin+1;
        }
    }

}
