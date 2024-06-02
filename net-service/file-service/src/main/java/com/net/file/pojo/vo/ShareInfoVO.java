package com.net.file.pojo.vo;

import lombok.Data;

@Data
public class ShareInfoVO {
    private Long shareId;
    private Long userId;
    private Long userFileId;
    private String begTime;
    private String endTime;
    private Integer status;
    private String code;
    private String link;
    private String filePath;
    private String username;
    private String avatar;
    private Integer check;
    private Integer count;
}
