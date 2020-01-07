package com.jyblife.datasource.po;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "`t_push_template`")
public class PushTemplate implements Serializable {
    @Id
    @Column(name = "`tpl_id`")
    private Integer tplId;

    @Column(name = "`tpl_name`")
    private String tplName;

    @Column(name = "`type`")
    private Integer type;

    @Column(name = "`msg_type`")
    private Byte msgType;

    /**
     * 消息中心类型1、活动福利  2、系统消息  3、订阅提醒
     */
    @Column(name = "`msg_center_type`")
    private Byte msgCenterType;

    @Column(name = "`title`")
    private String title;

    @Column(name = "`url`")
    private String url;

    @Column(name = "`tpl_params`")
    private String tplParams;

    @Column(name = "`create_time`")
    private Date createTime;

    @Column(name = "`creator`")
    private String creator;

    /**
     * 0 新建
     1 有效
     */
    @Column(name = "`status`")
    private Byte status;

    @Column(name = "`status_time`")
    private Date statusTime;

    @Column(name = "`pic_url`")
    private String picUrl;

    /**
     * 该模板声效名
     */
    @Column(name = "`sound`")
    private String sound;

    /**
     * 缩略图地址
     */
    @Column(name = "`thumbnail_url`")
    private String thumbnailUrl;

    @Column(name = "`text`")
    private String text;

    @Column(name = "`push_text`")
    private String pushText;

    private static final long serialVersionUID = 1L;
}