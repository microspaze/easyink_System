package com.easyink.wecom.domain.dto.live;

import com.easyink.wecom.domain.dto.WeResultDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 企微-创建预约直播响应
 *
 * @author easyink
 */
@Data
public class WeCreateLivingResp extends WeResultDTO {

    @JsonProperty("livingid")
    private String livingid;
}
