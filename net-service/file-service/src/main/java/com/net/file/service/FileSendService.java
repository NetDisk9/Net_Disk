package com.net.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.file.entity.FileCollectEntity;
import com.net.file.entity.FileSendEntity;
import com.net.file.entity.UserFileEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FileSendService extends IService<FileSendEntity> {
    void sendFile(String link, UserFileEntity userFile, String signer) throws InterruptedException;

    void saveFile(List<UserFileEntity> list, FileCollectEntity collect);
    void saveFile(Long sendId);
}
