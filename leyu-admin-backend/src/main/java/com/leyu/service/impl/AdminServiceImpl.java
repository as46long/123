package com.leyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leyu.dto.LoginDTO;
import com.leyu.entity.Admin;
import com.leyu.mapper.AdminMapper;
import com.leyu.service.AdminService;
import com.leyu.utils.PasswordEncoder;
import com.leyu.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 管理员服务实现类
 * 实现管理员登录验证、信息管理等功能
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserVO login(LoginDTO dto) {
        Admin admin = getByUsername(dto.getUsername());
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        updateLastLoginTime(admin.getId());
        UserVO vo = new UserVO();
        vo.setId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setNickname(admin.getUsername());
        return vo;
    }

    @Override
    public Admin getByUsername(String username) {
        return adminMapper.selectOne(new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, username));
    }

    @Override
    public void updateLastLoginTime(Long id) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setLastLoginTime(LocalDateTime.now());
        adminMapper.updateById(admin);
    }
}
