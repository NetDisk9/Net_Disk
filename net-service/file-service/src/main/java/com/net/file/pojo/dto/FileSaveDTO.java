package com.net.file.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class FileSaveDTO {
    @NotBlank
    private String link;
    @NotNull
    private Long[] userFileIds;
}
