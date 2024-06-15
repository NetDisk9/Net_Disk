package com.net.file.pojo.dto;

import lombok.Data;

@Data
public class FileCollectCreateDTO {
    String title;
    Integer duration;
    Integer maxNum;
    String signer;
    Integer autoCollect;
}
