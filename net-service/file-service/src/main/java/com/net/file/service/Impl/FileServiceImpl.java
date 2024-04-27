package com.net.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.context.BaseContext;
import com.net.common.exception.AuthException;
import com.net.common.exception.ParameterException;
import com.net.file.constant.DirConstants;
import com.net.file.constant.FileOperationModeConstants;
import com.net.file.constant.FileStatusConstants;
import com.net.file.entity.UserFileEntity;
import com.net.file.support.UserFileTree;
import com.net.file.mapper.FileMapper;
import com.net.file.pojo.dto.FileMoveDTO;
import com.net.file.service.FileService;
import com.net.file.util.PathUtil;
import com.net.file.util.UsefulNameUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl extends ServiceImpl< FileMapper,UserFileEntity> implements FileService  {
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
        if(!userId.equals(userFile.getUserId())){
            throw new AuthException();
        }
        return userFile;
    }

    @Override
    public UserFileEntity getNormalFile(Long userFileId, Long userId) throws AuthException, ParameterException {
        UserFileEntity userFile=getFile(userFileId,userId);
        if(!FileStatusConstants.NORMAL.equals(userFile.getStatus())){
            throw new ParameterException();
        }
        return userFile;
    }

    @Override
    public UserFileEntity getFileWithoutCheck(Long userFileId) throws ParameterException{
        UserFileEntity userFile = getUserFileByUserFileId(userFileId);
        if(userFile==null){
            throw new ParameterException();
        }
        return userFile;
    }

    @Override
    public UserFileTree buildUserFileTree(FileMoveDTO fileMoveDTO, List<UserFileEntity> failCollector,Integer mode) throws AuthException, ParameterException {
        Long userId= BaseContext.getCurrentId();
        UserFileEntity parentFile=getNormalFile(fileMoveDTO.getPid(),userId);
        if(DirConstants.NOT_DIR.equals(parentFile.getIsDir())){
            throw new ParameterException();
        }
        List<UserFileEntity> userFileEntities=null;
        userFileEntities= Arrays.stream(fileMoveDTO.getUserFileId()).map(fileId -> {
            try {
                UserFileEntity userFile = getNormalFile(fileId, userId);
                userFile.setPid(parentFile.getUserFileId());
                return userFile;
            } catch (ParameterException e) {
                throw new RuntimeException(e);
            } catch (AuthException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        System.out.println(parentFile+" "+userFileEntities);
        if(!PathUtil.checkPath(parentFile,userFileEntities)){
            throw new ParameterException();
        }
        List<UserFileEntity> list=new ArrayList<>();
        for(var entity:userFileEntities){
            if(isExist(parentFile.getFilePath()+"/"+entity.getFileName())){
                if(FileOperationModeConstants.DEFAULT.equals(mode)){
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
        UserFileTree tree=new UserFileTree(new UserFileTree.UserFileTreeNode(parentFile));
        tree.buildTree(list);
//        tree.rebuildPathByRootPath();
//        tree.reAssignUserFileIdExceptRoot();
//        System.out.println(tree.collect());
        return tree;
    }
}
