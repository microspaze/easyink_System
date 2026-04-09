package com.easyink.wecom.domain.dto.live;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 企微-创建预约直播请求
 *
 * @author easyink
 */
@Data
@Builder
public class WeCreateLivingReq {

    @JsonProperty("anchor_userid")
    private String anchorUserid;

    @JsonProperty("poster_media_id")
    private String posterMediaId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("starttime")
    private Long starttime;

    @JsonProperty("endtime")
    private Long endtime;

    @JsonProperty("type")
    @Builder.Default
    private Integer type = 0;
}
