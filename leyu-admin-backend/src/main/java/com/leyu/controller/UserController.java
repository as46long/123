package com.leyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.LoginDTO;
import com.leyu.dto.RegisterDTO;
import com.leyu.entity.User;
import com.leyu.service.UserService;
import com.leyu.utils.JwtUtil;
import com.leyu.vo.Result;
import com.leyu.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        UserVO user = userService.login(dto);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        return Result.success(result);
    }

    @PostMapping("/wxLogin")
    @ApiOperation("微信登录")
    public Result<Map<String, Object>> wxLogin(@RequestParam String code) {
        UserVO user = userService.wxLogin(code);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        return Result.success(result);
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterDTO dto) {
        UserVO user = userService.register(dto);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);
        return Result.success(result);
    }

    @GetMapping("/info")
    @ApiOperation("获取当前用户信息")
    public Result<UserVO> getCurrentUser(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.success(userService.getVOById(userId));
    }

    @PutMapping("/update")
    @ApiOperation("更新用户信息")
    public Result<Void> update(@RequestHeader("Authorization") String token, @RequestBody User user) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        user.setId(userId);
        userService.update(user);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("分页查询用户列表")
    public Result<Page<UserVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.getPage(pageNum, pageSize, keyword));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("获取用户详情")
    public Result<UserVO> detail(@PathVariable Long id) {
        return Result.success(userService.getVOById(id));
    }

    @PutMapping("/status/{id}")
    @ApiOperation("更新用户状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除用户")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
