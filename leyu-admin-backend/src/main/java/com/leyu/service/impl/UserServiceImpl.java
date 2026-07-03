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
import com.leyu.utils.WechatUtil;
import com.leyu.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 实现用户登录、注册、微信登录、信息管理等功能
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WechatUtil wechatUtil;

    @Value("${wechat.appid}")
    private String wechatAppid;

    @Value("${wechat.secret}")
    private String wechatSecret;

    /**
     * 用户名密码登录
     * 验证用户名存在性、密码正确性、账号状态
     */
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
        return wxLoginWithOptionalInfo(code, null, null);
    }

    @Override
    public UserVO wxLoginWithInfo(String code, String nickname, String avatar) {
        return wxLoginWithOptionalInfo(code, nickname, avatar);
    }

    /**
     * 微信登录
     * 通过微信code获取openid，新用户自动创建账号
     */
    private UserVO wxLoginWithOptionalInfo(String code, String nickname, String avatar) {
        try {
            // 调用微信API获取openid
            WechatUtil.WechatLoginResult result = wechatUtil.code2Session(wechatAppid, wechatSecret, code);
            if (!result.isSuccess()) {
                throw new RuntimeException("微信登录失败: " + result.getErrmsg());
            }
            String openid = result.getOpenid();

            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getWxOpenid, openid));
            if (user == null) {
                // 新用户，创建账号
                user = new User();
                user.setWxOpenid(openid);
                user.setUsername("wx_" + System.currentTimeMillis());
                user.setPassword(passwordEncoder.encode("123456"));
                user.setNickname(StringUtils.hasText(nickname) ? nickname : "微信用户");
                user.setAvatar(avatar);
                user.setIsVip(0);
                user.setStatus(1);
                userMapper.insert(user);
            } else {
                // 老用户，可选更新昵称和头像
                if (StringUtils.hasText(nickname)) {
                    user.setNickname(nickname);
                }
                if (StringUtils.hasText(avatar)) {
                    user.setAvatar(avatar);
                }
                userMapper.updateById(user);

                if (user.getStatus() == 0) {
                    throw new RuntimeException("账号已被禁用");
                }
            }
            return convertToVO(user);
        } catch (Exception e) {
            throw new RuntimeException("微信登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户注册
     * 检查用户名唯一性，加密密码，创建用户
     */
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
    public User getEntityById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
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
    public void update(Long id, UserVO vo) {
        User user = userMapper.selectById(id);
        if (user != null) {
            if (StringUtils.hasText(vo.getNickname())) {
                user.setNickname(vo.getNickname());
            }
            if (StringUtils.hasText(vo.getAvatar())) {
                user.setAvatar(vo.getAvatar());
            }
            if (StringUtils.hasText(vo.getPhone())) {
                user.setPhone(vo.getPhone());
            }
            if (StringUtils.hasText(vo.getEmail())) {
                user.setEmail(vo.getEmail());
            }
            userMapper.updateById(user);
        }
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
