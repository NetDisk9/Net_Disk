package com.net.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("file")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileData implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "file_id",type = IdType.ASSIGN_ID)
    private Long fileId;
    private Long fileSize;
    private String fileMd5;
    private String fileUrl;
    private Integer fileCategory;
    private Integer delFlag;
    private String fileCover;
    private String fileName;

}
