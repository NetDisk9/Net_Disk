package com.net.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_share")
public class ShareEntity {
    @TableId(value = "share_id",type = IdType.AUTO)
    private Long shareId;
    private Long userId;
    private Long userFileId;
    private String filePath;
    private String begTime;
    private String endTime;
    private Integer status;
    private String code;
    private String link;
}
