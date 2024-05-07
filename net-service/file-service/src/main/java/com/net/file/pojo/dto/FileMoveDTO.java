package com.net.file.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileMoveDTO {
    private Long pid;
    @NotNull
    private Long[] userFileId;
}


