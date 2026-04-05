package com.easyink.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 广点通广告配置
 *
 * @author admin
 * @date 2026-04-04
 */
@Component
@Data
@ConfigurationProperties(prefix = "gdt")
public class GdtConfig {

    /**
     * 回调接口accessToken，从腾讯广告开放平台获取，永久有效
     */
    private String accessToken;

    /**
     * 推广帐号id
     */
    private String accountId;

    /**
     * 用户行为源id
     */
    private String userActionSetId;

    /**
     * 用户行为上报接口URL
     */
    private String userActionApi;

    /**
     * 是否启用广点通回调
     */
    private boolean enabled = false;
}
