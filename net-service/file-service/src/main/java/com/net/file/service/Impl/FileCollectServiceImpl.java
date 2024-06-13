package com.net.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.exception.ParameterException;
import com.net.common.util.DateFormatUtil;
import com.net.file.constant.CollectStatusConstants;
import com.net.file.entity.FileCollectEntity;
import com.net.file.mapper.FIleCollectMapper;
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
    private void checkValid(FileCollectEntity collect){
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
