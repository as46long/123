package com.leyu.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Integer isVip;
    private LocalDateTime vipExpireTime;
    private Integer status;
    private LocalDateTime createTime;
}
