package com.yupi.web.mapper;

import com.yupi.web.model.entity.Generator;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Tom Smile
* @description 针对表【generator(代码生成器)】的数据库操作Mapper
* @createDate 2024-03-12 14:59:12
* @Entity com.yupi.web.model.entity.Generator
*/
public interface GeneratorMapper extends BaseMapper<Generator> {
@Select("  SELECT id,disPath  FORM genertor   WHERE isDelete = 1")
List<Generator> listDeletedGenerator();
}




