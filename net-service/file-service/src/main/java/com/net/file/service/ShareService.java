package com.net.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.net.common.dto.ResponseResult;
import com.net.file.entity.ShareEntity;
import com.net.file.pojo.dto.FileShareDTO;
import com.net.file.pojo.vo.FileVO;
import com.net.file.pojo.vo.ShareInfoVO;

import java.util.List;

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


    /**
     * 创建分享链接
     * @param time, code, userFileId
     * @return ResponseResult
     */
    public ResponseResult createShareLink(String time, String code, String userFileId, String filePath, String fileName);

    public ResponseResult deleteShareLink(String shareId);

    ShareInfoVO getShareInfoByLink(String link);

    IPage listShareFilePage(Page<FileVO> pageInfo, FileShareDTO fileShareDTO);

    ResponseResult listShareFileByPath(Integer page, Integer pageSize, FileShareDTO fileShareDTO, ShareEntity one);

    ResponseResult listShareFile(Integer page, Integer pageSize, FileShareDTO fileShareDTO, ShareEntity one);

    ResponseResult convertToPageVO(List<FileVO> fileVOS, int size, int total);

    ShareEntity checkListShareFileParam(Integer page, Integer pageSize, FileShareDTO fileShareDTO);

    // 是否已校验提取码
    void checkHasValid(String link);
}
