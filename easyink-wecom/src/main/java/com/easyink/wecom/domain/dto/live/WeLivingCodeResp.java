package com.easyink.wecom.domain.dto.live;

import com.easyink.wecom.domain.dto.WeResultDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 企微-获取直播凭证living_code响应
 *
 * @author easyink
 */
@Data
public class WeLivingCodeResp extends WeResultDTO {

    @JsonProperty("living_code")
    private String livingCode;
}
