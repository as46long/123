package com.leyu.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求DTO
 * 用于用户登录时的参数传递
 */
@Data
public class LoginDTO {
    /** 用户名，必填 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码，必填 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
