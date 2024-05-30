package com.net.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.net.common.exception.AuthException;
import com.net.common.exception.ParameterException;
import com.net.file.entity.UserFileEntity;
import com.net.file.pojo.dto.FileQueryDTO;
import com.net.file.support.UserFileTree;
import com.net.file.pojo.dto.FileMoveDTO;

import java.util.List;

public interface FileService extends IService<UserFileEntity> {
    UserFileEntity getUserFileByUserFileId(Long userFileId);

    List<UserFileEntity> listUserFileByPidAndPath(Long pid, String path, Integer status, Long userId);

    List<UserFileEntity> listUserFileByPidAndPathInDir(Long pid, String path, Integer status, Long userId);

    List<UserFileEntity> listUserFileInDir(String path, Integer status, Long userId);

    void insertDirFile(UserFileEntity userFile);
    void insertFile(UserFileEntity userFile);
    void updateFile(UserFileEntity userFile);
    void insertBatch(List<UserFileEntity> list);

    UserFileEntity getNormalUserFileByPath(String path,Long userId);

    boolean isExist(String path,Long userId);

    UserFileEntity getFile(Long userFileId, Long userId) throws ParameterException, AuthException;

    UserFileEntity getNormalFile(Long userFileId, Long userId) throws AuthException, ParameterException;

    UserFileEntity getFileWithoutCheck(Long userFileId) throws ParameterException;

    UserFileTree buildUserFileTree(UserFileEntity parentFile,List<UserFileEntity> list);
    List<UserFileEntity> copyFile(FileMoveDTO fileMoveDTO,Integer mode) throws Throwable;
    void saveFiles(UserFileEntity root,List<UserFileEntity> list,Long userId);

    void updateFileFoldStatus(List<Long> fileIds, Integer BEFORE_MODE, Integer AFTER_MODE);
    UserFileEntity getFileIdByPath(String path,Long userId);
    void restoreParent(UserFileEntity file);
    IPage selectPageVO(Page<UserFileEntity> pageInfo, FileQueryDTO fileQueryDTO);
}
