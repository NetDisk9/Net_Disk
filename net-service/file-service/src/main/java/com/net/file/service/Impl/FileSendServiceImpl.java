package com.net.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.context.BaseContext;
import com.net.common.exception.ParameterException;
import com.net.common.util.DateFormatUtil;
import com.net.file.constant.CollectStatusConstants;
import com.net.file.constant.FileSendStatus;
import com.net.file.constant.FileStatusConstants;
import com.net.file.entity.FileCollectEntity;
import com.net.file.entity.FileSendEntity;
import com.net.file.entity.UserFileEntity;
import com.net.file.factory.UserFileEntityFactory;
import com.net.file.mapper.FileMapper;
import com.net.file.mapper.FileSendMapper;
import com.net.file.pojo.dto.FileCollectQueryDTO;
import com.net.file.pojo.vo.FileSendPageVO;
import com.net.file.service.FileCollectService;
import com.net.file.service.FileSendService;
import com.net.file.service.FileService;
import com.net.file.support.TaskList;
import com.net.redis.lock.RedissonLockWrapper;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class FileSendServiceImpl extends ServiceImpl<FileSendMapper, FileSendEntity> implements FileSendService {
    @Resource
    FileCollectService collectService;
    @Resource
    FileMapper fileMapper;
    @Resource
    RedissonClient redissonClient;
    @Resource
    TaskList taskList;
    @Resource
    FileService fileService;
    private static final String SHARE_FOLDER_NAME = "来自：收集文件";

    @Override
    public void sendFile(String link, UserFileEntity userFile, String signer) throws InterruptedException {
        RedissonLockWrapper redissonLockWrapper = new RedissonLockWrapper(redissonClient, link);
        boolean result = redissonLockWrapper.lock();
        if (!result) {
            throw new RuntimeException();
        }
        try {
            FileCollectEntity collect = collectService.getCollectByLinkWithCheck(link);
            if (collect.getMaxNum() < collect.getCurNum() + 1) {
                throw new ParameterException("发送人数已达上限");
            }
//            if (getSendEntityByUserIdAndCollectId(userFile.getUserId(), collect.getCollectId()) != null) {
//                throw new ParameterException("不能重复发送");
//            }

            FileSendEntity fileSend = FileSendEntity.builder()
                    .userFileId(userFile.getUserFileId())
                    .sendTime(DateFormatUtil.format(LocalDateTime.now()))
                    .userId(userFile.getUserId())
                    .status(collect.getStatus())
                    .signer(signer)
                    .collectId(collect.getCollectId())
                    .build();
            if (Objects.equals(collect.getStatus(), CollectStatusConstants.AUTO)) {
                taskList.addTask(() -> saveFile(List.of(userFile), collect));
            }
            save(fileSend);
            collect.setCurNum(collect.getCurNum() + 1);
            collectService.updateById(collect);
        } finally {
            redissonLockWrapper.unlock();
        }
    }

    private FileSendEntity getSendEntityByUserIdAndCollectId(Long userId, Long collectId) {
        LambdaQueryWrapper<FileSendEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileSendEntity::getUserId, userId)
                .eq(FileSendEntity::getCollectId, collectId);
        FileSendEntity sendEntity = getOne(queryWrapper);
        return sendEntity;
    }

    @Override
    public void saveFile(List<UserFileEntity> list, FileCollectEntity collect) {
        String sharePath = createPath(collect);
        UserFileEntity shareFolder = fileService.getNormalUserFileByPath(sharePath, collect.getUserId());
        if (shareFolder == null) {
            shareFolder = UserFileEntityFactory.createDirEntity(sharePath, FileStatusConstants.NORMAL, collect.getUserId());
            fileService.restoreParent(shareFolder);
            fileService.save(shareFolder);
        }
        fileService.saveFiles(shareFolder, list, collect.getUserId());
    }

    @Override
    public void saveFile(Long sendId) {
        FileSendEntity sendEntity = getById(sendId);
        if (sendEntity == null) {
            throw new ParameterException("目标文件不存在");
        }
        if (Objects.equals(sendEntity.getStatus(), FileSendStatus.DONE)) {
            throw new ParameterException("目标文件已转存");
        }
        UserFileEntity file = fileService.getNormalFile(sendEntity.getUserFileId(), sendEntity.getUserId());
        FileCollectEntity fileCollectEntity = collectService.getCollectWithCheck(sendEntity.getCollectId());
        if (!Objects.equals(fileCollectEntity.getUserId(), BaseContext.getCurrentId())) {
            throw new ParameterException();
        }
        taskList.addTask(() -> saveFile(List.of(file), fileCollectEntity));
        sendEntity.setStatus(FileSendStatus.DONE);
        updateById(sendEntity);
    }

    @Override
    public IPage selectPageVO(Page<FileSendPageVO> pageInfo, FileCollectQueryDTO collectQueryDTO) {
        return fileMapper.selectSendPageVO(pageInfo,collectQueryDTO);
    }

    private String createPath(FileCollectEntity collect) {
        return SHARE_FOLDER_NAME + "/" + collect.getTitle();
    }
}
