package com.net.api.client;

import com.net.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@FeignClient("file-service")
public interface FileClient {
    @PostMapping(value = "/file/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseResult upload(@RequestPart MultipartFile[] multipartFiles)throws IOException ;
}