package com.net.user.pojo.dto;

import lombok.Data;

@Data
public class ForgetPasswordDTO {
    private String email;
    private String newPassword;
}
