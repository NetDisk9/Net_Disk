package com.net.file.pojo.dto;

import lombok.Data;

@Data
public class FileQueryDTO {
    Long userFileId;
    Long pid;
    Long currentUserId;
    String path;
    Integer status;
    Integer category;
    String sortField;
    String sortOrder;
}
