package com.jyblife.datasource.dao.mapper;

import com.jyblife.datasource.anotation.Transaction;
import com.jyblife.datasource.dao.CommonMapper;
import com.jyblife.datasource.po.Cust;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * CustMapper
 */
@Mapper
public interface CustMapper extends CommonMapper<Cust> {

    @Transaction
    List<Cust> selectBySql(@Param("sql") String sql);

}




