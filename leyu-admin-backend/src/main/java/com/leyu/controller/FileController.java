package com.leyu.controller;

import com.leyu.service.FileService;
import com.leyu.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件控制器
 * 处理文件上传功能，包括通用文件、头像、音频、封面图片的上传
 */
@RestController
@RequestMapping("/api/file")
@Tag(name = "文件管理")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传通用文件
     * @param file 文件对象
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadFile(file);
        return Result.success(url);
    }

    /**
     * 上传用户头像
     * @param file 头像文件
     * @return 头像访问URL
     */
    @PostMapping("/uploadAvatar")
    @Operation(summary = "上传头像")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadAvatar(file);
        return Result.success(url);
    }

    /**
     * 上传音频文件
     * @param file 音频文件
     * @return 音频访问URL
     */
    @PostMapping("/uploadAudio")
    @Operation(summary = "上传音频文件")
    public Result<String> uploadAudio(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadAudio(file);
        return Result.success(url);
    }

    /**
     * 上传封面图片
     * @param file 封面图片文件
     * @return 封面访问URL
     */
    @PostMapping("/uploadCover")
    @Operation(summary = "上传封面图片")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadCover(file);
        return Result.success(url);
    }
}
