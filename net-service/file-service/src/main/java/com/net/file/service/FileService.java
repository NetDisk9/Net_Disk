package com.net.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.common.exception.AuthException;
import com.net.common.exception.ParameterException;
import com.net.file.entity.UserFileEntity;
import com.net.file.support.UserFileTree;
import com.net.file.pojo.dto.FileMoveDTO;

import java.util.List;

public interface FileService extends IService<UserFileEntity> {
    UserFileEntity getUserFileByUserFileId(Long userFileId);



    List<UserFileEntity> listUserFileByPidAndPath(Long pid, String path, Integer status, Long userId);

    List<UserFileEntity> listUserFileByPidAndPathInDir(Long pid, String path, Integer status, Long userId);

    List<UserFileEntity> listUserFileInDir(String path, Integer status, Long userId);

    void insertFile(UserFileEntity userFile);
    void updateFile(UserFileEntity userFile);
    void insertBatch(List<UserFileEntity> list);

    UserFileEntity getNormalUserFileByPath(String path);

    boolean isExist(String path);

    UserFileEntity getFile(Long userFileId, Long userId) throws ParameterException, AuthException;

    UserFileEntity getNormalFile(Long userFileId, Long userId) throws AuthException, ParameterException;

    UserFileEntity getFileWithoutCheck(Long userFileId) throws ParameterException;

    UserFileTree buildUserFileTree(FileMoveDTO fileMoveDTO, List<UserFileEntity> failCollector,Integer mode) throws AuthException, ParameterException;

    void removeFile2Recycle(List<Long> fileIds);
}
