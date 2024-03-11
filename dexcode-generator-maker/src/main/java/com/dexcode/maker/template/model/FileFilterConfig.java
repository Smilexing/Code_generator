package com.dexcode.maker.template.model;

/**
 * @author Tom Smile
 * @version 1.0
 * @description: TODO
 * @date 2024/3/11 9:19
 */

import lombok.Builder;
import lombok.Data;

/**
 * 文件过滤配置
 */
@Data
@Builder
public class FileFilterConfig {

    /**
     * 过滤范围
     */
    private String range;

    /**
     * 过滤规则
     */
    private String rule;

    /**
     * 过滤值
     */
    private String value;

}
