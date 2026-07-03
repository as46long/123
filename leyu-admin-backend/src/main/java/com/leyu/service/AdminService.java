package com.leyu.service;

import com.leyu.dto.LoginDTO;
import com.leyu.entity.Admin;
import com.leyu.vo.UserVO;

/**
 * 管理员服务接口
 * 提供管理员登录和信息管理功能
 */
public interface AdminService {
    /**
     * 管理员登录
     * @param dto 登录参数
     * @return 管理员视图对象
     */
    UserVO login(LoginDTO dto);

    /**
     * 根据用户名获取管理员实体
     * @param username 用户名
     * @return 管理员实体
     */
    Admin getByUsername(String username);

    /**
     * 更新管理员最后登录时间
     * @param id 管理员ID
     */
    void updateLastLoginTime(Long id);
}
