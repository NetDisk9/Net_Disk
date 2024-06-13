package com.net.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@TableName("file_send")
@Data
@Builder
public class FileSendEntity {
    @TableId(value = "send_id",type = IdType.ASSIGN_ID)
    private Long sendId;
    private Long userFileId;
    private Long userId;
    private Long collectId;
    private String sendTime;
    private String signer;
    private Integer status;
}
