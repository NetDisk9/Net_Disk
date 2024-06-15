package com.net.file.service.Impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.exception.ParameterException;
import com.net.common.util.DateFormatUtil;
import com.net.file.constant.CollectStatusConstants;
import com.net.file.entity.FileCollectEntity;
import com.net.file.mapper.FIleCollectMapper;
import com.net.file.pojo.vo.FileCollectCreateVO;
import com.net.file.pojo.vo.FileCollectGetVO;
import com.net.file.service.FileCollectService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
@Service
public class FileCollectServiceImpl  extends ServiceImpl<FIleCollectMapper, FileCollectEntity> implements FileCollectService {

    @Override
    public FileCollectEntity getCollectWithCheck(Long collectId) {
        FileCollectEntity collect = getById(collectId);
        checkValid(collect);
        return collect;
    }

    @Override
    public FileCollectEntity getCollectByLinkWithCheck(String link) {
        LambdaQueryWrapper<FileCollectEntity> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(FileCollectEntity::getLink,link);
        FileCollectEntity collect = getOne(queryWrapper);
        checkValid(collect);
        return collect;
    }

    @Override
    public ResponseResult getCollectByLink(String link) {
        LambdaQueryWrapper<FileCollectEntity> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(FileCollectEntity::getLink,link);
        FileCollectEntity collect = getOne(queryWrapper);
        checkValid(collect);
        FileCollectGetVO fileCollectGetVO = new FileCollectGetVO();
        fileCollectGetVO.setTitle(collect.getTitle());
        fileCollectGetVO.setEndTime(collect.getEndTime());
        fileCollectGetVO.setMaxNum(String.valueOf(collect.getMaxNum()));
        fileCollectGetVO.setSigner(collect.getSigner());
        return ResponseResult.okResult(fileCollectGetVO);
    }

    @Override
    public ResponseResult createCollect(String title, Integer duration, Integer maxNum, String signer, Integer autoCollect) {
        String begTime = DateFormatUtil.getNow();
        String endTime;
        String link = RandomUtil.randomString(20);
        if (Objects.equals(duration, -1)) {
            endTime = DateFormatUtil.addDays(begTime, 10000000);
        } else {
            endTime = DateFormatUtil.addDays(begTime, duration);
        }
        FileCollectEntity fileCollectEntity = FileCollectEntity.builder()
                .title(title)
                .userId(BaseContext.getCurrentId())
                .begTime(begTime)
                .endTime(endTime)
                .maxNum(maxNum)
                .signer(signer)
                .status(autoCollect)
                .link(link)
                .curNum(0)
                .build();
        this.save(fileCollectEntity);
        FileCollectCreateVO fileCollectCreateVO = new FileCollectCreateVO();
        fileCollectCreateVO.setEndTime(endTime);
        fileCollectCreateVO.setLink(link);
        return ResponseResult.okResult(fileCollectCreateVO);
    }

    @Override
    public ResponseResult deleteCollectByCollectId(Long collectId) {
        LambdaQueryWrapper<FileCollectEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileCollectEntity::getCollectId, collectId);
        FileCollectEntity collect = getOne(queryWrapper);
        checkValid(collect);
        LambdaUpdateWrapper<FileCollectEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FileCollectEntity::getCollectId, collectId)
                .set(FileCollectEntity::getStatus, CollectStatusConstants.DELETED);
        this.update(updateWrapper);
        return ResponseResult.okResult();
    }

    private void checkValid(FileCollectEntity collect){
        if (collect == null) {
            throw new ParameterException("此收集不存在");
        }
        LocalDateTime endTime= DateFormatUtil.string2LocalDateTime(collect.getEndTime());
        if(Objects.equals(collect.getStatus(), CollectStatusConstants.DELETED)||Objects.equals(collect.getStatus(),CollectStatusConstants.EXPIRED)){
            throw new ParameterException("收集已无效");
        }
        if(!LocalDateTime.now().isBefore(endTime)){
            collect.setStatus(CollectStatusConstants.EXPIRED);
            updateById(collect);
            throw new ParameterException("收集已过期");
        }
    }
}
