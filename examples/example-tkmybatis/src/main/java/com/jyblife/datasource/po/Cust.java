package com.jyblife.datasource.po;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "`t_cust`")
public class Cust implements Serializable {
    @Id
    @Column(name = "`id`")
    private Integer id;

    @Column(name = "`phone`")
    private String phone;

    @Column(name = "`name`")
    private String name;

    @Column(name = "`pwd`")
    private String pwd;

    @Column(name = "`qq`")
    private String qq;

    @Column(name = "`wb`")
    private String wb;

    @Column(name = "`wx`")
    private String wx;

    @Column(name = "`identifier`")
    private String identifier;

    /**
     * 0:普通用户 1:大C用户 2:企业用户
     */
    @Column(name = "`type`")
    private String type;

    @Column(name = "`regist_time`")
    private Date registTime;

    @Column(name = "`login_time`")
    private Date loginTime;

    @Column(name = "`logout_time`")
    private Date logoutTime;

    @Column(name = "`apply_id`")
    private Integer applyId;

    /**
     * 用户状态 0:正常 1:冻结 2:注销
     */
    @Column(name = "`status`")
    private String status;

    @Column(name = "`src_from`")
    private String srcFrom;

    @Column(name = "`sub_src_from`")
    private String subSrcFrom;

    /**
     * 0 
     */
    @Column(name = "`bstatus`")
    private Byte bstatus;

    @Column(name = "`bstatus_time`")
    private Date bstatusTime;

    @Column(name = "`status_time`")
    private Date statusTime;

    @Column(name = "`level`")
    private Byte level;

    /**
     * 实名认证时间
     */
    @Column(name = "`authentic_time`")
    private Date authenticTime;

    /**
     * 用户注册IP
     */
    @Column(name = "`reg_ip`")
    private String regIp;

    /**
     * 设备id
     */
    @Column(name = "`reg_devid`")
    private String regDevid;

    /**
     * 0 未验证,1 验证成功,2 验证失败
     */
    @Column(name = "`authentic`")
    private Byte authentic;

    private static final long serialVersionUID = 1L;
}