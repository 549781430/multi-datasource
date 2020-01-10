package com.jyblife.datasource.dao.mapper;

import com.jyblife.datasource.po.Cust;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * CustMapper
 */
@Mapper
public interface CustMapper {

    List<Cust> selectBySql(@Param("sql") String sql);

    void updateByPrimaryKeySelective(Cust cust);
}




