package com.net.file.pojo.vo;

import lombok.Data;

@Data
public class FileSendPageVO {
    Long userFileId;
    String fileName;
    String fileSize;
    String sendTime;
    String signer;
    Integer status;
}
