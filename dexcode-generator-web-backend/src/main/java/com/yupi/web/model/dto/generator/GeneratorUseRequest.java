package com.yupi.web.model.dto.generator;

/**
 * @author Tom Smile
 * @version 1.0
 * @description: TODO
 * @date 2024/3/13 20:03
 */

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 使用代码生成器请求
 */
@Data
public class GeneratorUseRequest implements Serializable {

    /**
     * 生成器的 id
     */
    private Long id;

    /**
     * 数据模型
     */
    Map<String, Object> dataModel;

    private static final long serialVersionUID = 1L;
}
