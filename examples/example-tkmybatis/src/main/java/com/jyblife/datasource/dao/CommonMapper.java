package com.jyblife.datasource.dao;

import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 公共mapper接口
 *
 * @Date 2019/4/28 19:45
 * Version      1.0
 **/
public interface CommonMapper<T> extends Mapper<T>, MySqlMapper<T>,IdsMapper<T> {
}
