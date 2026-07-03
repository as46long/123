package com.leyu.controller;

import com.leyu.service.FileService;
import com.leyu.service.SystemConfigService;
import com.leyu.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统配置控制器
 * 管理系统级别的配置参数，如启动画面等
 */
@RestController
@RequestMapping("/api/system")
@Tag(name = "系统配置")
public class SystemConfigController {

    /** 启动画面配置键 */
    public static final String KEY_SPLASH_IMAGE = "splash_image";

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private FileService fileService;

    /**
     * 获取配置值
     * @param key 配置键
     * @return 配置值
     */
    @GetMapping("/config/{key}")
    @Operation(summary = "获取配置值")
    public Result<String> getConfig(@PathVariable String key) {
        String value = systemConfigService.getConfigValue(key);
        return Result.success(value);
    }

    /**
     * 设置配置值
     * @param key 配置键
     * @param value 配置值
     * @return 操作结果
     */
    @PostMapping("/config/{key}")
    @Operation(summary = "设置配置值")
    public Result<Void> setConfig(@PathVariable String key, @RequestBody String value) {
        systemConfigService.setConfigValue(key, value);
        return Result.success(null);
    }

    /**
     * 上传启动画面图片
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/uploadSplashImage")
    @Operation(summary = "上传启动画面图片")
    public Result<String> uploadSplashImage(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadCover(file);
        systemConfigService.setConfigValue(KEY_SPLASH_IMAGE, url);
        return Result.success(url);
    }

    /**
     * 获取启动画面图片URL
     * @return 图片访问URL
     */
    @GetMapping("/splashImage")
    @Operation(summary = "获取启动画面图片")
    public Result<String> getSplashImage() {
        String url = systemConfigService.getConfigValue(KEY_SPLASH_IMAGE);
        return Result.success(url);
    }
}
