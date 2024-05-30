package com.net.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.file.entity.ShareEntity;

public interface ShareService extends IService<ShareEntity> {
    /**
     * 带检查的取share实体对象
     * @param link
     * @return {@link ShareEntity }
     */
    public ShareEntity getShareEntityWithCheck(String link);

    /**
     * 检查share实体对象的有效性
     * @param shareEntity
     * @return boolean
     */
    public boolean checkShareValid(ShareEntity shareEntity);
}