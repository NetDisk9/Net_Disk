package com.net.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@TableName("file_collect")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FileCollectEntity {
    @TableId(value = "collect_id",type = IdType.ASSIGN_ID)
    private Long collectId;
    private Long userId;
    private String title;
    private String begTime;
    private String endTime;
    private Integer maxNum;
    private String signer;
    private Integer status;
    private String link;
    private Integer curNum;
}
