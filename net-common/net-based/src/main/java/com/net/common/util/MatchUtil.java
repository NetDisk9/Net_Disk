package com.net.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchUtil {
    private Pattern pattern;
    public MatchUtil(String pattern){
        this.pattern=Pattern.compile(pattern);
    }
    public boolean match(String str){
        Matcher matcher=pattern.matcher((str));
        return matcher.matches();
    }
}
