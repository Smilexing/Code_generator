package com.dexcode.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dexcode.maker.meta.Meta;
import com.dexcode.maker.meta.enums.FileGenerateTypeEnum;
import com.dexcode.maker.meta.enums.FileTypeEnum;
import com.dexcode.maker.template.enums.FileFilterRangeEnum;
import com.dexcode.maker.template.enums.FileFilterRuleEnum;
import com.dexcode.maker.template.model.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 第七期：模板制作工具
 */
public class TemplateMaker {

    /**
     * 制作模板(重载方法）
     *
     * @param templateMakerConfig
     * @return
     */
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig) {
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig templateMakerOutputConfig = templateMakerConfig.getOutputConfig();
        Long id = templateMakerConfig.getId();

        return makeTemplate(meta, originProjectPath, templateMakerFileConfig, templateMakerModelConfig,templateMakerOutputConfig, id);
    }

        /**
         * 制作模板（分步能力制作）
         *
         * @param newMeta
         * @param originProjectPath
         * @param templateMakerFileConfig
         * @param templateMakerModelConfig
         * @param id
         * @return
         */
    public static long makeTemplate(Meta newMeta, String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig,
                                    TemplateMakerModelConfig templateMakerModelConfig,
                                    TemplateMakerOutputConfig templateMakerOutputConfig,Long id) {
        {
            // 没有 id 则生成
            if (id == null) {
                id = IdUtil.getSnowflakeNextId();
            }

            // 复制目录
            String projectPath = System.getProperty("user.dir");
            String tempDirPath = projectPath + File.separator + ".temp";
            String templatePath = tempDirPath + File.separator + id;

            // 判断：是否为首次制作
            // 目录不存在，则是首次制作，复制目录
            if (!FileUtil.exist(templatePath)) {
                FileUtil.mkdir(templatePath);
                FileUtil.copy(originProjectPath, templatePath, true);
            }

            // 一、输入信息
            // 输入文件信息，获取项目根目录
            //            String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();


            String sourceRootPath = FileUtil.loopFiles(new File(templatePath), 1, null)
                    .stream()
                    .filter(File::isDirectory)
                    .findFirst()
                    .orElseThrow(RuntimeException::new)
                    .getAbsolutePath();



            // 注意 win 系统需要对路径进行转义
            sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
            List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();

            List<Meta.FileConfig.FileInfo> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);


            List<Meta.ModelConfig.ModelInfo> newModelInfoList = getModelInfoList(templateMakerModelConfig);


            // 三、生成配置文件
            String metaOutputPath = templatePath + File.separator + "meta.json";

            // 如果已有 meta 文件，说明不是第一次制作，则在 meta 基础上进行修改
            if (FileUtil.exist(metaOutputPath)) {
                Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
                BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
                newMeta = oldMeta;

                // 1. 追加配置参数
                List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
                fileInfoList.addAll(newFileInfoList);
                List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
                modelInfoList.addAll(newModelInfoList);

                // 配置去重
                newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
                newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
            } else {
                // 1. 构造配置参数
                Meta.FileConfig fileConfig = new Meta.FileConfig();
                newMeta.setFileConfig(fileConfig);
                fileConfig.setSourceRootPath(sourceRootPath);
                List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
                fileConfig.setFiles(fileInfoList);
                fileInfoList.addAll(newFileInfoList);

                Meta.ModelConfig modelConfig = new Meta.ModelConfig();
                newMeta.setModelConfig(modelConfig);
                List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
                modelConfig.setModels(modelInfoList);
                modelInfoList.addAll(newModelInfoList);
            }

            // 2. 额外的输出配置
            if (templateMakerOutputConfig != null) {
                // 文件外层和分组去重
                if (templateMakerOutputConfig.isRemoveGroupFilesFromRoot()) {
                    List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
                    newMeta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFromRoot(fileInfoList));
                }
            }

// 3. 输出元信息文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
            return id;
        }
    }

    private static List<Meta.FileConfig.FileInfo> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig,
                                                                    TemplateMakerModelConfig templateMakerModelConfig,
                                                                    String sourceRootPath) {

        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
            //新增：非空校验
        if (templateMakerFileConfig == null) {
            return newFileInfoList;
        }

        // 二、生成文件模板
        // 遍历输入文件
        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();
        if (CollUtil.isEmpty(fileConfigInfoList)) {
            return newFileInfoList;
        }
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileConfigInfoList) {
            String inputFilePath = fileInfoConfig.getPath();

            // 如果填的是相对路径，要改为绝对路径
            if (!inputFilePath.startsWith(sourceRootPath)) {
                inputFilePath = sourceRootPath + File.separator + inputFilePath;
            }

            // 获取过滤后的文件列表（不会存在目录）
            List<File> fileList = FileFilter.doFilter(inputFilePath, fileInfoConfig.getFilterConfigList());
            // 不处理已生成的 FTL 模板文件
            fileList = fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl"))
                    .collect(Collectors.toList());
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath, file,fileInfoConfig);
                newFileInfoList.add(fileInfo);
            }
        }


        // 如果是文件组，将上面的信息都放进同一分组下
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (fileGroupConfig != null) {
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();

            // 新增分组配置
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            // 文件全放到一个分组内
            groupFileInfo.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }
        return newFileInfoList;
    }

    private static List<Meta.ModelConfig.ModelInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        // 本次新增的模型配置列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();
        if (templateMakerModelConfig == null) {
            return newModelInfoList;
        }

        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        if (CollUtil.isEmpty(models)) {
            return newModelInfoList;
        }
        // 处理模型信息
        // - 转换为配置接受的 ModelInfo 对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream().map(modelInfoConfig -> {
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelInfoConfig, modelInfo);
            return modelInfo;
        }).collect(Collectors.toList());



        // - 如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {

            // 复制变量
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig, groupModelInfo);

            // 模型全放到一个分组内
            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);
        } else {
            // 不分组，添加所有的模型信息到列表
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }


    /**
     * 制作文件模板
     *
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @param inputFile
     * @return
     */

    private static Meta.FileConfig.FileInfo makeFileTemplate(TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath,
                                                             File inputFile,TemplateMakerFileConfig.FileInfoConfig fileInfoConfig) {
        // 要挖坑的文件绝对路径（用于制作模板）
        // 注意 win 系统需要对路径进行转义   文件 --> 绝对路径 --> 相对路径
        String fileInputAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 文件输入输出相对路径（用于生成配置）
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";

        // 二、使用字符串替换，生成模板文件
        String fileContent = null;

        // 如果已有.ftl文件，则为非首次制作，给一个判断标记_1
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        if (hasTemplateFile) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            // 读取原文件
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        // 已经拿到要替换的全文本（需要最新替换的内容）
        String newFileContent = fileContent;
        String replacement;
        // 支持多个模型：对于同一个文件的内容，遍历模型进行多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            String fieldName = modelInfoConfig.getFieldName();
            // 不是分组
            if (modelGroupConfig == null) {
                replacement = String.format("${%s}", fieldName);
            } else {
                // 是分组
                String groupKey = modelGroupConfig.getGroupKey();
                // 注意挖坑要多一个层级
                replacement = String.format("${%s.%s}", groupKey, fieldName);
            }
            // 多次替换
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }

        // 文件配置信息
        //将文件配置 fileInfo 的构造提前，无论是新增还是修改元信息都能使用该对象
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();

        // 修复3：注意文件的输入路径和输出路径要交换，元信息中 .ftl 文件是输入文件
        // 注意文件输入路径要和输出路径反转
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setCondition(fileInfoConfig.getCondition());
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 默认文件生成类型为 动态
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 修复1：既要hasTemplateFile为false 且和原文件一致，没有挖坑，则为静态生成
        // 判断标记_2：是否修改了文件内容 ---》原逻辑是只要和源文件是一致的，则视作没有挖坑，则为静态生成
        boolean contentEquals = newFileContent.equals(fileContent);
        if (!hasTemplateFile) {
            if (contentEquals) {
                // 输入路径
                fileInfo.setInputPath(fileInputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            } else {
                // 文件有挖坑，动态生成（首次制作）
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
            }
        } else if (!contentEquals) {
            // 有模板文件，且增加了新坑，动态生成（非首次制作）
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }
        return fileInfo;
    }

        /**
         * 模型去重
         *
         * @param modelInfoList
         * @return
         */
        private static List<Meta.ModelConfig.ModelInfo> distinctModels
        (List < Meta.ModelConfig.ModelInfo > modelInfoList) {
            // 策略：同分组内模型 merge，不同分组保留

            // 1. 有分组的，以组为单位划分
            Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList
                    .stream()
                    .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                    .collect(
                            Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey)
                    );


            // 2. 同组内的模型配置合并
            // 保存每个组对应的合并后的对象 map
            Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>();
            for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
                List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
                List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                        .flatMap(modelInfo -> modelInfo.getModels().stream())
                        .collect(
                                Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                        ).values());

                // 使用新的 group 配置
                Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
                newModelInfo.setModels(newModelInfoList);
                String groupKey = entry.getKey();
                groupKeyMergedModelInfoMap.put(groupKey, newModelInfo);
            }

            // 3. 将模型分组添加到结果列表
            List<Meta.ModelConfig.ModelInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());

            // 4. 将未分组的模型添加到结果列表
            List<Meta.ModelConfig.ModelInfo> noGroupModelInfoList = modelInfoList.stream().filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                    .collect(Collectors.toList());
            resultList.addAll(new ArrayList<>(noGroupModelInfoList.stream()
                    .collect(
                            Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)
                    ).values()));
            return resultList;
        }

        /**
         * 文件去重
         *
         * @param fileInfoList
         * @return
         */
        private static List<Meta.FileConfig.FileInfo> distinctFiles(List < Meta.FileConfig.FileInfo > fileInfoList) {
            // 策略：同分组内文件 merge，不同分组保留

            // 1. 有分组的，以组为单位划分
            // {"groupKey": "a", "files": [1, 2]}, {"groupKey": "a", "files": [2, 3]}, {"groupKey": "b", "files": [4, 5]}
            // {"groupKey": "a", "files": [[1, 2], [2, 3]]}, {"groupKey": "b", "files": [[4, 5]]}
            Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList
                    .stream()
                    .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                    .collect(
                            Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey)
                    );


            // 2. 同组内的文件配置合并
            // {"groupKey": "a", "files": [[1, 2], [2, 3]]}
            // {"groupKey": "a", "files": [1, 2, 2, 3]}
            // {"groupKey": "a", "files": [1, 2, 3]}
            // 保存每个组对应的合并后的对象 map
            Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
            for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
                List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();

                List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                        .flatMap(fileInfo -> fileInfo.getFiles().stream())
                        .collect(
                                Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)
                        ).values());

                // 使用新的 group 配置
                Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
                newFileInfo.setFiles(newFileInfoList);
                String groupKey = entry.getKey();
                groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
            }

            // 3. 将文件分组添加到结果列表
            List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

            // 4. 将未分组的文件添加到结果列表
            List<Meta.FileConfig.FileInfo> noGroupFileInfoList = fileInfoList.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                    .collect(Collectors.toList());
            resultList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                    .collect(
                            Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)
                    ).values()));
            return resultList;
        }
    }


