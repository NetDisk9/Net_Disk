package com.net.file.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.AuthException;
import com.net.common.exception.ParameterException;
import com.net.common.vo.PageResultVO;
import com.net.file.constant.FileStatusConstants;
import com.net.file.entity.UserFileEntity;
import com.net.file.support.UserFileTree;
import com.net.file.pojo.dto.FileMoveDTO;
import com.net.file.service.FileService;
import com.net.file.util.PathUtil;
import com.net.file.util.UsefulNameUtil;
import lombok.SneakyThrows;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
        List<UserFileEntity> list = new ArrayList<>();
        UserFileTree tree = fileService.buildUserFileTree(fileMoveDTO, list, mode);
        tree.rebuildPathByRootPath();
        tree.reAssignUserFileIdExceptRoot();
//        System.out.println(tree.collect());
        fileService.insertBatch(tree.collect());
        return ResponseResult.okResult(list);
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
        return ResponseResult.okResult(userFile);
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
        userFile.setFileName(name);
        userFile.setFilePath(PathUtil.replaceLastPath(userFile.getFilePath(), name));
        System.out.println(userFile.getFilePath());
        List<UserFileEntity> userFileEntities = fileService.listUserFileByPidAndPath(userFile.getPid(), userFile.getFilePath(), FileStatusConstants.NORMAL,userId);
        System.out.println(userFileEntities);
        UsefulNameUtil usefulNameUtil = new UsefulNameUtil(userFileEntities, userFile.getFileName());
        if (!usefulNameUtil.isUseful(userFile.getFileName())) {
            return ResponseResult.errorResult(ResultCodeEnum.FILE_NAME_REPEAT.getCode(), "文件名重复", usefulNameUtil.getNextName());
        }
        fileService.updateFile(userFile);
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
        PageResultVO<UserFileEntity> pageResultVO = new PageResultVO<>();
        pageResultVO.setList(pageInfo.getRecords());
        pageResultVO.setLen((int) pageInfo.getSize());
        pageResultVO.setTot((int) pageInfo.getTotal());
        return ResponseResult.okResult(pageResultVO);
    }

    @DeleteMapping("/delete")
    public ResponseResult removeFile2Recycle(@Valid @NotEmpty @RequestParam("fileIds") List<Long> fileIds) {
        fileService.removeFile2Recycle(fileIds);
        return ResponseResult.okResult();
    }
}
