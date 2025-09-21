package com.photowatermark.exception;

/**
 * 图片处理异常类，用于表示图片处理过程中的错误
 */
public class ImageProcessException extends RuntimeException {
    public ImageProcessException(String message) {
        super(message);
    }

    public ImageProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}