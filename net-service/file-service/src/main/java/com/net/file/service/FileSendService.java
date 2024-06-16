package com.net.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.net.file.entity.FileCollectEntity;
import com.net.file.entity.FileSendEntity;
import com.net.file.entity.UserFileEntity;
import com.net.file.pojo.dto.FileCollectQueryDTO;
import com.net.file.pojo.vo.FileSendPageVO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FileSendService extends IService<FileSendEntity> {
    void sendFile(String link, UserFileEntity userFile, String signer) throws InterruptedException;

    void saveFile(List<UserFileEntity> list, FileCollectEntity collect);
    void saveFile(Long sendId);

    IPage selectPageVO(Page<FileSendPageVO> pageInfo, FileCollectQueryDTO collectQueryDTO);
}
