package com.jyblife.datasource.service;

import com.jyblife.datasource.annotation.Transactional;
import com.jyblife.datasource.dao.mapper.CustMapper;
import com.jyblife.datasource.dao.mapper.PushTemplateMapper;
import com.jyblife.datasource.po.Cust;
import com.jyblife.datasource.po.PushTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * custService
 */
@Service
@Slf4j
@Transactional
public class TestService {

    @Autowired
    private CustMapper custMapper;
    @Autowired
    private PushTemplateMapper pushTemplateMapper;

    public void update() {
        Cust cust = new Cust();
        cust.setId(1);
        cust.setName("加油宝金融科技2");
        custMapper.updateByPrimaryKeySelective(cust);

        PushTemplate pushTemplate = new PushTemplate();
        pushTemplate.setTplId(22);
        pushTemplate.setTplName("中石油BP卡2");
        pushTemplateMapper.updateByPrimaryKeySelective(pushTemplate);

    }

    @Transactional
    public void insert(){
        System.out.println("ces");
    }
}

