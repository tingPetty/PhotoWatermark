package com.photowatermark.util;

import com.photowatermark.model.WatermarkConfig;
import org.apache.commons.cli.*;

import java.awt.Color;

/**
 * 命令行参数解析工具类，用于解析用户提供的命令行参数
 */
public class CommandLineParser {
    /**
     * 解析命令行参数并构建水印配置
     * @param args 命令行参数数组
     * @return 水印配置对象，如果解析失败则返回null
     */
    public WatermarkConfig parse(String[] args) {
        // 创建选项对象
        Options options = createOptions();

        try {
            // 创建命令行解析器
            DefaultParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            // 检查是否显示帮助信息
            if (cmd.hasOption("help")) {
                printHelp(options);
                return null;
            }

            // 获取图片路径（必需参数）
            String[] remainingArgs = cmd.getArgs();
            if (remainingArgs.length == 0) {
                System.err.println("错误: 请提供图片文件路径");
                printHelp(options);
                return null;
            }

            // 创建并配置WatermarkConfig对象
            WatermarkConfig config = new WatermarkConfig();
            config.setImagePath(remainingArgs[0]);

            // 处理可选参数
            if (cmd.hasOption("size")) {
                try {
                    int fontSize = Integer.parseInt(cmd.getOptionValue("size"));
                    if (fontSize <= 0) {
                        System.err.println("警告: 字体大小必须为正数，使用默认值");
                    } else {
                        config.setFontSize(fontSize);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("警告: 无效的字体大小，使用默认值");
                }
            }

            if (cmd.hasOption("color")) {
                String colorStr = cmd.getOptionValue("color").toLowerCase();
                Color color = parseColor(colorStr);
                if (color != null) {
                    config.setColor(color);
                } else {
                    System.err.println("警告: 无效的颜色值，使用默认值");
                }
            }

            if (cmd.hasOption("position")) {
                String position = cmd.getOptionValue("position").toLowerCase();
                if (isValidPosition(position)) {
                    config.setPosition(position);
                } else {
                    System.err.println("警告: 无效的位置值，使用默认值");
                }
            }

            return config;

        } catch (ParseException e) {
            System.err.println("解析命令行参数错误: " + e.getMessage());
            printHelp(options);
            return null;
        }
    }

    /**
     * 创建命令行选项
     * @return 选项对象
     */
    private Options createOptions() {
        Options options = new Options();

        // 帮助选项
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("显示帮助信息")
                .build());

        // 字体大小选项
        options.addOption(Option.builder("s")
                .longOpt("size")
                .hasArg()
                .argName("大小")
                .desc("设置水印字体大小（默认: 30）")
                .build());

        // 颜色选项
        options.addOption(Option.builder("c")
                .longOpt("color")
                .hasArg()
                .argName("颜色")
                .desc("设置水印颜色（默认: white，可选值: black, white, red, blue, green 或十六进制颜色码）")
                .build());

        // 位置选项
        options.addOption(Option.builder("p")
                .longOpt("position")
                .hasArg()
                .argName("位置")
                .desc("设置水印位置（默认: bottom-right，可选值: top-left, top-center, top-right, center-left, center, center-right, bottom-left, bottom-center, bottom-right）")
                .build());

        return options;
    }

    /**
     * 解析颜色字符串
     * @param colorStr 颜色字符串
     * @return Color对象，如果解析失败则返回null
     */
    private Color parseColor(String colorStr) {
        switch (colorStr) {
            case "black":
                return Color.BLACK;
            case "white":
                return Color.WHITE;
            case "red":
                return Color.RED;
            case "blue":
                return Color.BLUE;
            case "green":
                return Color.GREEN;
            default:
                // 尝试解析十六进制颜色码
                if (colorStr.startsWith("#") && (colorStr.length() == 7 || colorStr.length() == 9)) {
                    try {
                        if (colorStr.length() == 7) {
                            return Color.decode(colorStr);
                        } else {
                            // 处理带透明度的十六进制颜色
                            int r = Integer.parseInt(colorStr.substring(1, 3), 16);
                            int g = Integer.parseInt(colorStr.substring(3, 5), 16);
                            int b = Integer.parseInt(colorStr.substring(5, 7), 16);
                            int a = Integer.parseInt(colorStr.substring(7, 9), 16);
                            return new Color(r, g, b, a);
                        }
                    } catch (NumberFormatException e) {
                        // 解析失败
                    }
                }
                return null;
        }
    }

    /**
     * 检查位置值是否有效
     * @param position 位置字符串
     * @return 如果位置有效则返回true，否则返回false
     */
    private boolean isValidPosition(String position) {
        switch (position) {
            case "top-left":
            case "top-center":
            case "top-right":
            case "center-left":
            case "center":
            case "center-right":
            case "bottom-left":
            case "bottom-center":
            case "bottom-right":
                return true;
            default:
                return false;
        }
    }

    /**
     * 打印帮助信息
     * @param options 选项对象
     */
    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar PhotoWatermark.jar <图片路径> [选项]", options);
    }
}