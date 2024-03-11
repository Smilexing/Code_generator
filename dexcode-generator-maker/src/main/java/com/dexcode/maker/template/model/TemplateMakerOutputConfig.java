package com.dexcode.maker.template.model;

/**
 * @author Tom Smile
 * @version 1.0
 * @description: TODO
 * @date 2024/3/11 20:27
 */

import lombok.Data;

/**
 * 模版制作输出配置
 */
@Data
public class TemplateMakerOutputConfig {

    // 从未分组文件中移除组内的同名文件
    private boolean removeGroupFilesFromRoot = true;
}

