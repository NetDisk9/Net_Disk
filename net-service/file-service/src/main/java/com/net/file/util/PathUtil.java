package com.net.file.util;

import com.net.file.constant.DirConstants;
import com.net.file.entity.UserFileEntity;

import java.util.List;

public class PathUtil {

    /**
     * 替换路径中的文件名
     * @param target
     * @param replacement
     * @return {@link String }
     */
    public static String replacePathName(String target,String replacement){
        int pos = target.lastIndexOf("/");
        return target.substring(0,pos+1)+replacement;
    }

    /**
     * 返回路径中的文件名
     * @param path
     * @return {@link String }
     */
    public static String getNameFromPath(String path){
        int pos=path.lastIndexOf("/");
        return path.substring(pos+1);
    }

    /**
     * 返回扩展名 .txt
     * @param name
     * @return {@link String }
     */
    public static String getExtName(String name){
        int pos=name.lastIndexOf(".");
        if(pos==-1){
            return null;
        }
        return name.substring(pos);
    }

    /**
     * 返回不带扩展名的文件名
     * @param name
     * @return {@link String }
     */
    public static String getPlainName(String name){
        int pos=name.lastIndexOf(".");
        if(pos==-1){
            return name;
        }
        return name.substring(0,pos);
    }
    public static boolean checkPath(UserFileEntity parent, List<UserFileEntity> list){
        String parentPath=parent.getFilePath()+"/";
        for(var userFile:list){
            if(!DirConstants.IS_DIR.equals(userFile.getIsDir()))
                continue;
            String childPath=userFile.getFilePath()+"/";
            if(parentPath.startsWith(childPath)){
                return false;
            }
        }
        return true;
    }

}
