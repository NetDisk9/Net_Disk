package com.net.file.pojo.dto;

import lombok.Data;

/**
 * FileDTO
 */
@Data
public class FileDTO {
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 文件id
     */
    private String fileId;
    /**
     * 目录id
     */
    private String pid;
    /**
     * 状态
     */
    private String status;
}
