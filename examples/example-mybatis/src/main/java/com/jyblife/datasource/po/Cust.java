package com.jyblife.datasource.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Cust implements Serializable {
    private Integer id;

    private String phone;

    private String name;

    private String pwd;

    private String qq;

    private String wb;

    private String wx;

    private String identifier;

    private String type;

    private Date registTime;

    private Date loginTime;

    private Date logoutTime;

    private Integer applyId;

    private String status;

    private String srcFrom;

    private String subSrcFrom;

    private Byte bstatus;

    private Date bstatusTime;

    private Date statusTime;

    private Byte level;

    private Date authenticTime;

    private String regIp;

    private String regDevid;

    private Byte authentic;

    private static final long serialVersionUID = 1L;
}
