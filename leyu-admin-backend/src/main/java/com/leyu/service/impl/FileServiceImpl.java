package com.leyu.service.impl;

import com.leyu.service.FileService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload-path}")
    private String configuredUploadPath;

    @Value("${file.access-url}")
    private String accessUrl;

    private String uploadPath;

    private static final List<String> AUDIO_TYPES = Arrays.asList("mp3", "wav", "flac", "aac", "ogg", "m4a");
    private static final List<String> IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    @PostConstruct
    public void init() {
        // 将相对路径转换为绝对路径
        File path = new File(configuredUploadPath);
        if (!path.isAbsolute()) {
            // 获取项目根目录
            String projectPath = System.getProperty("user.dir");
            uploadPath = new File(projectPath, configuredUploadPath).getAbsolutePath();
        } else {
            uploadPath = configuredUploadPath;
        }
        
        // 确保上传根目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        System.out.println("文件上传路径: " + uploadPath);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        return upload(file, "files");
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        validateFileType(file, IMAGE_TYPES, "头像只能上传图片文件");
        return upload(file, "avatars");
    }

    @Override
    public String uploadAudio(MultipartFile file) {
        validateFileType(file, AUDIO_TYPES, "音频文件格式不支持，仅支持 mp3, wav, flac, aac, ogg, m4a");
        return upload(file, "audio");
    }

    @Override
    public String uploadCover(MultipartFile file) {
        validateFileType(file, IMAGE_TYPES, "封面只能上传图片文件");
        return upload(file, "covers");
    }

    private void validateFileType(MultipartFile file, List<String> allowedTypes, String errorMsg) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new RuntimeException(errorMsg);
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!allowedTypes.contains(extension)) {
            throw new RuntimeException(errorMsg);
        }
    }

    private String upload(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFilename = UUID.randomUUID().toString() + extension;
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = folder + "/" + datePath;
        String fullPath = uploadPath + "/" + relativePath;

        File dir = new File(fullPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new RuntimeException("创建目录失败: " + fullPath);
            }
        }

        try {
            File destFile = new File(fullPath + "/" + newFilename);
            file.transferTo(destFile.getAbsoluteFile());
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }

        return accessUrl + relativePath + "/" + newFilename;
    }
}
