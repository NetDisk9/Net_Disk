package com.net.file;

import com.net.file.util.RegexUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
public class FileApplicationTests {
    @Test
    public void test(){
        System.out.println(RegexUtil.checkImageValid("jpg"));
    }
}
