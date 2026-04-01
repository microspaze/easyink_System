package com.easyink.wecom.domain.vo.statistics.advert;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 广告统计-渠道维度VO
 *
 * @author admin
 * @date 2026-04-01
 */
@Data
public class AdvertChannelVO extends AdvertStatisticVO {

    @ApiModelProperty("渠道名称/活码名称")
    private String empleName;

    @ApiModelProperty("活码id/获客链接id")
    private String empleCodeId;
}