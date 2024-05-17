package com.net.common.enums;

import java.util.Arrays;
import java.util.List;

public enum FileTypeEnum {
    IMAGE("1000", Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "tiff", "tif", "psd", "raw", "svg", "ico")),
    VIDEO("2000", Arrays.asList("mp4", "avi", "mov", "wmv", "m4v", "flv")),
    DOCUMENT("3000", Arrays.asList("txt", "doc", "docx", "pdf", "xls", "xlsx", "ppt", "pptx", "md", "csv", "rtf")),
    MUSIC("4000", Arrays.asList("mp3", "wav", "wma", "ape", "flac", "aac", "ogg")),
    ARCHIVE("5000", Arrays.asList("zip", "rar", "7z", "tar", "gz", "bz2", "xz"));

    private final String typeCode;
    private final List<String> extensions;

    FileTypeEnum(String typeCode, List<String> extensions) {
        this.typeCode = typeCode;
        this.extensions = extensions;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public static FileTypeEnum getFileTypeEnumByCode(String code) {
        for (FileTypeEnum FileTypeEnum : FileTypeEnum.values()) {
            if (FileTypeEnum.getTypeCode().equals(code)) {
                return FileTypeEnum;
            }
        }
        return null;
    }

    public static FileTypeEnum getFileTypeEnumByExtension(String extension) {
        for (FileTypeEnum FileTypeEnum : FileTypeEnum.values()) {
            if (FileTypeEnum.getExtensions().contains(extension)) {
                return FileTypeEnum;
            }
        }
        return null;
    }
}
