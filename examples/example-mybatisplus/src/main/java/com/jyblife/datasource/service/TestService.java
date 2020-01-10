package com.jyblife.datasource.service;

import com.jyblife.datasource.anotation.Transactional;
import com.jyblife.datasource.dao.mapper.CustMapper;
import com.jyblife.datasource.dao.mapper.PushTemplateMapper;
import com.jyblife.datasource.po.Cust;
import com.jyblife.datasource.po.PushTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * custService
 */
@Service
//@Transactional // 可以使用在类上面
public class TestService {

    @Autowired
    private CustMapper custMapper;
    @Autowired
    private PushTemplateMapper pushTemplateMapper;

    @Transactional
    public void update() {
        Cust cust = new Cust();
        cust.setId(1);
        cust.setName("加油宝金融科技1");
        custMapper.updateByPrimaryKeySelective(cust);

        PushTemplate pushTemplate = new PushTemplate();
        pushTemplate.setTplId(22);
        pushTemplate.setTplName("中石油BP卡1");
        pushTemplateMapper.updateByPrimaryKeySelective(pushTemplate);
        // throw new RuntimeException("发生异常类");

    }

    @Transactional
    public void insert(){
        System.out.println("ces");
    }
}

