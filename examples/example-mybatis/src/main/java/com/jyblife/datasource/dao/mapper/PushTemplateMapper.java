package com.jyblife.datasource.dao.mapper;


import com.jyblife.datasource.annotation.TargetDataSource;
import com.jyblife.datasource.po.PushTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * PushTemplateMapper
 */
@Mapper
@TargetDataSource("db_push")
public interface PushTemplateMapper {

    int updateByPrimaryKeySelective(PushTemplate pushTemplate);
}




