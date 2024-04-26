package com.net.file;

import com.net.file.service.FileService;
import com.net.file.util.RegexUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class FileApplicationTests {
    @Resource
    FileService fileService;
    @Test
    public void testImg(){
        System.out.println(RegexUtil.checkImageValid("jpg"));
    }
//    @Test
//    public void testPath()
//    {
//        System.out.println(fileService.isExist("/test(3)/test"));
//        System.out.println(fileService.isExist("/test(3)/testt"));
//        ;
//    }

}
