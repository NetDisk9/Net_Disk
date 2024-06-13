package com.net.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.file.entity.FileCollectEntity;

public interface FileCollectService extends IService<FileCollectEntity> {
    public FileCollectEntity getCollectWithCheck(Long collectId);
    public FileCollectEntity getCollectByLinkWithCheck(String link);
}
