package com.net.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("user_share")
@AllArgsConstructor
@NoArgsConstructor
public class ShareEntity {
    @TableId(value = "share_id",type = IdType.AUTO)
    private Long shareId;
    private Long userId;
    private Long userFileId;
    private String begTime;
    private String endTime;
    private Integer status;
    private String code;
    private String link;
    private String filePath;
    private String fileName;
}
