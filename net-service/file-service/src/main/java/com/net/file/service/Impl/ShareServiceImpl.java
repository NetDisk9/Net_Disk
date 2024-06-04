package com.net.file.service.Impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import cn.hutool.core.text.CharPool;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.CustomException;
import com.net.common.exception.ParameterException;
import com.net.common.util.DateFormatUtil;
import com.net.file.constant.FileStatusConstants;
import com.net.common.util.SortUtils;
import com.net.common.vo.PageResultVO;
import com.net.file.constant.DirConstants;
import com.net.file.constant.FileStatusConstants;
import com.net.file.constant.ShareStatusConstants;
import com.net.file.entity.ShareEntity;
import com.net.file.entity.UserFileEntity;
import com.net.file.mapper.ShareMapper;
import com.net.file.pojo.dto.FileShareDTO;
import com.net.file.pojo.vo.FileVO;
import com.net.file.pojo.vo.ShareInfoVO;
import com.net.file.pojo.vo.ShareLinkVO;
import com.net.file.service.FileService;
import com.net.file.service.ShareService;
import com.net.redis.constant.RedisConstants;
import com.net.redis.utils.RedisUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.List;

@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, ShareEntity> implements ShareService {
    @Resource
    ShareMapper shareMapper;
    @Resource
    FileService fileService;
    @Resource
    RedisUtil redisUtil;

    @Override
    public ShareEntity getShareEntityWithCheck(String link) {
        LambdaQueryWrapper<ShareEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShareEntity::getLink, link);
        ShareEntity shareEntity = getOne(queryWrapper);
        if (!checkShareValid(shareEntity)) {
            throw new ParameterException("分享链接失效");
        }
        return shareEntity;
    }

    @Override
    public boolean checkShareValid(ShareEntity shareEntity) {
        if (!ShareStatusConstants.VALID.equals(shareEntity.getStatus())) {
            return false;
        }
        LocalDateTime endDateTime = DateFormatUtil.string2LocalDateTime(shareEntity.getEndTime());
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(endDateTime);
    }

    @Override
    public ShareInfoVO getShareInfoByLink(String link) {
        return shareMapper.getShareInfoByLink(link);
    }

    @Override
    public IPage listShareFilePage(Page<FileVO> pageInfo, @Param("param") FileShareDTO fileShareDTO) {
        return shareMapper.listShareFile(pageInfo, fileShareDTO);
    }


    @Override
    public ResponseResult createShareLink(String time, String code, String userFileId, String filePath, String fileName) {
        String begTime = DateFormatUtil.getNow();
        String endTime;
        if (Objects.equals(time, "-1")) {
            endTime = DateFormatUtil.addDays(begTime, 10000000);
        } else {
            endTime = DateFormatUtil.addDays(begTime, Integer.parseInt(time));
        }
        if (code == null) {
            code = RandomUtil.randomNumbers(4);
        }
        String link = RandomUtil.randomString(20);
        LambdaQueryWrapper<ShareEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShareEntity::getUserFileId, userFileId);
        ShareEntity _shareEntity = this.getOne(queryWrapper);
        if (_shareEntity != null) {
            LambdaUpdateWrapper<ShareEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ShareEntity::getUserFileId, userFileId)
                    .set(ShareEntity::getStatus, ShareStatusConstants.VALID)
                    .set(ShareEntity::getBegTime, begTime)
                    .set(ShareEntity::getEndTime, endTime)
                    .set(ShareEntity::getCode, code)
                    .set(ShareEntity::getLink, link)
                    .set(ShareEntity::getFilePath, filePath)
                    .set(ShareEntity::getFileName, fileName);
            this.update(updateWrapper);
        } else {
            ShareEntity shareEntity = ShareEntity.builder()
                    .userId(BaseContext.getCurrentId())
                    .userFileId(Long.valueOf(userFileId))
                    .begTime(begTime)
                    .endTime(endTime)
                    .status(ShareStatusConstants.VALID)
                    .code(code)
                    .link(link)
                    .filePath(filePath)
                    .fileName(fileName)
                    .build();
            this.save(shareEntity);
        }
        ShareLinkVO shareLinkVO = new ShareLinkVO();
        shareLinkVO.setLink(link);
        shareLinkVO.setCode(code);
        return ResponseResult.okResult(shareLinkVO);
    }

    @Override
    public ResponseResult deleteShareLink(String shareId) {
        LambdaQueryWrapper<ShareEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShareEntity::getShareId, shareId);
        if (this.getOne(queryWrapper) == null) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        LambdaUpdateWrapper<ShareEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShareEntity::getShareId, shareId)
                .set(ShareEntity::getStatus, ShareStatusConstants.INVALID);
        this.update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listShareFileByPath(Integer page, Integer pageSize, FileShareDTO fileShareDTO, ShareEntity one) {
        UserFileEntity parentFile = fileService.getOne(
                new LambdaQueryWrapper<UserFileEntity>()
                        .eq(UserFileEntity::getFilePath, fileShareDTO.getPath())
                        .eq(UserFileEntity::getUserId, one.getUserId())
                        .eq(UserFileEntity::getStatus, FileStatusConstants.NORMAL));
        if (parentFile == null || DirConstants.NOT_DIR.equals(parentFile.getIsDir())) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR, "路径错误");
        }
        fileShareDTO.setPid(parentFile.getUserFileId());
        return listShareFile(page, pageSize, fileShareDTO, one);
    }

    @Override
    public ResponseResult listShareFile(Integer page, Integer pageSize, FileShareDTO fileShareDTO, ShareEntity one) {
        // 构造分页构造器
        Page<FileVO> pageInfo = new Page<>(page, pageSize);
        SortUtils.setOrderPage(pageInfo, fileShareDTO.getSortField(), fileShareDTO.getSortOrder()); // 排序
        // 分页查询
        pageInfo.setOptimizeCountSql(false);
        fileShareDTO.setUserId(one.getUserId()); // 分享人的文件
        this.listShareFilePage(pageInfo, fileShareDTO);
        // 转换成VO
        List<FileVO> fileVOS = pageInfo.getRecords();
        int size = (int) pageInfo.getSize();
        int total = (int) pageInfo.getTotal();
        return convertToPageVO(fileVOS, size, total);
    }

    @Override
    public ResponseResult convertToPageVO(List<FileVO> fileVOS, int size, int total) {
        PageResultVO<FileVO> pageResultVO = new PageResultVO<>();
        pageResultVO.setList(fileVOS);
        pageResultVO.setLen(size);
        pageResultVO.setTot(total);
        return ResponseResult.okResult(pageResultVO);
    }

    @Override
    public ShareEntity checkListShareFileParam(Integer page, Integer pageSize, FileShareDTO fileShareDTO) {
        // 参数校验
        if (page == null || pageSize == null) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR, "页码和数量不能为空");
        }
        // 是否已校验提取码
        checkHasValid(fileShareDTO.getLink());
        // 校验并返回
        return this.getShareEntityWithCheck(fileShareDTO.getLink());
    }

    // 是否已校验提取码
    @Override
    public void checkHasValid(String link) {
        Integer checked = (Integer) redisUtil.get(RedisConstants.FILE_SHARE_RES_KEY + link + CharPool.COLON + BaseContext.getCurrentIp());
        if (checked == null) throw new CustomException(ResultCodeEnum.PARAM_ERROR, "请先校验提取码");
    }
}
