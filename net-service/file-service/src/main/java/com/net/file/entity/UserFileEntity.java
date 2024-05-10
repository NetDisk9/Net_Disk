package com.net.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.net.common.util.DateFormatUtil;
import com.net.common.util.LongIdUtil;
import com.net.common.wrapper.LocalDateTimeWrapper;
import com.net.file.constant.DirConstants;
import com.net.file.constant.FileStatusConstants;
import com.net.file.util.PathUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_file")
@Builder
public class UserFileEntity {
    @TableId(value = "user_file_id", type = IdType.ASSIGN_ID)
    Long userFileId;
    Long userId;
    Long pid;
    Long fileId;
    String fileName;
    String filePath;
    String createTime;
    String updateTime;
    String recycleTime;
    Integer status;
    Integer isDir;
    @TableField(exist = false)
    Long fileSize;
    @TableField(exist = false)
    Integer fileCategory;
    @TableField(exist = false)
    String fileCover;
    public boolean hasParent(){
        return filePath.lastIndexOf("/")!=0;
    }
    public String getParentPath(){
        int pos = filePath.lastIndexOf("/");
        if(pos<=0){
            return null;
        }
        return filePath.substring(0,pos);
    }
    public String getExtName(){
        int pos=fileName.lastIndexOf(".");
        if(pos==-1){
            return null;
        }
        return filePath.substring(pos+1);

    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        UserFileEntity userFile = (UserFileEntity) object;
        return Objects.equals(userFileId, userFile.userFileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userFileId);
    }

    @Override
    public String toString() {
        return "UserFileEntity{" +
                "userFileId=" + userFileId +
                ", pid=" + pid +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    public static class UserFileEntityFactory{
        public static UserFileEntity createDirEntity(UserFileEntity parent,String name,Long userId){
            String dateTime= DateFormatUtil.format(LocalDateTimeWrapper.now());
            UserFileEntity userFile = UserFileEntity.builder()
                    .filePath(parent == null ? "/"+name : parent.getFilePath() + "/" + name)
                    .fileName(name)
                    .userId(userId)
                    .isDir(DirConstants.IS_DIR)
                    .pid(parent == null ? null : parent.getUserFileId())
                    .status(FileStatusConstants.NORMAL)
                    .createTime(dateTime)
                    .updateTime(dateTime)
                    .recycleTime(null)
                    .build();
            return userFile;
        }
        public static UserFileEntity createDirEntity(String path,Integer status,Long userId){
            String dateTime= DateFormatUtil.format(LocalDateTimeWrapper.now());
            String name= PathUtil.getNameFromPath(path);
            UserFileEntity userFile = UserFileEntity.builder()
                    .filePath(path)
                    .fileName(name)
                    .userId(userId)
                    .isDir(DirConstants.IS_DIR)
                    .pid(null)
                    .status(status)
                    .createTime(dateTime)
                    .updateTime(dateTime)
                    .recycleTime(null)
                    .build();
            return userFile;
        }
    }
}
