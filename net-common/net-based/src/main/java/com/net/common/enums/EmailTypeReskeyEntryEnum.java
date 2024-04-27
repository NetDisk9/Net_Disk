package com.net.common.enums;

import com.net.common.constant.EmailKeyConstants;
import com.net.common.constant.EmailTypeConstants;

public enum EmailTypeReskeyEntryEnum{

    RESET_RES_ENTRY(EmailTypeConstants.RESET, EmailKeyConstants.RESET_PASSWORD_RES_KEY);

    private String key;
    private String val;

    private EmailTypeReskeyEntryEnum(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public static String find(String type){
        EmailTypeReskeyEntryEnum[] reskeyEntryEnums = values();
        for(var entry:reskeyEntryEnums){
            if(entry.key.equals(type))
                return entry.val;
        }
        return null;
    }
}
