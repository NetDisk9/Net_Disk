package com.net.file.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.net.api.client.AuthClient;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.util.SortUtils;
import com.net.common.vo.PageResultVO;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/file/share")
public class ShareController {
    @Resource
    ShareService shareService;
    @Resource
    RedisUtil redisUtil;
    @Resource
    FileService fileService;
    @Resource
    AuthClient authClient;

    @PostMapping("/create")
    public ResponseResult createShare(String time, String code, String userFileId) {
        if (!fileService.isFileExist(userFileId) || (code != null && code.length() != 4) ||
                (!Objects.equals(time, "1") && !Objects.equals(time, "7") && !Objects.equals(time, "30") && !Objects.equals(time, "-1"))) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        if (!authClient.isVIP() && Objects.equals(time, "-1")) {
            return ResponseResult.errorResult(ResultCodeEnum.UNAUTHORIZED);
        }
        UserFileEntity userFileEntity = fileService.getFile(Long.valueOf(userFileId), BaseContext.getCurrentId());
        return shareService.createShareLink(time, code, userFileId, userFileEntity.getFilePath(), userFileEntity.getFileName());
    }

    @DeleteMapping("/cancel")
    public ResponseResult deleteShare(String shareId) {
        return shareService.deleteShareLink(shareId);
    }

    @GetMapping("/info")
    public ResponseResult getShareInfoByLink(@Valid @NotBlank String link) {
        ShareInfoVO shareInfoVO = shareService.getShareInfoByLink(link);
        Integer redisCount = (Integer) redisUtil.get(RedisConstants.FILE_SHARE_COUNT + link);
        // 添加点击次数
        redisUtil.set(RedisConstants.FILE_SHARE_COUNT + link, redisCount == null ? 0 : redisCount + 1);
        shareInfoVO.setCount(redisCount == null ? 0 : redisCount + 1);
        // 是否已校验验证码
        Integer checked = (Integer) redisUtil.get(RedisConstants.FILE_SHARE_RES_KEY + link + CharPool.COLON + BaseContext.getCurrentIp());
        shareInfoVO.setCheck(checked == null ? 0 : 1);
        return ResponseResult.okResult(shareInfoVO);
    }

    @GetMapping("/code/check")
    public ResponseResult checkCode(@Valid @NotBlank String link, @Valid @NotBlank String code) {
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
        List<ShareEntity> records = pageInfo.getRecords();
        // 获取所有链接对应的 Redis 计数
        Map<String, Integer> redisCounts = records.parallelStream()
                .map(shareEntity -> RedisConstants.FILE_SHARE_COUNT + shareEntity.getLink())
                .distinct() // 确保只有唯一的链接
                .collect(Collectors.toMap(
                        linkKey -> linkKey,
                        linkKey -> (Integer) redisUtil.get(linkKey)
                ));
        // 更新每个记录的计数
        records.forEach(shareEntity -> {
            Integer redisCount = redisCounts.get(RedisConstants.FILE_SHARE_COUNT + shareEntity.getLink());
            shareEntity.setCount(redisCount);
        });
        // 转换成PageResultVO
        return PageResultVO.convertPageInfoToPageVO(pageInfo);
    }

    @GetMapping("/list")
    public ResponseResult listShareFile(Integer page, Integer pageSize, FileShareDTO fileShareDTO) {
        ShareEntity one = shareService.checkListShareFileParam(page, pageSize, fileShareDTO);
        boolean isRoot = fileShareDTO.getPid() == null;
        if (isRoot) {
            List<UserFileEntity> root = fileService.list(Wrappers.<UserFileEntity>lambdaQuery()
                    .eq(UserFileEntity::getUserFileId, one.getUserFileId())
                    .eq(UserFileEntity::getUserId, one.getUserId())
                    .eq(UserFileEntity::getStatus, FileStatusConstants.NORMAL)
            );
            List<FileVO> fileVOS = BeanUtil.copyToList(root, FileVO.class);
            return PageResultVO.convertListToPageVO(fileVOS, 1, 1);
        } else {
            return shareService.listShareFile(page, pageSize, fileShareDTO, one);
        }
    }

    @GetMapping("/list/path")
    public ResponseResult listShareFileByPath(Integer page, Integer pageSize, FileShareDTO fileShareDTO) {
        ShareEntity one = shareService.checkListShareFileParam(page, pageSize, fileShareDTO);
        boolean isRoot = fileShareDTO.getPath().equals("/");
        if (isRoot) {
            List<UserFileEntity> root = fileService.list(Wrappers.<UserFileEntity>lambdaQuery()
                    .eq(UserFileEntity::getFilePath, one.getFilePath())
                    .eq(UserFileEntity::getUserId, one.getUserId())
                    .eq(UserFileEntity::getStatus, FileStatusConstants.NORMAL)
            );
            List<FileVO> fileVOS = BeanUtil.copyToList(root, FileVO.class);
            return PageResultVO.convertListToPageVO(fileVOS, 1, 1);
        } else {
            return shareService.listShareFileByPath(page, pageSize, fileShareDTO, one);
        }
    }

}
