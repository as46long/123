package com.leyu.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 注册请求DTO
 * 用于用户注册时的参数传递
 */
@Data
public class RegisterDTO {
    /** 用户名，必填 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码，必填 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 昵称，可选 */
    private String nickname;

    /** 手机号，可选 */
    private String phone;
}
