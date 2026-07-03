package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.LoginDTO;
import com.leyu.dto.RegisterDTO;
import com.leyu.mapper.FavoriteMapper;
import com.leyu.mapper.SongCommentMapper;
import com.leyu.mapper.UserBehaviorMapper;
import com.leyu.service.UserService;
import com.leyu.utils.JwtUtil;
import com.leyu.vo.Result;
import com.leyu.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户登录、注册、微信登录、个人信息管理等请求
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private SongCommentMapper songCommentMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    /**
     * 用户名密码登录
     * @param dto 登录参数(用户名、密码)
     * @return 用户信息和JWT令牌
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        UserVO user = userService.login(dto);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        return Result.success(result);
    }

    /**
     * 微信小程序登录
     * 通过微信code获取openid，自动创建或登录用户
     * @param request 包含微信登录code
     * @return 用户信息和JWT令牌
     */
    @PostMapping("/wxLogin")
    @Operation(summary = "微信登录")
    public Result<Map<String, Object>> wxLogin(@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            if (code == null || code.isEmpty()) {
                return Result.error(400, "缺少code参数");
            }

            UserVO user = userService.wxLogin(code);
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("token", token);
            return Result.success(result);
        } catch (Exception e) {
            System.out.println("微信登录异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "微信登录失败：" + e.getMessage());
        }
    }

    /**
     * 微信登录并更新用户信息
     * @param request 包含code、nickname、avatar
     * @return 用户信息和JWT令牌
     */
    @PostMapping("/wxLoginWithInfo")
    @Operation(summary = "微信用户信息登录")
    public Result<Map<String, Object>> wxLoginWithInfo(@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            String nickname = request.get("nickname");
            String avatar = request.get("avatar");

            if (code == null || code.isEmpty()) {
                return Result.error(400, "缺少code参数");
            }

            UserVO user = userService.wxLoginWithInfo(code, nickname, avatar);
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("token", token);
            return Result.success(result);
        } catch (Exception e) {
            System.out.println("微信登录异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error(500, "微信登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户注册
     * @param dto 注册参数(用户名、密码、昵称、手机号)
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     * @param token JWT令牌
     * @return 用户详细信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<UserVO> info(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(userService.getById(userId));
    }

    /**
     * 更新用户个人信息
     * @param token JWT令牌
     * @param vo 更新的用户信息
     * @return 更新结果
     */
    @PutMapping("/update")
    @Operation(summary = "更新用户信息")
    public Result<Void> update(@RequestHeader("Authorization") String token, @RequestBody UserVO vo) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        userService.update(userId, vo);
        return Result.success();
    }

    /**
     * 获取用户统计数据
     * 包括收藏数量、评论数量、听过歌曲数量
     * @param token JWT令牌
     * @return 统计数据Map
     */
    @GetMapping("/stats")
    @Operation(summary = "获取用户统计数据")
    public Result<Map<String, Object>> stats(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        Map<String, Object> stats = new HashMap<>();
        
        // 收藏数量
        int favoriteCount = favoriteMapper.countByUserId(userId);
        stats.put("favoriteCount", favoriteCount);
        
        // 评论数量（歌曲评论）
        int commentCount = songCommentMapper.countByUserId(userId);
        stats.put("commentCount", commentCount);
        
        // 听过歌曲数量（去重）
        int playCount = userBehaviorMapper.countUserPlaySongs(userId);
        stats.put("playCount", playCount);
        
        return Result.success(stats);
    }
}
