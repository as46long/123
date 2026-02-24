package com.leyu.service;

import com.leyu.dto.LoginDTO;
import com.leyu.entity.Admin;
import com.leyu.vo.UserVO;

public interface AdminService {
    UserVO login(LoginDTO dto);
    Admin getByUsername(String username);
    void updateLastLoginTime(Long id);
}
