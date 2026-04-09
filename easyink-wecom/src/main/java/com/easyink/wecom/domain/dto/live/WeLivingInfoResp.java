package com.easyink.wecom.domain.dto.live;

import com.easyink.wecom.domain.dto.WeResultDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 企微-获取直播详情响应
 *
 * @author easyink
 */
@Data
public class WeLivingInfoResp extends WeResultDTO {

    @JsonProperty("livingid")
    private String livingid;

    @JsonProperty("push_stream_url")
    private String pushStreamUrl;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("start_time")
    private Long startTime;

    @JsonProperty("end_time")
    private Long endTime;
}
