package com.net.file.pojo.vo;

import lombok.Data;

@Data
public class FileVO {
    private Long userFileId;
    private Long pid;
    private Long fileSize;
    private Integer fileCategory;
    private String fileCover;
    private String fileName;
    private String updateTime;
    private String filePath;
    private Integer isDir;
}
