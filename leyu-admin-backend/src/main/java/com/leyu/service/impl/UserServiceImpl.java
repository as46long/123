package com.leyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.LoginDTO;
import com.leyu.dto.RegisterDTO;
import com.leyu.entity.User;
import com.leyu.mapper.UserMapper;
import com.leyu.service.UserService;
import com.leyu.utils.JwtUtil;
import com.leyu.utils.PasswordEncoder;
import com.leyu.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public UserVO login(LoginDTO dto) {
        User user = getByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        return convertToVO(user);
    }

    @Override
    public UserVO wxLogin(String code) {
        // 实际项目中应调用微信API获取openid
        // 这里简化处理，假设code就是openid
        String openid = code;
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getWxOpenid, openid));
        if (user == null) {
            user = new User();
            user.setWxOpenid(openid);
            user.setUsername("wx_" + System.currentTimeMillis());
            user.setPassword(passwordEncoder.encode("123456"));
            user.setNickname("微信用户");
            user.setIsVip(0);
            user.setStatus(1);
            userMapper.insert(user);
        }
        return convertToVO(user);
    }

    @Override
    public UserVO register(RegisterDTO dto) {
        if (getByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setIsVip(0);
        user.setStatus(1);
        userMapper.insert(user);
        return convertToVO(user);
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserVO getVOById(Long id) {
        User user = getById(id);
        return user != null ? convertToVO(user) : null;
    }

    @Override
    public Page<UserVO> getPage(int pageNum, int pageSize, String keyword) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword).or().like(User::getPhone, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> userPage = userMapper.selectPage(page, wrapper);
        Page<UserVO> voPage = new Page<>(pageNum, pageSize, userPage.getTotal());
        voPage.setRecords(userPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public void update(User user) {
        userMapper.updateById(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
