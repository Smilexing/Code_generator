# 鱼籽 - 定制化代码生成项目（持续更新）

> 学习教程来源，程序员鱼皮的开源项目：https://github.com/liyupi/yuzi-generator
>
> 学习笔记分享在语雀知识库，目前只有编程导航知识星球的小伙伴可以查看：https://wx.zsxq.com/dweb2/index/footprint/841184212441112
>
> 也欢迎互联网上的朋友们多提issue，大家一起交流进步！

## 一、项目介绍

基于 React + Spring Boot + Vert.x 响应式编程的 **定制化代码生成项目**

完整的项目分为 **3个阶段**：

1. 制作属于自己的 `本地代码生成器`，是一个基于命令行的脚手架，能够根据用户的交互式输入快速生成特定代码
2. 开发 `制作代码生成器的工具`，比如自己有一段常用的代码，使用该工具可以快速地把项目制作为代码生成器（我的理解：就是一套自用的模板，ex.我**刷算法题**用的代码 有LeetCode题目要求、Main方法、编写的算法本身），大大提升工作效率
3. 开发 `在线代码生成器平台`，在平台上制作发布自己的代码生成器，还可以在线使用别人的代码生成器，甚至可以共享协作

## 二、学习进度记录

### 第八期、模板项目生成（第二阶段🎉🎉）

当前目录说明：

- `dexcode-generator-maker`目录下：继续 template目录下的代码编写
- 完成第二阶段的开发目标 —— 制作 Spring Boot 项目模板の代码生成器，并测试文件和代码的生成情况

本期内容：

- 模板制作工具 - Bug修复
- 模板制作工具 - 参数封装 - 易用性优化
- `第六期`提出的7个需求逐一实现
- 测试成果

### 第七期、模板の制作工具

当前目录说明：

- `dexcode-generator-maker`目录下：新建template目录，存放本期代码模板制作的代码

本期内容，模板制作工具：

- 需求分析
- 核心设计
- 基础功能实现（工作空间隔离、分步制作能力）
- 更多功能实现
  - 单次制作多个模板文件
  - 文件过滤
  - 文件分组
  - 模型分组

### 第六期、配置能力增强

当前目录说明：

- 围绕`dexcode-generator-demo-projects`目录下的`springboot-init`初始化模板【精简版】，进行制作工具的7大通用能力开发
- 本期除“替换生成的代码包名”的需求，均已开发完毕

本期内容：

- 生成目标 - Spring Boot模板项目介绍
- 工具通用能力分析
- 配置能力增强开发
  1. 一个参数对应某个文件是否生成
  2. 一个参数对应多个文件是否生成
  3. 一个参数控制多处代码修改以及文件是否生成
  4. 定义一组参数，控制代码修改或文件生成
  5. 定义一组参数，通过其他开关参数控制是否需要输入该参数

### 第五期、制作工具优化

当前目录说明：

- `dexcode-generator-maker`目录下：围绕 `generator.MainGenerator`生成代码生成器的流程 和本期新建的`meta.MetaValidator` 元信息校验类，进行项目优化

本期内容：

- 可移植性优化
- 新增项目介绍文件、制作精简版代码生成器
- 健壮性优化（涉及圈复杂度优化）
- 可扩展性优化

### 第四期、制作工具开发

当前目录说明：

- `dexcode-generator-maker`目录下：围绕 `generator`下的`MainGenerator`，**制作生成**代码生成器**の工具**，并具备**一键**打jar包、创建脚本工具等功能

本期内容：

- 元信息定义（**双检锁**单例模式进行读取）
- 制作工具开发

### 第三期、命令行开发（第一阶段🎉）

当前目录说明：

- `dexcode-generator-basic`目录下新增`cli`包，存放本期的Picocli示例代码、命令模式demo、代码生成器命令行开发代码

本期内容：

- Java 命令行开发方案
- Picocli 命令行框架入门
- 命令模式讲解
- **Picocli 命令行代码生成器开发** （已解决反射-强制交互 & 中文乱码问题）

### 第二期、代码生成

当前目录说明：

- 项目根目录`dexcode-generator`，便于后续不同阶段的代码管理

- `dexcode-generator-demo-projects` 存放了本项目需要用到的示例代码

- `dexcode-generator-basic` 维护项目第一阶段的开发

本期内容：

- 学习静态文件生成的两种方式
  - 使用现有的hutool工具库
  - 自己写代码进行递归复制
- 借助FreeMarker模板引擎实现动态文件生成
- 并将两者结合，统一写在了`com.dexcode.generator` 包下的`MainGenerator`类中，实现了Java ACM示例模板代码的本地定制生成

### 第一期、项目总览

跟着鱼皮的视频，了解了本项目的项目介绍、项目背景，做了需求分析和技术选型；

项目的设计主要分为三个阶段：

- 第一阶段、开发本地代码生成器和命令行工具
- 第二阶段、开发代码生成器的制作工具
- 第三阶段、开发在线代码生成器管理、分享、使用平台

本期重点，是学习面对一个项目，如何分阶段分步骤的拆解项目，做到循序渐进的开展研发，要能够**自己写出一套设计方案**；另外新项目的需求分析、技术选型、寻找网上已有的开源项目进行对比，都是一个项目开发前期必不可少的工作。



## 三、后续版本更新记录