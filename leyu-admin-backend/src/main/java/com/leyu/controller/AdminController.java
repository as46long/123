package com.leyu.controller;

import com.leyu.dto.LoginDTO;
import com.leyu.service.AdminService;
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
@RequestMapping("/api/admin")
@Api(tags = "管理员接口")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @ApiOperation("管理员登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        UserVO admin = adminService.login(dto);
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), "ADMIN");
        Map<String, Object> result = new HashMap<>();
        result.put("user", admin);
        result.put("token", token);
        return Result.success(result);
    }
}
