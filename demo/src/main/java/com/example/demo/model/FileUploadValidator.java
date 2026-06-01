package com.example.demo.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileUploadValidator {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        ".xlsx", ".xls", ".csv"
    );

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB

    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请上传有效的文件");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小超过限制，最大允许100MB");
        }

        // 检查文件扩展名
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String lowerFilename = filename.toLowerCase();
        boolean isValidExtension = ALLOWED_EXTENSIONS.stream()
            .anyMatch(lowerFilename::endsWith);

        if (!isValidExtension) {
            throw new IllegalArgumentException("不支持的文件格式，请上传 .xlsx、.xls 或 .csv 文件");
        }
    }
}
