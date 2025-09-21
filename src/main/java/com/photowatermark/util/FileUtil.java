package com.photowatermark.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 文件工具类，提供文件相关的操作方法
 */
public class FileUtil {
    private static final Logger logger = LogManager.getLogger(FileUtil.class);

    // 支持的图片文件扩展名
    private static final List<String> SUPPORTED_IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp");

    /**
     * 检查文件是否为支持的图片文件
     * @param file 要检查的文件
     * @return 如果是支持的图片文件则返回true，否则返回false
     */
    public static boolean isImageFile(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }

        String fileName = file.getName().toLowerCase();
        for (String extension : SUPPORTED_IMAGE_EXTENSIONS) {
            if (fileName.endsWith("." + extension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取文件的图片格式（不包含点）
     * @param file 图片文件
     * @return 图片格式字符串
     */
    public static String getImageFormat(File file) {
        if (file == null || !file.isFile()) {
            return "jpg"; // 默认返回jpg格式
        }

        String fileName = file.getName().toLowerCase();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            String extension = fileName.substring(dotIndex + 1);
            if (SUPPORTED_IMAGE_EXTENSIONS.contains(extension)) {
                return extension;
            }
        }

        return "jpg"; // 默认返回jpg格式
    }

    /**
     * 获取目录中的所有图片文件
     * @param directory 目录对象
     * @return 图片文件列表
     */
    public static List<File> getImageFilesInDirectory(File directory) {
        List<File> imageFiles = new ArrayList<>();

        if (directory == null || !directory.isDirectory()) {
            return imageFiles;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return imageFiles;
        }

        for (File file : files) {
            if (file.isFile() && isImageFile(file)) {
                imageFiles.add(file);
            }
        }

        return imageFiles;
    }

    /**
     * 创建目录，如果目录已存在则忽略
     * @param directory 要创建的目录
     * @return 如果创建成功或目录已存在则返回true，否则返回false
     */
    public static boolean createDirectory(File directory) {
        if (directory == null) {
            return false;
        }

        if (directory.exists()) {
            if (directory.isDirectory()) {
                return true; // 目录已存在
            } else {
                logger.error("路径已存在但不是目录: {}", directory.getAbsolutePath());
                return false;
            }
        }

        boolean created = directory.mkdirs();
        if (created) {
            logger.info("成功创建目录: {}", directory.getAbsolutePath());
        } else {
            logger.error("创建目录失败: {}", directory.getAbsolutePath());
        }

        return created;
    }
}