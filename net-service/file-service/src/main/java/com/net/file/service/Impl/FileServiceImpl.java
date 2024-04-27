package com.net.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.context.BaseContext;
import com.net.common.exception.AuthException;
import com.net.common.exception.ParameterException;
import com.net.common.util.DateFormatUtil;
import com.net.common.vo.PageResultVO;
import com.net.file.constant.DirConstants;
import com.net.file.constant.FileOperationModeConstants;
import com.net.file.constant.FileStatusConstants;
import com.net.file.entity.UserFileEntity;
import com.net.file.pojo.dto.FileDTO;
import com.net.file.support.UserFileTree;
import com.net.file.mapper.FileMapper;
import com.net.file.pojo.dto.FileMoveDTO;
import com.net.file.service.FileService;
import com.net.file.util.PathUtil;
import com.net.file.util.UsefulNameUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, UserFileEntity> implements FileService {
    @Resource
    FileMapper fileMapper;

    @Override
    public UserFileEntity getUserFileByUserFileId(Long userFileId) {
        return getById(userFileId);
    }

    @Override
    public List<UserFileEntity> listUserFileByPidAndPath(Long pid, String path, Integer status) {
        path += "/";
        return fileMapper.listUserFileByPidAndPath(pid, path, status);
    }

    @Override
    public void insertFile(UserFileEntity userFile) {
        save(userFile);
    }

    @Override
    public void updateFile(UserFileEntity userFile) {
        updateById(userFile);
    }

    @Override
    public List<UserFileEntity> listUserFileByPath(String path, Integer status) {
        path += "/";
        return list(new LambdaQueryWrapper<UserFileEntity>().likeRight(UserFileEntity::getFilePath, path).eq(UserFileEntity::getStatus, status));
    }

    @Override
    public void insertBatch(List<UserFileEntity> list) {
        saveBatch(list);
    }

    @Override
    public UserFileEntity getUserFileByPath(String path) {
        return getOne(new LambdaQueryWrapper<UserFileEntity>().eq(UserFileEntity::getFilePath, path));
    }

    @Override
    public boolean isExist(String path) {
        return getUserFileByPath(path) != null;
    }

    @Override
    public UserFileEntity getFile(Long userFileId, Long userId) throws ParameterException, AuthException {
        UserFileEntity userFile = getFileWithoutCheck(userFileId);
        if (!userId.equals(userFile.getUserId())) {
            throw new AuthException();
        }
        return userFile;
    }

    @Override
    public UserFileEntity getNormalFile(Long userFileId, Long userId) throws AuthException, ParameterException {
        UserFileEntity userFile = getFile(userFileId, userId);
        if (!FileStatusConstants.NORMAL.equals(userFile.getStatus())) {
            throw new ParameterException();
        }
        return userFile;
    }

    @Override
    public UserFileEntity getFileWithoutCheck(Long userFileId) throws ParameterException {
        UserFileEntity userFile = getUserFileByUserFileId(userFileId);
        if (userFile == null) {
            throw new ParameterException();
        }
        return userFile;
    }

    @Override
    public UserFileTree buildUserFileTree(FileMoveDTO fileMoveDTO, List<UserFileEntity> failCollector, Integer mode) throws AuthException, ParameterException {
        Long userId = BaseContext.getCurrentId();
        // 判断传入的pid对应的文件是否为正常的文件夹
        UserFileEntity parentFile = getNormalFile(fileMoveDTO.getPid(), userId);
        if (DirConstants.NOT_DIR.equals(parentFile.getIsDir())) {
            throw new ParameterException();
        }
        List<UserFileEntity> userFileEntities;
        // 移动文件，修改文件的所属的文件夹
        userFileEntities = Arrays.stream(fileMoveDTO.getUserFileId()).map(fileId -> {
            try {
                UserFileEntity userFile = getNormalFile(fileId, userId);
                userFile.setPid(parentFile.getUserFileId());
                return userFile;
            } catch (ParameterException | AuthException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        // 移动到当前文件夹
        if (!PathUtil.checkPath(parentFile, userFileEntities)) {
            throw new ParameterException();
        }
        List<UserFileEntity> list = new ArrayList<>();
        for (var entity : userFileEntities) {
            if (isExist(parentFile.getFilePath() + "/" + entity.getFileName())) {// 存在同名文件
                if (FileOperationModeConstants.DEFAULT.equals(mode)) {
                    failCollector.add(entity);
                    continue;
                } else {
                    List<UserFileEntity> temp = listUserFileByPidAndPath(parentFile.getUserFileId(), parentFile.getFilePath(), FileStatusConstants.NORMAL);
                    UsefulNameUtil usefulNameUtil = new UsefulNameUtil(temp, entity.getFileName());
                    entity.setFileName(usefulNameUtil.getNextName());
                }
            }
            if (DirConstants.IS_DIR.equals(entity.getIsDir())) {
                List<UserFileEntity> temp = listUserFileByPath(entity.getFilePath(), FileStatusConstants.NORMAL);
                list.addAll(temp);
            }
            list.add(entity);
        }
        UserFileTree tree = new UserFileTree(new UserFileTree.UserFileTreeNode(parentFile));
        tree.buildTree(list);
        return tree;
    }

    @SneakyThrows
    @Override
    public void removeFile2Recycle(List<Long> fileIds) {
        // 查询待删除的文件或文件夹
        LambdaQueryWrapper<UserFileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFileEntity::getUserId, BaseContext.getCurrentId())
                .eq(UserFileEntity::getStatus, FileStatusConstants.NORMAL)
                .in(UserFileEntity::getUserFileId, fileIds);
        List<UserFileEntity> deleteFiles = fileMapper.selectList(queryWrapper);
        if (deleteFiles.isEmpty()) {
            throw new ParameterException();
        }
        // 递归查询待删除文件的所有子级
        List<Long> deleteFilesAndChildren = new ArrayList<>();
        for (UserFileEntity deleteFile : deleteFiles) {
            if (DirConstants.IS_DIR.equals(deleteFile.getIsDir())) {
                findAllFileList(deleteFilesAndChildren, deleteFile.getUserFileId(),
                        BaseContext.getCurrentId(), FileStatusConstants.NORMAL);
            }else {
                deleteFilesAndChildren.add(deleteFile.getUserFileId());
            }
        }
        // 将待删除文件状态改为回收站
        LambdaUpdateWrapper<UserFileEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserFileEntity::getRecycleTime, DateFormatUtil.format(LocalDateTime.now()))
                .set(UserFileEntity::getStatus, FileStatusConstants.RECYCLED)
                .in(UserFileEntity::getUserFileId, deleteFilesAndChildren);
        this.update(updateWrapper);
    }

    /**
     * 查询所有文件及其子级（递归）
     */
    private void findAllFileList(List<Long> fileIdList, Long parentFileId, Long userId, Integer delFlag) {
        fileIdList.add(parentFileId);

        // 查询文件是否有子级
        LambdaQueryWrapper<UserFileEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFileEntity::getUserId, userId)
                .eq(UserFileEntity::getStatus, delFlag)
                .eq(UserFileEntity::getPid, parentFileId);
        List<UserFileEntity> files = fileMapper.selectList(wrapper);
        // 递归调用，查询所有文件和目录
        for (UserFileEntity file : files) {
            findAllFileList(fileIdList, file.getUserFileId(), userId, delFlag);
        }
    }
}
