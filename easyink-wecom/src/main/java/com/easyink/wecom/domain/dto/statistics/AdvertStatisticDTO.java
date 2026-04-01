package com.easyink.wecom.domain.dto.statistics;

import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 广告统计-查询DTO
 *
 * @author admin
 * @date 2026-04-01
 */
@Data
public class AdvertStatisticDTO extends BaseEntity {

    @ApiModelProperty("企业ID")
    private String corpId;

    @ApiModelProperty("员工活码idList")
    private List<Long> empleCodeIdList;

    @ApiModelProperty("开始时间 格式: YYYY-MM-DD")
    private String beginDate;

    @ApiModelProperty("结束时间 格式: YYYY-MM-DD")
    private String endDate;
}