package com.dexcode.maker.template.enums;

/**
 * @author Tom Smile
 * @version 1.0
 * @description: TODO
 * @date 2024/3/11 9:32
 */

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 文件过滤范围枚举
 */
@Getter
public enum FileFilterRangeEnum {

    FILE_NAME("文件名称", "fileName"),
    FILE_CONTENT("文件内容", "fileContent");

    private final String text;

    private final String value;

    FileFilterRangeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static FileFilterRangeEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (FileFilterRangeEnum anEnum : FileFilterRangeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
