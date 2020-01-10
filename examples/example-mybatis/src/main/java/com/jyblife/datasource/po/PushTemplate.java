package com.jyblife.datasource.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PushTemplate implements Serializable {
    private Integer tplId;

    private String tplName;

    private Integer type;

    private Byte msgType;

    private Byte msgCenterType;

    private String title;

    private String url;

    private String tplParams;

    private Date createTime;

    private String creator;

    private Byte status;

    private Date statusTime;

    private String picUrl;

    private String sound;

    private String thumbnailUrl;

    private String text;

    private String pushText;

    private static final long serialVersionUID = 1L;
}
