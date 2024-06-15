package com.net.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.common.dto.ResponseResult;
import com.net.file.entity.FileCollectEntity;

public interface FileCollectService extends IService<FileCollectEntity> {
    public FileCollectEntity getCollectWithCheck(Long collectId);
    public FileCollectEntity getCollectByLinkWithCheck(String link);
    public ResponseResult getCollectByLink(String link);
    public ResponseResult createCollect(String title, Integer duration, Integer maxNum, String signer, Integer autoCollect);
    public ResponseResult deleteCollectByCollectId(Long collectId);
}
