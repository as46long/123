package com.leyu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.LoginDTO;
import com.leyu.dto.RegisterDTO;
import com.leyu.entity.User;
import com.leyu.vo.UserVO;

public interface UserService {
    UserVO login(LoginDTO dto);
    UserVO wxLogin(String code);
    UserVO register(RegisterDTO dto);
    User getByUsername(String username);
    User getById(Long id);
    UserVO getVOById(Long id);
    Page<UserVO> getPage(int pageNum, int pageSize, String keyword);
    void update(User user);
    void updateStatus(Long id, Integer status);
    void delete(Long id);
}
