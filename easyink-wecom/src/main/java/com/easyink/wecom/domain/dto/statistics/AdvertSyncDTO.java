package com.easyink.wecom.domain.dto.statistics;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 广告数据同步DTO
 *
 * @author admin
 * @date 2026-04-04
 */
@Data
@ApiModel("广告数据同步DTO")
public class AdvertSyncDTO {

    @ApiModelProperty("广告平台，gdtwx: 微信广点通，gdtqz：QQ广点通，juldy: 抖音巨量引擎")
    private String platform;

    @ApiModelProperty("渠道信息，对应we_emple_code表中的state字段")
    private String channel;

    @ApiModelProperty("广告点击ID，不同平台对应，gdtwx: gdt_vid, gdtqz: qz_gdt, juldy: clickid")
    private String clickid;

    @ApiModelProperty("用户UNIONID")
    private String unionid;

    @ApiModelProperty("用户已验证手机号")
    private String mobile;

    @ApiModelProperty("备注信息")
    private String remark;

    @ApiModelProperty("是否提交表单，1：已提交，0：未提交")
    private Integer isFormed;

    @ApiModelProperty("是否支付，1：已支付，0：未支付")
    private Integer isPaid;

    @ApiModelProperty("广告回调类型")
    private String callbackType;
}
