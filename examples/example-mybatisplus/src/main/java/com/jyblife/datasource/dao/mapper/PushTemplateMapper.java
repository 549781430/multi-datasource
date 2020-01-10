package com.jyblife.datasource.dao.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.jyblife.datasource.anotation.TargetDataSource;
import com.jyblife.datasource.po.PushTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * PushTemplateMapper
 */
@Mapper
@TargetDataSource("db_push")
public interface PushTemplateMapper extends BaseMapper<PushTemplate> {

    void updateByPrimaryKeySelective(PushTemplate pushTemplate);
}




