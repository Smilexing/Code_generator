package com.yupi.web.model.dto.generator;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Tom Smile
 * @version 1.0
 * @description: TODO
 * @date 2024/3/21 11:09
 */
@Data
public class GeneratorCacheRequest implements Serializable {
    /**
     * 生成器的 id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
