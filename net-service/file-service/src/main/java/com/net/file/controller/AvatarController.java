package com.net.file.controller;

import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.CustomException;
import com.net.file.util.AliOssUtil;
import com.net.file.util.RegexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class AvatarController {

    @Resource
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     */
    @PostMapping(value = "/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseResult upload(@RequestPart MultipartFile[] multipartFiles) {
        ArrayList<String> filePaths = new ArrayList<>();
        try {
            if (multipartFiles == null || multipartFiles.length == 0) { //检查图片文件是否存在
                return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
            }
            for (MultipartFile multipartFile : multipartFiles) {
                // 图片不存在
                if (multipartFile == null) return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
                //原始文件名
                String originalFilename = multipartFile.getOriginalFilename();
                if (originalFilename == null) return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);

                //截取原始文件名的后缀
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                if (extension.isEmpty() || !RegexUtil.checkImageValid(extension)) {
                    return ResponseResult.errorResult(ResultCodeEnum.IMAGE_FORMAT_ERROR);
                }
                //构造新文件名称
                String objectName = UUID.randomUUID() + extension;
                //文件的请求路径
                String filePath = null;
                filePath = aliOssUtil.upload(multipartFile.getBytes(), objectName);
                filePaths.add(filePath);
            }
        } catch (IOException e) {
            throw new CustomException(ResultCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult(filePaths);
    }

}
