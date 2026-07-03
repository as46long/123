package com.leyu.controller;

import com.leyu.dto.LoginDTO;
import com.leyu.service.AdminService;
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
 * 管理员控制器
 * 处理管理员登录和权限验证
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员接口")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 管理员登录
     * @param dto 登录参数(用户名、密码)
     * @return 管理员信息和JWT令牌
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        try {
            UserVO admin = adminService.login(dto);
            String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), "ADMIN");
            Map<String, Object> result = new HashMap<>();
            result.put("user", admin);
            result.put("token", token);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(500, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 测试管理员权限
     * @return 权限验证成功消息
     */
    @GetMapping("/test")
    @Operation(summary = "测试管理员权限")
    public Result<String> testAdmin() {
        return Result.success("管理员权限验证成功");
    }

    /**
     * 健康检查接口
     * @return 服务运行状态
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Result<String> health() {
        return Result.success("管理员服务正常运行");
    }
}
