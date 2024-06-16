package com.net.file.pojo.dto;

import lombok.Data;


@Data
public class FileCollectQueryDTO {
    Integer collectId;
    String link;
    String sortField;
    String sortOrder;
}
