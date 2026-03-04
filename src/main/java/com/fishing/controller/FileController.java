package com.fishing.controller;

import com.fishing.pojo.Result;
import com.fishing.utils.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*")
@Slf4j
public class FileController {

    private final MinioUtils minioUtils;

    public FileController(MinioUtils minioUtils) {
        this.minioUtils = minioUtils;
    }

    /**
     * 上传帖子图片
     */
    @PostMapping("/upload/post")
    public Result<String> uploadPostImage(@RequestParam("file") MultipartFile file) {
        log.info("上传帖子图片，文件名：{}，大小：{}", file.getOriginalFilename(), file.getSize());
        try {
            String url = minioUtils.uploadPostImage(file);
            log.info("上传成功，URL：{}", url);
            return Result.success(url);
        } catch (Exception e) {
            log.error("上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/upload/avatar")
    public Result<String> uploadUserAvatar(@RequestParam("file") MultipartFile file) {
        log.info("上传用户头像，文件名：{}，大小：{}", file.getOriginalFilename(), file.getSize());
        try {
            String url = minioUtils.uploadUserAvatar(file);
            log.info("上传成功，URL：{}", url);
            return Result.success(url);
        } catch (Exception e) {
            log.error("上传失败", e);
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传帖子图片
     */
    @PostMapping("/upload/post/batch")
    public Result<java.util.List<String>> uploadPostImages(@RequestParam("files") MultipartFile[] files) {
        log.info("批量上传帖子图片，数量：{}", files.length);
        java.util.List<String> urls = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String url = minioUtils.uploadPostImage(file);
                urls.add(url);
            } catch (Exception e) {
                log.error("上传文件失败：{}，错误：{}", file.getOriginalFilename(), e.getMessage());
            }
        }
        return Result.success(urls);
    }
}
