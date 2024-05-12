package com.net.file.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FileData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long fileId;

    private Long fileSize;

    private String fileMd5;

    private String fileUrl;

    private Integer fileCategory;

    private Integer delFlag;

    private String fileCover;

    private String fileName;


}
