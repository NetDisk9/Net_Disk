package com.net.file.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class FileUploadDTO {
    @NotBlank
    String fileMd5;
    @NotBlank
    String filePath;
    @NotBlank
    String fileName;
    Integer totalChunk;
}
