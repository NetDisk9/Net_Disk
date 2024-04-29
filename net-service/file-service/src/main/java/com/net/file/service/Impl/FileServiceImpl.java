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
import com.net.file.wrapper.LambdaFunctionWrapper;
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
    public List<UserFileEntity> listUserFileByPidAndPath(Long pid, String path, Integer status, Long userId){
        return fileMapper.listUserFileByPidAndPath(pid,path,status,userId);
    }
    @Override
    public List<UserFileEntity> listUserFileByPidAndPathInDir(Long pid, String path, Integer status, Long userId){
        path+="/";
        return fileMapper.listUserFileByPidAndPath(pid,path,status,userId);
    }
    @Override
    public List<UserFileEntity> listUserFileInDir(String path, Integer status, Long userId) {
        path+="/";
        return list(new LambdaQueryWrapper<UserFileEntity>().likeRight(UserFileEntity::getFilePath,path).eq(UserFileEntity::getStatus,status).eq(UserFileEntity::getUserId,userId));
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
    public void insertBatch(List<UserFileEntity> list) {
        saveBatch(list);
    }

    @Override
    public UserFileEntity getNormalUserFileByPath(String path) {
        return getOne(new LambdaQueryWrapper<UserFileEntity>().eq(UserFileEntity::getFilePath,path).eq(UserFileEntity::getStatus,FileStatusConstants.NORMAL));
    }

    @Override
    public boolean isExist(String path) {
        return getNormalUserFileByPath(path)!=null;
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
    public UserFileTree buildUserFileTree(FileMoveDTO fileMoveDTO, List<UserFileEntity> failCollector, Integer mode) throws Throwable {
        Long userId = BaseContext.getCurrentId();
        // 判断传入的pid对应的文件是否为正常的文件夹
        UserFileEntity parentFile = getNormalFile(fileMoveDTO.getPid(), userId);
        if (DirConstants.NOT_DIR.equals(parentFile.getIsDir())) {
            throw new ParameterException();
        }
        List<UserFileEntity> userFileEntities;
        // 移动文件，修改文件的所属的文件夹
        try{
            userFileEntities = Arrays.stream(fileMoveDTO.getUserFileId()).map(
                LambdaFunctionWrapper.wrap(fileId -> {
                    UserFileEntity userFile = getNormalFile(fileId, userId);
                    userFile.setPid(parentFile.getUserFileId());
                    return userFile;
                })
            ).collect(Collectors.toList());
        }catch (Exception e){
            e.printStackTrace();
            throw e.getCause();
        }

        System.out.println(parentFile+" "+userFileEntities);
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
                }
                else{
                    List<UserFileEntity> temp = listUserFileByPidAndPath(parentFile.getUserFileId(),parentFile.getFilePath(),FileStatusConstants.NORMAL,userId);
                    UsefulNameUtil usefulNameUtil = new UsefulNameUtil(temp, entity.getFileName());
                    entity.setFileName(usefulNameUtil.getNextName());
                }
            }
            if(DirConstants.IS_DIR.equals(entity.getIsDir())){
                List<UserFileEntity> temp=listUserFileInDir(entity.getFilePath(),FileStatusConstants.NORMAL,userId);
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
            // 删除文件的子级
            for (UserFileEntity deleteFile : deleteFiles) {
                LambdaUpdateWrapper<UserFileEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(UserFileEntity::getRecycleTime, DateFormatUtil.format(LocalDateTime.now()))
                        .eq(UserFileEntity::getStatus, FileStatusConstants.NORMAL)
                        .set(UserFileEntity::getStatus, FileStatusConstants.RECYCLED);
                if (DirConstants.IS_DIR.equals(deleteFile.getIsDir())) {
                    // 匹配子文件的路径
                    updateWrapper.likeRight(UserFileEntity::getFilePath, deleteFile.getFilePath());
                } else {
                    updateWrapper.eq(UserFileEntity::getUserFileId,deleteFile.getUserFileId());
                }
                this.update(updateWrapper);
            }
        }

}
