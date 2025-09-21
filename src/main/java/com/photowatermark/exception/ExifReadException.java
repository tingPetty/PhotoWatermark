package com.photowatermark.exception;

/**
 * EXIF读取异常类，用于表示读取图片EXIF信息过程中的错误
 */
public class ExifReadException extends RuntimeException {
    public ExifReadException(String message) {
        super(message);
    }

    public ExifReadException(String message, Throwable cause) {
        super(message, cause);
    }
}