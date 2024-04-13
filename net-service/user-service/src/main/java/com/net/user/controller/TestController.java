package com.net.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/super")
@RestController
public class TestController {
    @GetMapping("/list")
    public String test(){
        return "Mk1";
    }
    @GetMapping("/mk2")
    public String testMk2(){
        return "Mk2";
    }
}
