package com.easyink.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 抖音巨量引擎广告配置
 *
 * @author admin
 * @date 2026-04-04
 */
@Component
@Data
@ConfigurationProperties(prefix = "oceanengine")
public class OceanEngineConfig {

    /**
     * 巨量引擎广告回调接口URL
     */
    private String callbackUrl = "https://analytics.oceanengine.com/api/v2/conversion";

    /**
     * 是否启用巨量引擎回调
     */
    private boolean enabled = false;
}
