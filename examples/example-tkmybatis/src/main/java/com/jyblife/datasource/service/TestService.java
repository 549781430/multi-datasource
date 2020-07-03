package com.jyblife.datasource.service;

import com.jyblife.datasource.annotation.Transactional;
import com.jyblife.datasource.dao.mapper.CustMapper;
import com.jyblife.datasource.dao.mapper.PushTemplateMapper;
import com.jyblife.datasource.po.Cust;
import com.jyblife.datasource.po.PushTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * custService
 */
@Service
@Slf4j
public class TestService {

    @Autowired
    private CustMapper custMapper;
    @Autowired
    private PushTemplateMapper pushTemplateMapper;

    @Transactional
    public void update() {
        Cust cust = new Cust();
        cust.setId(1);
        cust.setName("加油宝金融科技2");
        custMapper.updateByPrimaryKeySelective(cust);

        PushTemplate pushTemplate = new PushTemplate();
        pushTemplate.setTplId(22);
        pushTemplate.setTplName("中石油BP卡1");
        pushTemplateMapper.updateByPrimaryKeySelective(pushTemplate);

        nestUpdate();

        log.info("嵌套事务");
    }

    @Transactional
    public void nestUpdate() {
        Cust cust = new Cust();
        cust.setId(2);
        cust.setName("腾讯推广2");
        custMapper.updateByPrimaryKeySelective(cust);
    }

    public List<Cust> selectList() {
        return custMapper.selectBySql("select * from t_cust limit 1,10");
    }
}

