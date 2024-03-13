package com.dexcode.maker.generator.main;

/**
 * @author Tom Smile
 * @version 1.0
 * @description: 代码生成器の压缩包
 * @date 2024/3/12 20:32
 */

public class ZipGenerator extends GenerateTemplate {

    @Override
    protected String buildDist(String outputPath, String sourceCopyDestPath, String jarPath, String shellOutputFilePath) {
        // 生成精简版程序
        String distPath = super.buildDist(outputPath, sourceCopyDestPath, jarPath, shellOutputFilePath);
        // 压缩包
        return super.buildZip(distPath);
    }
}
