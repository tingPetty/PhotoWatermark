package com.photowatermark.service;

import com.photowatermark.exception.ExifReadException;
import com.photowatermark.exception.ImageProcessException;
import com.photowatermark.model.ImageInfo;
import com.photowatermark.model.WatermarkConfig;
import com.photowatermark.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 图片处理服务类，负责处理图片并添加水印
 */
public class ImageProcessor {
    private static final Logger logger = LogManager.getLogger(ImageProcessor.class);
    private final ExifReader exifReader = new ExifReader();
    private static final SimpleDateFormat FALLBACK_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 处理指定配置中的图片
     * @param config 水印配置
     * @return 成功处理的图片数量
     */
    public int processImages(WatermarkConfig config) {
        File inputFile = new File(config.getImagePath());
        List<File> imageFiles = new ArrayList<>();

        // 检查输入路径是文件还是目录
        if (inputFile.isDirectory()) {
            imageFiles.addAll(FileUtil.getImageFilesInDirectory(inputFile));
        } else if (inputFile.isFile() && FileUtil.isImageFile(inputFile)) {
            imageFiles.add(inputFile);
        } else {
            logger.error("无效的图片路径: {}", config.getImagePath());
            throw new IllegalArgumentException("无效的图片路径: " + config.getImagePath());
        }

        if (imageFiles.isEmpty()) {
            logger.info("没有找到可处理的图片文件");
            return 0;
        }

        logger.info("找到 {} 张图片待处理", imageFiles.size());

        // 创建输出目录
        File outputDir = new File(inputFile.getParentFile(), inputFile.getName() + "_watermark");
        if (inputFile.isDirectory()) {
            outputDir = new File(inputFile.getAbsolutePath() + "_watermark");
        }
        FileUtil.createDirectory(outputDir);

        int successCount = 0;

        // 处理每张图片
        for (File file : imageFiles) {
            try {
                processSingleImage(file, outputDir, config);
                successCount++;
            } catch (Exception e) {
                logger.error("处理图片失败: {}", file.getAbsolutePath(), e);
            }
        }

        return successCount;
    }

    /**
     * 处理单张图片
     * @param inputFile 输入图片文件
     * @param outputDir 输出目录
     * @param config 水印配置
     * @throws ImageProcessException 当处理图片失败时抛出
     */
    private void processSingleImage(File inputFile, File outputDir, WatermarkConfig config) throws ImageProcessException {
        try {
            // 读取图片
            BufferedImage image = ImageIO.read(inputFile);
            if (image == null) {
                throw new ImageProcessException("无法读取图片文件: " + inputFile.getAbsolutePath());
            }

            // 创建ImageInfo对象
            ImageInfo imageInfo = new ImageInfo(inputFile);

            // 读取拍摄日期
            try {
                Date shootDate = exifReader.readShootDate(inputFile);
                imageInfo.setShootDate(shootDate);

                // 格式化日期作为水印文本
                String watermarkText = exifReader.formatDateForWatermark(shootDate);
                if (watermarkText == null) {
                    // 如果没有EXIF日期信息，使用当前日期作为备选
                    watermarkText = FALLBACK_DATE_FORMAT.format(new Date());
                    logger.warn("使用当前日期作为水印: {}", watermarkText);
                }
                imageInfo.setWatermarkText(watermarkText);

            } catch (ExifReadException e) {
                // 读取EXIF失败时，使用当前日期作为水印
                String watermarkText = FALLBACK_DATE_FORMAT.format(new Date());
                imageInfo.setWatermarkText(watermarkText);
                logger.warn("读取EXIF信息失败，使用当前日期作为水印: {}", watermarkText);
            }

            // 添加水印
            BufferedImage watermarkedImage = addWatermark(image, imageInfo.getWatermarkText(), config);

            // 保存处理后的图片
            String outputFileName = outputDir.getAbsolutePath() + File.separator + inputFile.getName();
            String formatName = FileUtil.getImageFormat(inputFile);
            if (!ImageIO.write(watermarkedImage, formatName, new File(outputFileName))) {
                throw new ImageProcessException("保存图片失败: " + outputFileName);
            }

            logger.info("成功处理并保存图片: {}", outputFileName);

        } catch (IOException e) {
            throw new ImageProcessException("处理图片失败: " + inputFile.getAbsolutePath(), e);
        }
    }

    /**
     * 在图片上添加水印
     * @param image 原始图片
     * @param watermarkText 水印文本
     * @param config 水印配置
     * @return 添加水印后的图片
     */
    private BufferedImage addWatermark(BufferedImage image, String watermarkText, WatermarkConfig config) {
        // 创建一个可编辑的图片副本
        BufferedImage watermarkedImage = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        // 获取Graphics2D对象进行绘制
        Graphics2D g2d = watermarkedImage.createGraphics();

        // 绘制原始图片
        g2d.drawImage(image, 0, 0, null);

        // 设置水印属性
        g2d.setColor(config.getColor());
        g2d.setFont(new Font("Arial", Font.BOLD, config.getFontSize()));

        // 计算水印位置
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(watermarkText);
        int textHeight = fontMetrics.getHeight();

        int x = 0;
        int y = 0;
        int padding = 20; // 边距

        // 根据位置参数计算水印坐标
        String position = config.getPosition();
        switch (position) {
            case "top-left":
                x = padding;
                y = padding + textHeight;
                break;
            case "top-center":
                x = (image.getWidth() - textWidth) / 2;
                y = padding + textHeight;
                break;
            case "top-right":
                x = image.getWidth() - textWidth - padding;
                y = padding + textHeight;
                break;
            case "center-left":
                x = padding;
                y = (image.getHeight() + textHeight) / 2;
                break;
            case "center":
                x = (image.getWidth() - textWidth) / 2;
                y = (image.getHeight() + textHeight) / 2;
                break;
            case "center-right":
                x = image.getWidth() - textWidth - padding;
                y = (image.getHeight() + textHeight) / 2;
                break;
            case "bottom-left":
                x = padding;
                y = image.getHeight() - padding;
                break;
            case "bottom-center":
                x = (image.getWidth() - textWidth) / 2;
                y = image.getHeight() - padding;
                break;
            case "bottom-right":
            default:
                x = image.getWidth() - textWidth - padding;
                y = image.getHeight() - padding;
                break;
        }

        // 绘制水印文本
        g2d.drawString(watermarkText, x, y);

        // 释放资源
        g2d.dispose();

        return watermarkedImage;
    }
}