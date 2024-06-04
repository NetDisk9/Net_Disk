package com.net.file.pojo.dto;

import lombok.Data;

@Data
public class FileShareDTO {
    Long shareId;
    Long pid;
    Long userId;
    Long userFileId;
    String path;
    String begTime;
    String endTime;
    Integer status;
    String code;
    String link;
    String sortField;
    String sortOrder;
    Boolean validate;
}