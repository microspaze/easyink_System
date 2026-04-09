package com.easyink.wecom.domain.dto.live;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 企微-获取直播观看明细请求
 *
 * @author easyink
 */
@Data
@Builder
public class WeWatchStatReq {

    @JsonProperty("livingid")
    private String livingid;

    @JsonProperty("next_key")
    private String nextKey;
}
