package com.photowatermark.model;

import java.awt.Color;

/**
 * 水印配置类，存储水印的相关参数
 */
public class WatermarkConfig {
    private String imagePath;
    private int fontSize;
    private Color color;
    private String position;

    public WatermarkConfig() {
        // 默认配置
        this.fontSize = 30;
        this.color = Color.WHITE;
        this.position = "bottom-right";
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}