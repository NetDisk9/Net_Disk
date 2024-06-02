package com.net.file.controller;

import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.util.SortUtils;
import com.net.common.vo.PageResultVO;
import com.net.file.constant.DirConstants;
import com.net.file.constant.FileStatusConstants;
import com.net.file.entity.ShareEntity;
import com.net.file.entity.UserFileEntity;
import com.net.file.pojo.dto.FileShareDTO;
import com.net.file.pojo.vo.FileVO;
import com.net.file.pojo.vo.ShareInfoVO;
import com.net.file.service.FileService;
import com.net.file.service.ShareService;
import com.net.redis.constant.RedisConstants;
import com.net.redis.utils.RedisUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;


@RestController
@RequestMapping("/file/share")
public class ShareController {
    @Resource
    ShareService shareService;
    @Resource
    RedisUtil redisUtil;
    @Resource
    FileService fileService;

    @GetMapping("/info")
    public ResponseResult getShareInfoByLink(@Valid @NotBlank String link) {
        ShareInfoVO shareInfoVO = shareService.getShareInfoByLink(link);
        Integer redisCount = (Integer) redisUtil.get(RedisConstants.FILE_SHARE_COUNT + link);
        if (redisCount == null) {
            redisUtil.set(RedisConstants.FILE_SHARE_COUNT + link, 1);
            shareInfoVO.setCount(1);
        } else {
            redisUtil.set(RedisConstants.FILE_SHARE_COUNT + link, redisCount + 1);
            shareInfoVO.setCount(redisCount + 1);
        }
        Integer checked = (Integer) redisUtil.get(RedisConstants.FILE_SHARE_RES_KEY + link + CharPool.COLON + BaseContext.getCurrentIp());
        shareInfoVO.setCheck(checked == null ? 0 : 1);
        return ResponseResult.okResult(shareInfoVO);
    }

    @GetMapping("/code/check")
    public ResponseResult getShareInfoByLink(@Valid @NotBlank String link, @Valid @NotBlank String code) {
        ShareEntity one = shareService.getOne(Wrappers.<ShareEntity>lambdaQuery().eq(ShareEntity::getLink, link));
        if (StrUtil.isBlank(one.getCode()) || !one.getCode().equals(code))
            return ResponseResult.errorResult(ResultCodeEnum.CODE_ERROR, "提取码错误");
        redisUtil.set(RedisConstants.FILE_SHARE_RES_KEY + link + CharPool.COLON + BaseContext.getCurrentIp(), 1, RedisConstants.FILE_SHARE_TTL);
        return ResponseResult.okResult(Boolean.TRUE);
    }

    @GetMapping("/record/list")
    public ResponseResult listShareRecord(Integer page, Integer pageSize, FileShareDTO fileShareDTO) {
        if (page == null || pageSize == null)
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR, "页码和数量不能为空");
        // 构造分页构造器
        Page<ShareEntity> pageInfo = new Page<>(page, pageSize);
        SortUtils.setOrderPage(pageInfo, fileShareDTO.getSortField(), fileShareDTO.getSortOrder());// 排序
        // 分页查询
        shareService.page(pageInfo, Wrappers.<ShareEntity>lambdaQuery().eq(ShareEntity::getUserId, BaseContext.getCurrentId()));
        // 转换成VO
        PageResultVO<ShareEntity> pageResultVO = new PageResultVO<>();
        pageResultVO.setList(pageInfo.getRecords());
        pageResultVO.setLen((int) pageInfo.getSize());
        pageResultVO.setTot((int) pageInfo.getTotal());
        return ResponseResult.okResult(pageResultVO);
    }

    @GetMapping("/list")
    public ResponseResult listShareFile(Integer page, Integer pageSize, FileShareDTO fileShareDTO) {
        // 参数校验
        if (page == null || pageSize == null)
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR, "页码和数量不能为空");
        ShareEntity one = shareService.getOne(Wrappers.<ShareEntity>lambdaQuery().eq(ShareEntity::getLink, fileShareDTO.getLink()));
        if (!shareService.checkShareValid(one))
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR, "分享链接失效");
        // 构造分页构造器
        Page<FileVO> pageInfo = new Page<>(page, pageSize);
        SortUtils.setOrderPage(pageInfo, fileShareDTO.getSortField(), fileShareDTO.getSortOrder()); // 排序
        // 分页查询
        pageInfo.setOptimizeCountSql(false);
        fileShareDTO.setUserId(one.getUserId()); // 分享人的文件
        shareService.listShareFile(pageInfo, fileShareDTO);
        // 转换成VO
        PageResultVO<FileVO> pageResultVO = new PageResultVO<>();
        pageResultVO.setList(pageInfo.getRecords());
        pageResultVO.setLen((int) pageInfo.getSize());
        pageResultVO.setTot((int) pageInfo.getTotal());
        return ResponseResult.okResult(pageResultVO);
    }

    @GetMapping("/list/path")
    public ResponseResult listFileByPath(Integer page, Integer pageSize, FileShareDTO fileShareDTO) {
        if (page == null || pageSize == null)
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR, "页码和数量不能为空");
        ShareEntity one = shareService.getOne(Wrappers.<ShareEntity>lambdaQuery().eq(ShareEntity::getLink, fileShareDTO.getLink()));
        if (!shareService.checkShareValid(one))
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR, "分享链接失效");
        boolean isRoot = fileShareDTO.getPath().equals("/");
        UserFileEntity parentFile = fileService.getOne(
                new LambdaQueryWrapper<UserFileEntity>()
                        .eq(isRoot,UserFileEntity::getUserFileId, one.getUserFileId())
                        .eq(!isRoot, UserFileEntity::getFilePath, one.getFilePath() +fileShareDTO.getPath())
                        .eq(UserFileEntity::getUserId, one.getUserId())
                        .eq(UserFileEntity::getStatus, FileStatusConstants.NORMAL));
        if (parentFile == null || DirConstants.NOT_DIR.equals(parentFile.getIsDir())) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR, "路径错误");
        }
        fileShareDTO.setPid(parentFile.getUserFileId());
        return listShareFile(page, pageSize, fileShareDTO);
    }
}
