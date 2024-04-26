package com.net.file.util;

import com.net.file.constant.DirConstants;
import com.net.file.entity.UserFileEntity;

import java.util.List;

public class PathUtil {
    public static String replaceLastPath(String target,String replacement){
        int pos = target.lastIndexOf("/");
        return target.substring(0,pos+1)+replacement;
    }
    public static boolean checkPath(UserFileEntity parent, List<UserFileEntity> list){
        String parentPath=parent.getFilePath()+"/";
        for(var userFile:list){
            if(!DirConstants.IS_DIR.equals(userFile.getIsDir()))
                continue;
            String childPath=userFile.getFilePath()+"/";
            if(childPath.startsWith(parentPath)){
                return false;
            }
        }
        return true;
    }
}
