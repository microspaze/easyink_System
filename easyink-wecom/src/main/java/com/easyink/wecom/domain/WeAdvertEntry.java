package com.easyink.wecom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * 广告记录表 we_advert_entry
 *
 * @author admin
 * @date 2026-04-01
 */
@Data
@TableName("we_advert_entry")
@ApiModel("广告记录表")
public class WeAdvertEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("记录ID")
    @TableId(type = IdType.INPUT)
    private Long id;

    @ApiModelProperty("广告平台，gdtwx: 微信广点通，gdtqz：QQ广点通，juldy: 抖音巨量引擎")
    private String platform;

    @ApiModelProperty("渠道信息，对应we_emple_code表中的state字段")
    private String channel;

    @ApiModelProperty("广告点击ID，不同平台对应，gdtwx: gdt_vid, gdtqz: qz_gdt, juldy: clickid")
    private String clickid;

    @ApiModelProperty("用户UNIONID，由广告展示端提交，主要来自小程序")
    private String unionid;

    @ApiModelProperty("用户已验证手机号，小程序快捷授权或验证码验证")
    private String mobile;

    @ApiModelProperty("备注信息，用于保存表单提交信息，如股票代码、股票名称")
    private String remark;

    @ApiModelProperty("是否提交表单，1：已提交，0：未提交")
    private Integer isFormed;

    @ApiModelProperty("是否支付，1：已支付，0：未支付")
    private Integer isPaid;

    @ApiModelProperty("是否添加业务员企微，1：已添加，0：未添加")
    private Integer isAdded;

    @ApiModelProperty("是否流失，1：已流失，0：未流失")
    private Integer isDeleted;

    @ApiModelProperty("是否回调成功，1：已回调，0：未回调")
    private Integer isCallbacked;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("回调时间")
    private Date callbackTime;

    // ========== 以下字段用于统计查询结果，非数据库字段 ==========

    @ApiModelProperty("进线数（统计用）")
    @TableField(exist = false)
    private BigInteger totalCnt;

    @ApiModelProperty("表单提交数（统计用）")
    @TableField(exist = false)
    private BigInteger formedCnt;

    @ApiModelProperty("订单支付数（统计用）")
    @TableField(exist = false)
    private BigInteger paidCnt;

    @ApiModelProperty("企微添加数（统计用）")
    @TableField(exist = false)
    private BigInteger addedCnt;

    @ApiModelProperty("删除数（统计用）")
    @TableField(exist = false)
    private BigInteger deletedCnt;
}