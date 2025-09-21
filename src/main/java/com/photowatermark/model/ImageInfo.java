package com.photowatermark.model;

import java.io.File;
import java.util.Date;

/**
 * 图片信息类，存储图片的基本信息
 */
public class ImageInfo {
    private File file;
    private Date shootDate;
    private String watermarkText;

    public ImageInfo(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Date getShootDate() {
        return shootDate;
    }

    public void setShootDate(Date shootDate) {
        this.shootDate = shootDate;
    }

    public String getWatermarkText() {
        return watermarkText;
    }

    public void setWatermarkText(String watermarkText) {
        this.watermarkText = watermarkText;
    }
}