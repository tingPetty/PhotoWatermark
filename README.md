# PhotoWatermark

PhotoWatermark 是一个基于 Java 的命令行程序，用于自动为图片添加基于拍摄日期的水印。该程序将读取图片的 EXIF 信息中的拍摄时间，提取年月日作为水印文本，并根据用户设定的字体大小、颜色和位置，将水印绘制到图片上，最终保存为新的图片文件。

## 功能特点

- 自动读取图片的 EXIF 信息，提取拍摄日期作为水印
- 支持自定义水印的字体大小、颜色和位置
- 支持处理单个图片文件或整个目录中的所有图片
- 处理后的图片保存在原目录的 `_watermark` 子目录中
- 完善的错误处理和日志输出

## 技术栈

- Java 8
- Maven
- Apache Commons Imaging（读取EXIF信息）
- Apache Commons CLI（解析命令行参数）
- Log4j2（日志管理）

## 构建项目

### 前提条件

- 已安装 JDK 8 或更高版本
- 已安装 Maven

### 构建命令

```bash
mvn clean package
```

构建成功后，可执行的 JAR 文件将生成在 `target` 目录下，文件名为 `PhotoWatermark-1.0-SNAPSHOT-jar-with-dependencies.jar`。

## 使用方法

```bash
java -jar PhotoWatermark-1.0-SNAPSHOT-jar-with-dependencies.jar <图片路径> [选项]
```

### 参数说明

- `<图片路径>`：必需参数，指定要处理的图片文件或目录路径
- `--size, -s`：可选参数，指定水印字体大小（默认值：30）
- `--color, -c`：可选参数，指定水印颜色（默认值：white）
- `--position, -p`：可选参数，指定水印位置（默认值：bottom-right）
- `--help, -h`：显示帮助信息

### 位置选项

位置参数支持以下值：
- `top-left`：左上角
- `top-center`：上居中
- `top-right`：右上角
- `center-left`：左居中
- `center`：正中央
- `center-right`：右居中
- `bottom-left`：左下角
- `bottom-center`：下居中
- `bottom-right`：右下角

### 颜色选项

颜色参数支持以下值：
- `black`：黑色
- `white`：白色
- `red`：红色
- `blue`：蓝色
- `green`：绿色
- 也可支持十六进制颜色码，如 `#FF0000` 表示红色

## 使用示例

### 处理单个图片

```bash
java -jar PhotoWatermark-1.0-SNAPSHOT-jar-with-dependencies.jar photo.jpg
```

### 处理整个目录

```bash
java -jar PhotoWatermark-1.0-SNAPSHOT-jar-with-dependencies.jar photos/
```

### 自定义水印设置

```bash
java -jar PhotoWatermark-1.0-SNAPSHOT-jar-with-dependencies.jar photo.jpg --size 40 --color red --position bottom-center
```

## 注意事项

- 程序将在原目录的同级目录下创建一个名为 `<原目录名>_watermark` 的新目录，用于保存处理后的图片
- 如果图片没有EXIF信息或无法读取EXIF信息，程序将使用当前日期作为水印
- 支持的图片格式：JPG、PNG、GIF、BMP、TIFF、WebP

## 许可证

本项目使用 MIT 许可证