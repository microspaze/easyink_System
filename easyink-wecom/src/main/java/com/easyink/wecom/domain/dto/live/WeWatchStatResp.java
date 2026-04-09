package com.easyink.wecom.domain.dto.live;

import com.easyink.wecom.domain.dto.WeResultDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 企微-获取直播观看明细响应
 *
 * @author easyink
 */
@Data
public class WeWatchStatResp extends WeResultDTO {

    @JsonProperty("ending")
    private Integer ending;

    @JsonProperty("next_key")
    private String nextKey;

    @JsonProperty("stat_info")
    private List<WatchStatItem> statInfo;

    @Data
    public static class WatchStatItem {

        @JsonProperty("external_userid")
        private String externalUserid;

        @JsonProperty("enter_time")
        private Long enterTime;

        @JsonProperty("watch_time")
        private Integer watchTime;

        @JsonProperty("comment_count")
        private Integer commentCount;

        @JsonProperty("is_internal")
        private Integer isInternal;

        @JsonProperty("inviter_userid")
        private String inviterUserid;

        @JsonProperty("inviter_external_userid")
        private String inviterExternalUserid;
    }
}
