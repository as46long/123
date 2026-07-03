package com.leyu.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 * 提供文件上传功能，包括通用文件、头像、音频、封面图片
 */
public interface FileService {
    /**
     * 上传通用文件
     * @param file 文件对象
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file);

    /**
     * 上传用户头像
     * @param file 头像文件
     * @return 头像访问URL
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 上传音频文件
     * @param file 音频文件
     * @return 音频访问URL
     */
    String uploadAudio(MultipartFile file);

    /**
     * 上传封面图片
     * @param file 封面图片文件
     * @return 封面访问URL
     */
    String uploadCover(MultipartFile file);
}
