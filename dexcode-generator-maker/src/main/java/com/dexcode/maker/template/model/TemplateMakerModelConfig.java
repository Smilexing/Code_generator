package com.dexcode.maker.template.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dexcode.maker.meta.Meta;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Tom Smile
 * @version 1.0
 * @description: TODO
 * @date 2024/3/11 13:15
 */
@Data
public class TemplateMakerModelConfig {

    private List<ModelInfoConfig> models;

    private ModelGroupConfig modelGroupConfig;

    @NoArgsConstructor
    @Data
    public static class ModelInfoConfig {

        private String fieldName;

        private String type;

        private String description;

        private Object defaultValue;

        private String abbr;

        // 用于替换哪些文本
        private String replaceText;
    }

    @Data
    public static class ModelGroupConfig {

        private String condition;

        private String groupKey;

        private String groupName;

        private String type;

        private String description;
    }
}

