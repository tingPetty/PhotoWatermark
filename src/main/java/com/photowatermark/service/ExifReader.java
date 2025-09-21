package com.photowatermark.service;

import com.photowatermark.exception.ExifReadException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
// import org.apache.commons.imaging.common.IImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * EXIF信息读取服务类，用于从图片中提取EXIF信息
 */
public class ExifReader {
    private static final Logger logger = LogManager.getLogger(ExifReader.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private static final SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 从图片文件中读取拍摄日期
     * @param file 图片文件
     * @return 拍摄日期，如果无法读取则返回null
     * @throws ExifReadException 当读取EXIF信息失败时抛出
     */
    public Date readShootDate(File file) throws ExifReadException {
        try {
             // 使用Apache Commons Imaging读取图片元数据（注意：返回的是 ImageMetadata）
            ImageMetadata metadata = Imaging.getMetadata(file);

            if (metadata instanceof JpegImageMetadata) {
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

                // 尝试读取拍摄日期
                TiffField field = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                if (field == null) {
                    // 如果没有拍摄日期，尝试读取修改日期
                    // field = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_DATE_TIME);
                    field = jpegMetadata.findEXIFValue(TiffTagConstants.TIFF_TAG_DATE_TIME);
                }
                if (field == null) {
                    // 最后尝试读取数字化日期
                    field = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED);
                }

                if (field != null) {
                    String dateString = field.getStringValue();
                    try {
                        return DATE_FORMAT.parse(dateString);
                    } catch (ParseException e) {
                        logger.warn("解析日期字符串失败: {}", dateString, e);
                    }
                }
            }

            logger.warn("无法从图片中读取EXIF日期信息: {}", file.getAbsolutePath());
            return null;

        } catch (ImageReadException | IOException e) {
            throw new ExifReadException("读取图片EXIF信息失败: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * 将日期格式化为水印文本格式
     * @param date 日期对象
     * @return 格式化后的日期字符串
     */
    public String formatDateForWatermark(Date date) {
        if (date == null) {
            return null;
        }
        return OUTPUT_FORMAT.format(date);
    }
}