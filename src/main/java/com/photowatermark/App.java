package com.photowatermark;

import com.photowatermark.model.WatermarkConfig;
import com.photowatermark.service.ImageProcessor;
import com.photowatermark.util.CommandLineParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * 程序入口类
 */
public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        try {
            // 解析命令行参数
            CommandLineParser parser = new CommandLineParser();
            WatermarkConfig config = parser.parse(args);

            if (config == null) {
                return; // 显示帮助信息后退出
            }

            logger.info("开始处理图片...");
            logger.info("图片路径: {}", config.getImagePath());
            logger.info("水印字体大小: {}", config.getFontSize());
            logger.info("水印颜色: {}", config.getColor());
            logger.info("水印位置: {}", config.getPosition());

            // 处理图片
            ImageProcessor processor = new ImageProcessor();
            int successCount = processor.processImages(config);

            logger.info("图片处理完成，成功处理 {} 张图片", successCount);

        } catch (Exception e) {
            logger.error("程序执行出错: {}", e.getMessage(), e);
            System.err.println("程序执行出错: " + e.getMessage());
        }
    }
}