package com.net.file.factory;

import com.net.common.util.DateFormatUtil;
import com.net.common.util.LongIdUtil;
import com.net.file.constant.DirConstants;
import com.net.file.constant.FileStatusConstants;
import com.net.file.entity.FileData;
import com.net.file.entity.UserFileEntity;
import com.net.file.util.PathUtil;

import java.time.LocalDateTime;

public class UserFileEntityFactory {
    public static UserFileEntity createDirEntity(UserFileEntity parent, String name, Long userId){
        String dateTime= DateFormatUtil.format(LocalDateTime.now());
        UserFileEntity userFile = UserFileEntity.builder()
                .filePath(parent == null ? "/"+name : parent.getFilePath() + "/" + name)
                .fileName(name)
                .userId(userId)
                .isDir(DirConstants.IS_DIR)
                .pid(parent == null ? null : parent.getUserFileId())
                .status(FileStatusConstants.NORMAL)
                .createTime(dateTime)
                .updateTime(dateTime)
                .recycleTime(null)
                .build();
        return userFile;
    }
    public static UserFileEntity createDirEntity(String path,Integer status,Long userId){
        String dateTime= DateFormatUtil.format(LocalDateTime.now());
        String name= PathUtil.getNameFromPath(path);
        UserFileEntity userFile = UserFileEntity.builder()
                .filePath(path)
                .fileName(name)
                .userId(userId)
                .isDir(DirConstants.IS_DIR)
                .pid(null)
                .status(status)
                .createTime(dateTime)
                .updateTime(dateTime)
                .recycleTime(null)
                .build();
        return userFile;
    }
    public static UserFileEntity createRootDirEntity(Long userId){
        UserFileEntity root=UserFileEntity.builder()
                .filePath("")
                .isDir(DirConstants.IS_DIR)
                .status(FileStatusConstants.NORMAL)
                .userId(userId).build();
        return root;
    }
    public static UserFileEntity createFileEntity(FileData fileData,String path,String name,Long userId){
        String dateTime= DateFormatUtil.format(LocalDateTime.now());
        UserFileEntity file=UserFileEntity.builder()
                .fileId(fileData.getFileId())
                .userId(userId)
                .fileName(name)
                .filePath(path)
                .isDir(DirConstants.NOT_DIR)
                .status(FileStatusConstants.NORMAL)
                .createTime(dateTime)
                .updateTime(dateTime)
                .recycleTime(null).build();
        return file;
    }
}
