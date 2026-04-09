package com.easyink.wecom.domain.dto.live;

import com.easyink.wecom.domain.dto.WeResultDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 企微-上传临时素材响应
 *
 * @author easyink
 */
@Data
public class WeMediaUploadResp extends WeResultDTO {

    @JsonProperty("media_id")
    private String mediaId;

    @JsonProperty("type")
    private String type;

    @JsonProperty("created_at")
    private String createdAt;
}
