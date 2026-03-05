package com.fishing.utils;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Slf4j
@Component
public class MinioUtils {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${file.base-url}")
    private String fileBaseUrl;

    @Value("${file.post-image-dir}")
    private String postImageDir;

    @Value("${file.user-avatar-dir}")
    private String userAvatarDir;

    @Value("${file.spot-image-dir:spot_image}")
    private String spotImageDir;

    @Value("${file.gear-image-dir:gear_image}")
    private String gearImageDir;

    public MinioUtils(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * 上传文件到 MinIO
     *
     * @param file       文件
     * @param dir        目录
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String dir) {
        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            String objectName = dir + "/" + fileName;

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 返回完整URL
            return getFullUrl(fileName, dir);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传帖子图片
     */
    public String uploadPostImage(MultipartFile file) {
        return uploadFile(file, postImageDir);
    }

    /**
     * 上传用户头像
     */
    public String uploadUserAvatar(MultipartFile file) {
        return uploadFile(file, userAvatarDir);
    }

    /**
     * 上传钓点图片
     */
    public String uploadSpotImage(MultipartFile file) {
        return uploadFile(file, spotImageDir);
    }

    /**
     * 上传装备图片
     */
    public String uploadGearImage(MultipartFile file) {
        return uploadFile(file, gearImageDir);
    }

    /**
     * 获取文件完整URL
     *
     * @param relativePath 相对路径（文件名）
     * @param dir          目录
     * @return 完整URL
     */
    public String getFullUrl(String relativePath, String dir) {
        return fileBaseUrl + "/" + bucket + "/" + dir + "/" + relativePath;
    }

    /**
     * 从完整URL中提取相对路径
     */
    public String extractRelativePath(String fullUrl) {
        if (fullUrl == null || fullUrl.isEmpty()) {
            return "";
        }
        // 提取文件名部分
        int lastSlash = fullUrl.lastIndexOf("/");
        if (lastSlash > 0) {
            return fullUrl.substring(lastSlash + 1);
        }
        return fullUrl;
    }
}
