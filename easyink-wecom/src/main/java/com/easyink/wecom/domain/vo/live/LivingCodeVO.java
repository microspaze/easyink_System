package com.easyink.wecom.domain.vo.live;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * H5获取直播凭证响应VO
 *
 * @author easyink
 */
@Data
@ApiModel("直播凭证响应")
public class LivingCodeVO {

    @ApiModelProperty("直播凭证(5分钟有效)")
    private String livingCode;

    @ApiModelProperty("企微直播ID")
    private String livingid;

    @ApiModelProperty("课程标题")
    private String title;

    @ApiModelProperty("直播状态: 0-未开始 1-直播中 2-已结束")
    private Integer livingStatus;

    @ApiModelProperty("课程开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date courseStartTime;
}
