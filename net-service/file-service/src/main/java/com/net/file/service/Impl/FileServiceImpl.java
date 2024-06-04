package com.net.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.context.BaseContext;
import com.net.common.exception.AuthException;
import com.net.common.exception.ParameterException;
import com.net.common.util.DateFormatUtil;
import com.net.common.util.LongIdUtil;
import com.net.file.constant.DirConstants;
import com.net.file.constant.FileOperationModeConstants;
import com.net.file.constant.FileStatusConstants;
import com.net.file.entity.UserFileEntity;
import com.net.file.factory.UserFileEntityFactory;
import com.net.file.pojo.dto.FileQueryDTO;
import com.net.file.support.UserFileTree;
import com.net.file.mapper.FileMapper;
import com.net.file.pojo.dto.FileMoveDTO;
import com.net.file.service.FileService;
import com.net.file.util.PathUtil;
import com.net.file.util.UsefulNameUtil;
import com.net.file.wrapper.LambdaFunctionWrapper;
import lombok.SneakyThrows;
import org.apache.catalina.User;
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
        return fileMapper.getUserFileByUserFileId(userFileId);
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
    public void insertDirFile(UserFileEntity userFile) {
        save(userFile);
    }

    @Override
    public void insertFile(UserFileEntity userFile) {
        //恢复路径中的文件夹
        restoreParent(userFile);
        //重名处理
        if(isExist(userFile.getFilePath(),userFile.getUserId())){
            List<UserFileEntity> temp = listUserFileByPidAndPath(userFile.getPid(),PathUtil.getPlainName(userFile.getFilePath()),FileStatusConstants.NORMAL, userFile.getUserId());
            UsefulNameUtil usefulNameUtil=new UsefulNameUtil(temp,userFile.getFileName());
            userFile.setFileName(usefulNameUtil.getNextName());
            userFile.setFilePath(PathUtil.replacePathName(userFile.getFilePath(),userFile.getFileName()));
        }
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
    public UserFileEntity getNormalUserFileByPath(String path,Long userId) {
        return getOne(new LambdaQueryWrapper<UserFileEntity>().eq(UserFileEntity::getFilePath,path).eq(UserFileEntity::getStatus,FileStatusConstants.NORMAL).eq(UserFileEntity::getUserId,userId));
    }

    @Override
    public boolean isExist(String path,Long userId) {
        return getNormalUserFileByPath(path,userId)!=null;
    }

    @Override
    public boolean isFileExist(String userFileId) {
        return getOne(new LambdaQueryWrapper<UserFileEntity>()
                        .eq(UserFileEntity::getUserFileId, userFileId)
                        .eq(UserFileEntity::getStatus,FileStatusConstants.NORMAL)) != null;
    }

    @Override
    public UserFileEntity getFile(Long userFileId, Long userId) throws ParameterException, AuthException {
        UserFileEntity userFile = getFileWithoutCheck(userFileId);
        if (!userId.equals(userFile.getUserId())) {
            throw new AuthException("没有查看该文件的权限");
        }
        return userFile;
    }

    @Override
    public UserFileEntity getNormalFile(Long userFileId, Long userId) throws AuthException, ParameterException {
        UserFileEntity userFile = getFile(userFileId, userId);
        if (!FileStatusConstants.NORMAL.equals(userFile.getStatus())) {
            throw new ParameterException("目的文件状态错误");
        }
        return userFile;
    }

    @Override
    public UserFileEntity getFileWithoutCheck(Long userFileId) throws ParameterException {
        UserFileEntity userFile = getUserFileByUserFileId(userFileId);
        if (userFile == null) {
            throw new ParameterException("不存在该文件");
        }
        return userFile;
    }
    @Override
    public UserFileEntity getFileIdByPath(String path,Long userId){
        UserFileEntity userFileEntity = fileMapper.getUserFileByPath(userId, FileStatusConstants.NORMAL, path);
        return userFileEntity;
    }

    @Override
    public UserFileTree buildUserFileTree(UserFileEntity parentFile,List<UserFileEntity> list)  {
        UserFileTree tree = new UserFileTree(new UserFileTree.UserFileTreeNode(parentFile));
        tree.buildTree(list);
        return tree;
    }

    @SneakyThrows
    @Override
    public void updateFileFoldStatus(List<Long> fileIds, Integer BEFORE_MODE, Integer AFTER_MODE) {
        // 查询待修改状态的文件或文件夹
        LambdaQueryWrapper<UserFileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFileEntity::getUserId, BaseContext.getCurrentId())
                .eq(UserFileEntity::getStatus, BEFORE_MODE)
                .in(UserFileEntity::getUserFileId, fileIds);
        List<UserFileEntity> updateFiles = fileMapper.selectList(queryWrapper);
        if (updateFiles.isEmpty()) {
            throw new ParameterException();
        }
        // 修改文件的子级
        for (UserFileEntity updateFile : updateFiles) {
            LambdaUpdateWrapper<UserFileEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(UserFileEntity::getRecycleTime, DateFormatUtil.format(LocalDateTime.now()))
                    .eq(UserFileEntity::getStatus, BEFORE_MODE)
                    .eq(UserFileEntity::getUserFileId, updateFile.getUserFileId())
                    .set(UserFileEntity::getStatus, AFTER_MODE);
            if (DirConstants.IS_DIR.equals(updateFile.getIsDir())) {
                // 匹配子文件的路径
                updateWrapper.likeRight(UserFileEntity::getFilePath, updateFile.getFilePath());
            } else {
                updateWrapper.eq(UserFileEntity::getUserFileId,updateFile.getUserFileId());
            }
            this.update(updateWrapper);
        }
    }
    @Override
    public void restoreParent(UserFileEntity file){
        UserFileEntity parent = doRestoreParent(file);
        file.setPid(parent==null?null:parent.getUserFileId());
    }

    @Override
    public IPage selectPageVO(Page<UserFileEntity> pageInfo, FileQueryDTO fileQueryDTO) {
        return fileMapper.selectPageVO(pageInfo,fileQueryDTO);
    }

    @Override
    public List<UserFileEntity> copyFile(FileMoveDTO fileMoveDTO, Integer mode) throws Throwable {
        List<UserFileEntity> failCollector = new ArrayList<>();
        Long userId = BaseContext.getCurrentId();
        // 判断传入的pid对应的文件是否为正常的文件夹
        UserFileEntity parentFile = (fileMoveDTO.getPid()!=null)?getNormalFile(fileMoveDTO.getPid(), userId):UserFileEntityFactory.createRootDirEntity(userId);
        if (DirConstants.NOT_DIR.equals(parentFile.getIsDir())) {
            throw new ParameterException("目的文件不是文件夹");
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
        // 移动根文件夹
        if (!PathUtil.checkPath(parentFile, userFileEntities)) {
            throw new ParameterException("目的文件夹不能是子文件夹");
        }
        List<UserFileEntity> list = collectFilesForMove(parentFile,userFileEntities,mode,failCollector,userId);
        UserFileTree tree = buildUserFileTree(parentFile,list);
        tree.rebuildPathByRootPath();
        tree.reAssignUserFileIdExceptRoot();
//        System.out.println(tree.collect());
        insertBatch(tree.collect());
        return failCollector;
    }

    /**
     * 转存
     * @param root
     * @param userFileEntities
     * @param userId
     */
    @Override
    public void saveFiles(UserFileEntity root, List<UserFileEntity> userFileEntities, Long userId) {
        userFileEntities.forEach(userFile -> userFile.setPid(null));
        List<UserFileEntity> list=collectFilesForMove(root,userFileEntities,FileOperationModeConstants.RENAME,new ArrayList<>(),userId);
        UserFileTree tree = buildUserFileTree(root,list);
        tree.reAssignUserFileIdExceptRoot();
        tree.resetUserId();
        tree.rebuildPathByRootPath();
        System.out.println(tree.collect());
        insertBatch(tree.collect());
    }

    private List<UserFileEntity> collectFilesForMove(UserFileEntity parentFile, List<UserFileEntity> userFileEntities, Integer mode, List<UserFileEntity> failCollector, Long userId){
        List<UserFileEntity> list = new ArrayList<>();
        for (var entity : userFileEntities) {
            if (isExist(parentFile.getFilePath() + "/" + entity.getFileName(),userId)) {// 存在同名文件
                if (FileOperationModeConstants.DEFAULT.equals(mode)) {// 跳过则返回重名的文件列表
                    failCollector.add(entity);
                    continue;
                }
                else{ // 生成新的文件名
                    List<UserFileEntity> temp = listUserFileByPidAndPath(parentFile.getUserFileId(),parentFile.getFilePath(),FileStatusConstants.NORMAL,userId);
                    UsefulNameUtil usefulNameUtil = new UsefulNameUtil(temp, entity.getFileName());
                    entity.setFileName(usefulNameUtil.getNextName());
                }
            }
            if(DirConstants.IS_DIR.equals(entity.getIsDir())){// 如果是文件夹，则拷贝子文件
                List<UserFileEntity> temp=listUserFileInDir(entity.getFilePath(),FileStatusConstants.NORMAL, entity.getUserId());
                list.addAll(temp);
            }
            list.add(entity);
        }
        return list;
    }
    private UserFileEntity doRestoreParent(UserFileEntity file){
        if(file==null){
            return null;
        }
        String parentPath = file.getParentPath();
        if(parentPath==null){
            return null;
        }
        UserFileEntity parentFile = fileMapper.getUserFileByPath(file.getUserId(), file.getStatus(), parentPath);
        //如果父文件夹不存在，创建父文件夹
        if(parentFile==null){
            parentFile = UserFileEntityFactory.createDirEntity(parentPath, file.getStatus(), file.getUserId());
            parentFile.setUserFileId(LongIdUtil.createLongId(parentFile));
            //检查父文件夹的父文件夹是否存在
            UserFileEntity temp = doRestoreParent(parentFile);
            parentFile.setPid(temp==null?null:temp.getUserFileId());
            save(parentFile);
        }
        return parentFile;
    }

}
