package com.net.file.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.AuthException;
import com.net.common.exception.ParameterException;
import com.net.common.vo.PageResultVO;
import com.net.file.constant.DirConstants;
import com.net.file.constant.FileOperationModeConstants;
import com.net.file.constant.FileStatusConstants;
import com.net.file.entity.UserFileEntity;
import com.net.file.pojo.vo.FileVO;
import com.net.file.service.Impl.FileServiceImpl;
import com.net.file.support.UserFileTree;
import com.net.file.pojo.dto.FileMoveDTO;
import com.net.file.service.FileService;
import com.net.file.util.PathUtil;
import com.net.file.util.UsefulNameUtil;
import com.net.file.wrapper.LambdaFunctionWrapper;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
@Validated
public class FileController {
    @Resource
    FileService fileService;

    @PostMapping("/copy")
    public ResponseResult copyFile(@Valid @RequestBody FileMoveDTO fileMoveDTO, @Valid @NotNull Integer mode) throws Throwable {
        if (fileMoveDTO.getUserFileId().length == 0) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        List<UserFileEntity> failCollector = fileService.copyFile(fileMoveDTO, mode);
        List<FileVO> fileVOS = BeanUtil.copyToList(failCollector, FileVO.class);
        return ResponseResult.okResult(fileVOS);
    }

    @PutMapping("/move")
    public ResponseResult moveFile(@Valid @RequestBody FileMoveDTO fileMoveDTO, @Valid @NotNull Integer mode) throws Throwable {
        ResponseResult result = copyFile(fileMoveDTO, mode);
        if (result.getCode() != 200) {
            return result;
        }
        fileService.updateFileFoldStatus(List.of(fileMoveDTO.getUserFileId()), FileStatusConstants.NORMAL, FileStatusConstants.DELETED);
        List<?> dataList = (List<?>) result.getData();
        List<UserFileEntity> list = new ArrayList<>();
        for (Object item : dataList) {
            if (item instanceof UserFileEntity) {
                list.add((UserFileEntity) item);
            }
        }
        List<Long> userFileIdList = new ArrayList<>();
        for (UserFileEntity entity : list) {
            userFileIdList.add(entity.getUserFileId());
        }
        if (!list.isEmpty()) {
            fileService.updateFileFoldStatus(userFileIdList, FileStatusConstants.DELETED, FileStatusConstants.NORMAL);
        }
        List<FileVO> fileVOS = BeanUtil.copyToList(list, FileVO.class);
        return ResponseResult.okResult(fileVOS);
    }

    @GetMapping("/info")
    public ResponseResult showInfo(@Valid @NotBlank String userFileId) {
        UserFileEntity userFile = fileService.getFile(Long.parseLong(userFileId), BaseContext.getCurrentId());
        FileVO fileVO=new FileVO();
        BeanUtils.copyProperties(userFile,fileVO);
        return ResponseResult.okResult(fileVO);
    }

    @PostMapping("/dir/create")
    public ResponseResult insertDir(String pid,
                                    @Valid @NotBlank String name) throws Exception {
        Long userId = BaseContext.getCurrentId();
        Long longPid;
        UserFileEntity parentFile = null;
        try {
            if (pid != null) {
                longPid = Long.parseLong(pid);
                parentFile = fileService.getNormalFile(longPid, userId);
            }
        } catch (ParameterException | AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new ParameterException();
        }
        UserFileEntity userFile = UserFileEntity.UserFileEntityFactory.createDirEntity(parentFile, name, userId);
        List<UserFileEntity> userFileEntities = fileService.listUserFileByPidAndPath(userFile.getPid(), userFile.getFilePath(), FileStatusConstants.NORMAL,userId);
        UsefulNameUtil usefulNameUtil = new UsefulNameUtil(userFileEntities, userFile.getFileName());
        if (!usefulNameUtil.isUseful(userFile.getFileName())) {
            return ResponseResult.errorResult(ResultCodeEnum.FILE_NAME_REPEAT.getCode(), "文件名重复", usefulNameUtil.getNextName());
        }
        fileService.insertFile(userFile);
        FileVO fileVO=new FileVO();
        BeanUtils.copyProperties(userFile,fileVO);
        return ResponseResult.okResult(fileVO);
    }

    @PutMapping("/rename")
    public ResponseResult updateFileName(@Valid @NotBlank String userFileId,
                                         @Valid @NotBlank String name) throws ParameterException, AuthException {
        Long userId = BaseContext.getCurrentId();

        UserFileEntity userFile;
        try {
            userFile = fileService.getNormalFile(Long.parseLong(userFileId), userId);
        } catch (ParameterException | AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new ParameterException();
        }
        if (userFile.getFileName().equals(name)) {
            return ResponseResult.okResult();
        }
        String oldPath=userFile.getFilePath();
        userFile.setFileName(name);
        userFile.setFilePath(PathUtil.replaceLastPath(userFile.getFilePath(), name));
        System.out.println(userFile.getFilePath());
        List<UserFileEntity> userFileEntities = fileService.listUserFileByPidAndPath(userFile.getPid(), userFile.getFilePath(), FileStatusConstants.NORMAL,userId);
        System.out.println(userFileEntities);
        UsefulNameUtil usefulNameUtil = new UsefulNameUtil(userFileEntities, userFile.getFileName());
        if (!usefulNameUtil.isUseful(userFile.getFileName())) {
            return ResponseResult.errorResult(ResultCodeEnum.FILE_NAME_REPEAT.getCode(), "文件名重复", usefulNameUtil.getNextName());
        }
        if(DirConstants.IS_DIR.equals(userFile.getIsDir())){
            List<UserFileEntity> list = fileService.listUserFileInDir(oldPath, FileStatusConstants.NORMAL, userId);
            UserFileTree tree = fileService.buildUserFileTree(userFile, list);
            tree.rebuildPathByRootPath();
            List<UserFileEntity> collect = tree.collect();
            collect.add(userFile);
            fileService.updateBatchById(collect);
        }
        else{
            fileService.updateById(userFile);
        }
        return ResponseResult.okResult();
    }

    @GetMapping("/list")
    public ResponseResult listFile(String pid, Integer page, Integer pageSize) {
        if (page == null || pageSize == null) return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        //构造分页构造器
        Page<UserFileEntity> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<UserFileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNull(StrUtil.isBlank(pid), UserFileEntity::getPid)
                .eq(StrUtil.isNotBlank(pid), UserFileEntity::getPid, pid)
                .eq(UserFileEntity::getUserId, BaseContext.getCurrentId())
                .eq(UserFileEntity::getStatus, FileStatusConstants.NORMAL);
        //分页查询
        fileService.page(pageInfo, queryWrapper);
        PageResultVO<FileVO> pageResultVO = new PageResultVO<>();
        List<FileVO> fileVOS = BeanUtil.copyToList(pageInfo.getRecords(), FileVO.class);
        pageResultVO.setList(fileVOS);
        pageResultVO.setLen((int) pageInfo.getSize());
        pageResultVO.setTot((int) pageInfo.getTotal());
        return ResponseResult.okResult(pageResultVO);
    }
    @GetMapping("/list/path")
    public ResponseResult listFileByPath(String path, Integer page, Integer pageSize) {
        if (page == null || pageSize == null) return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        Long userId=BaseContext.getCurrentId();
        Long pid=null;
        if(!path.equals("/")){
            UserFileEntity parentFile = fileService.getOne(
                    new LambdaQueryWrapper<UserFileEntity>()
                            .eq(UserFileEntity::getFilePath, path)
                            .eq(UserFileEntity::getUserId, userId)
                            .eq(UserFileEntity::getStatus, FileStatusConstants.NORMAL));
            if(parentFile==null|| DirConstants.NOT_DIR.equals(parentFile.getIsDir())){
                return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
            }
            pid=parentFile.getUserFileId();
        }

        return listFile(pid.toString(),page,pageSize);
    }
    @GetMapping("/get/path")
    public ResponseResult getFileIdByPath(String path){
        Long userId=BaseContext.getCurrentId();
        UserFileEntity userFileEntity = fileService.getFileIdByPath(path, userId);
        FileVO fileVO=new FileVO();
        BeanUtils.copyProperties(userFileEntity,fileVO);
        return ResponseResult.okResult(fileVO);
    }

    @DeleteMapping("/delete")
    public ResponseResult removeFile2Recycle(@Valid @NotEmpty @RequestParam("fileIds") List<Long> fileIds) {
        fileService.updateFileFoldStatus(fileIds, FileStatusConstants.NORMAL, FileStatusConstants.RECYCLED);
        return ResponseResult.okResult();
    }
}
