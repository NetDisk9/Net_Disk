package com.net.user.controller;


import com.net.api.client.FileClient;
import com.net.redis.constant.RedisConstants;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.CustomException;
import com.net.common.util.JWTUtil;
import com.net.redis.utils.RedisUtil;
import com.net.user.entity.LoginLog;
import com.net.user.pojo.dto.LoginDTO;
import com.net.user.pojo.dto.RegisterDTO;
import com.net.user.pojo.dto.UpdatePasswordDTO;
import com.net.user.pojo.dto.UserDTO;
import com.net.user.service.LoginLogService;
import com.net.user.service.SysUserService;
import com.net.user.util.IPUtil;
import com.net.user.util.RegexUtil;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-15
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {
    private final SysUserService userService;
    private final LoginLogService loginLogService;
    private final FileClient fileClient;
    private final RedisUtil redisUtil;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        if (loginDTO == null || StringUtil.isNullOrEmpty(loginDTO.getPassword())) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
//        loginDTO.setPassword(SHAUtil.encrypt(loginDTO.getPassword()));
        ResponseResult result = null;
        if (!StringUtil.isNullOrEmpty(loginDTO.getEmail())) {
            result = userService.getUserIdByEmailAndPassword(loginDTO.getEmail(), loginDTO.getPassword());
        } else if (loginDTO.getId() != null) {
            result = userService.getUserIdByUserIdAndPassword(loginDTO.getId(), loginDTO.getPassword());
        } else if (!StringUtil.isNullOrEmpty(loginDTO.getUsername())) {
            result = userService.getUserIdByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword());
        } else {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        if (result.getCode() != 200) {
            return result;
        }
        Long userId = (Long) result.getData();
        String token = JWTUtil.getJWT(userId + "");
        // 存到redis
        redisUtil.set(RedisConstants.LOGIN_USER_KEY + token, token, RedisConstants.LOGIN_USER_TTL);
        String ip = IPUtil.getIp(request);
        String address = IPUtil.getIpAddress(ip);
        loginLogService.save(new LoginLog(userId, loginDTO.getDeviceName(), loginDTO.getDeviceOS(), LocalDateTime.now(), address, ip));
        return ResponseResult.okResult(token);
    }

    @GetMapping("/login/method")
    public ResponseResult getLoginMethod() {
        return userService.getLoginMethod();
    }

    @PutMapping("/login/method")
    public ResponseResult updateLoginMethod(@RequestHeader("type") String methods) {
        if (!methods.matches("[01]{3}")) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        return userService.updateLoginMethod(methods);
    }


    @PostMapping("/code/login")
    public ResponseResult loginByCode(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        if (loginDTO == null || StringUtil.isNullOrEmpty(loginDTO.getCode()) || StringUtil.isNullOrEmpty(loginDTO.getEmail())) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        if (!loginDTO.getCode().equals(userService.getUserLoginCode(loginDTO.getEmail()))) {
            return ResponseResult.errorResult(ResultCodeEnum.CODE_ERROR);
        }
        userService.deleteUserLoginCode(loginDTO.getEmail());
        Long userId = userService.getUserIdByEmail(loginDTO.getEmail());
        String ip = IPUtil.getIp(request);
        String address = IPUtil.getIpAddress(ip);
        loginLogService.save(new LoginLog(userId, loginDTO.getDeviceName(), loginDTO.getDeviceOS(), LocalDateTime.now(), address, ip));

        String token = JWTUtil.getJWT(userId + "");
        // 存到redis
        redisUtil.set(RedisConstants.LOGIN_USER_KEY + token, token, RedisConstants.LOGIN_USER_TTL);
        return ResponseResult.okResult(token);

    }

    @GetMapping("/info")
    public ResponseResult getUserInfo() {
        return userService.getUserInfo();
    }

    @GetMapping("/info/login")
    public ResponseResult getLoginRecord() {
        List<LoginLog> records = new ArrayList<>();
        for (LoginLog loginLog : loginLogService.list()) {
            if (Objects.equals(loginLog.getUserId(), BaseContext.getCurrentId())) {
                records.add(loginLog);
            }
        }
        return ResponseResult.okResult(records);
    }

    @PutMapping("/update")
    public ResponseResult update(@RequestBody UserDTO userDTO) {
        if (userDTO == null) { //为空
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        if (StringUtils.isBlank(userDTO.getNickname()) && StringUtils.isBlank(userDTO.getUsername())) { //为空
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        return userService.updateUserInfo(userDTO);
    }

    @PutMapping("/update/password")
    public ResponseResult updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO, @RequestHeader("Authorization") String token) {
        if (updatePasswordDTO == null) { //为空
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        String oldPassword = updatePasswordDTO.getOldPassword();
        String newPassword = updatePasswordDTO.getNewPassword();
        int times = 0;
        if (redisUtil.get(RedisConstants.PASSWORD_ERROR_TIMES) != null) { // 获取失败次数
            times = (int) redisUtil.get(RedisConstants.PASSWORD_ERROR_TIMES);
        }
        if (times >= RedisConstants.MAX_ERROR_TIMES) { // 失败次数过多
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        ResponseResult responseResult = null;

        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) { //为空
            responseResult = ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        if (!RegexUtil.checkPasswordValid(newPassword)) { // 格式不正确
            responseResult = ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }

        if (responseResult != null && responseResult.getCode() != ResultCodeEnum.SUCCESS.getCode()) { // 修改错误
            redisUtil.set(RedisConstants.PASSWORD_ERROR_TIMES, times + 1, RedisConstants.PASSWORD_ERROR_TTL);
            return responseResult;
        }
        // 根据Id和输入的密码查找用户
        responseResult = userService.getUserIdByUserIdAndPassword(BaseContext.getCurrentId(), oldPassword);

        // 修改密码
        responseResult = userService.updatePassword(updatePasswordDTO);
        if (responseResult != null && responseResult.getCode() == ResultCodeEnum.SUCCESS.getCode()) { // 修改成功
            redisUtil.del(RedisConstants.PASSWORD_ERROR_TIMES);
            redisUtil.del(RedisConstants.LOGIN_USER_KEY + token);
            return responseResult;
        }
        return ResponseResult.okResult();
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseResult uploadAvatar(@RequestPart MultipartFile[] multipartFiles) {
        try {

            return fileClient.upload(multipartFiles);
        } catch (IOException e) {
            throw new CustomException(ResultCodeEnum.SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseResult register(@RequestBody RegisterDTO registerDTO, HttpServletRequest request) {
        if (registerDTO == null || StringUtil.isNullOrEmpty(registerDTO.getUsername()) ||
                                   StringUtil.isNullOrEmpty(registerDTO.getPassword()) ||
                                   StringUtil.isNullOrEmpty(registerDTO.getEmail()) ||
                                   StringUtil.isNullOrEmpty(registerDTO.getCode())) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        return userService.insertRegisterInfo(registerDTO);
    }

    @GetMapping("/device")
    public ResponseResult getDevice() {
        return loginLogService.getDevice();
    }


}
