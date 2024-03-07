package com.dexcode.generator.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

public class MetaManager {

    // volatile，并发编程中常用的关键字，确保多线程环境下的内存可见性，这样meta一旦被修改，所有内存都能看见
    private static volatile com.dexcode.maker.meta.Meta meta;

    public static com.dexcode.maker.meta.Meta getMetaObject() {
        if (meta == null) {
            // 加锁
            synchronized (MetaManager.class) {
                if (meta == null) {
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static com.dexcode.maker.meta.Meta initMeta() {
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        com.dexcode.maker.meta.Meta newMeta = JSONUtil.toBean(metaJson, com.dexcode.maker.meta.Meta.class);
        // todo 校验配置文件，处理默认值
        return newMeta;
    }
}
