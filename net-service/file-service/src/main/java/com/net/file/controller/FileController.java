package com.net.file.controller;

import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.AuthException;
import com.net.common.exception.ParameterException;
import com.net.file.constant.FileStatusConstants;
import com.net.file.entity.UserFileEntity;
import com.net.file.support.UserFileTree;
import com.net.file.pojo.dto.FileMoveDTO;
import com.net.file.service.FileService;
import com.net.file.util.PathUtil;
import com.net.file.util.UsefulNameUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
@Validated
public class FileController {
    @Resource
    FileService fileService;
    @PostMapping("/copy")
    public ResponseResult copyFile(@Valid @RequestBody FileMoveDTO fileMoveDTO, @Valid @NotNull Integer mode) throws AuthException, ParameterException {
        if(fileMoveDTO.getUserFileId().length==0){
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        List<UserFileEntity> list=new ArrayList<>();
        UserFileTree tree = fileService.buildUserFileTree(fileMoveDTO,list,mode);
        tree.rebuildPathByRootPath();
        tree.reAssignUserFileIdExceptRoot();
//        System.out.println(tree.collect());
        fileService.insertBatch(tree.collect());
        return ResponseResult.okResult(list);
    }
    @PostMapping("/dir/create")
    public ResponseResult insertDir(String pid,
                                    @Valid @NotBlank String name) throws Exception {
        Long userId=BaseContext.getCurrentId();
        Long longPid=null;
        UserFileEntity parentFile=null;
        try{
            if(pid!=null){
                longPid=Long.parseLong(pid);
                parentFile = fileService.getNormalFile(longPid,userId);
            }
        }catch (ParameterException e){
            throw e;
        } catch (AuthException e) {
            throw e;
        }
        catch (Exception e){
            throw new ParameterException();
        }
        UserFileEntity userFile=UserFileEntity.UserFileEntityFactory.createDirEntity(parentFile,name,userId);
        System.out.println(userFile.getFilePath());
        List<UserFileEntity> userFileEntities = fileService.listUserFileByPidAndPath(userFile.getPid(), userFile.getFilePath(),FileStatusConstants.NORMAL,userId);
        System.out.println(userFileEntities.size());
        UsefulNameUtil usefulNameUtil = new UsefulNameUtil(userFileEntities, userFile.getFileName());
        if(!usefulNameUtil.isUseful(userFile.getFileName())){
            return ResponseResult.errorResult(ResultCodeEnum.FILE_NAME_REPEAT.getCode(),"文件名重复",usefulNameUtil.getNextName());
        }
        fileService.insertFile(userFile);
        return ResponseResult.okResult(userFile);
    }
    @PutMapping("/rename")
    public ResponseResult updateFileName(@Valid @NotBlank String userFileId,
                                         @Valid @NotBlank String name) throws ParameterException, AuthException {
        Long userId=BaseContext.getCurrentId();

        UserFileEntity userFile =null;
        try {
            userFile=fileService.getNormalFile(Long.parseLong(userFileId), userId);
        } catch (ParameterException e){
            throw e;
        } catch (AuthException e) {
            throw e;
        }
        catch (Exception e){
            throw new ParameterException();
        }
        if(userFile.getFileName().equals(name)){
            return ResponseResult.okResult();
        }
        userFile.setFileName(name);
        userFile.setFilePath(PathUtil.replaceLastPath(userFile.getFilePath(),name));
        System.out.println(userFile.getFilePath());
        List<UserFileEntity> userFileEntities = fileService.listUserFileByPidAndPath(userFile.getPid(), userFile.getFilePath(),FileStatusConstants.NORMAL,userId);
        System.out.println(userFileEntities);
        UsefulNameUtil usefulNameUtil = new UsefulNameUtil(userFileEntities, userFile.getFileName());
        if(!usefulNameUtil.isUseful(userFile.getFileName())){
            return ResponseResult.errorResult(ResultCodeEnum.FILE_NAME_REPEAT.getCode(),"文件名重复",usefulNameUtil.getNextName());
        }
        fileService.updateFile(userFile);
        return ResponseResult.okResult();
    }



}
