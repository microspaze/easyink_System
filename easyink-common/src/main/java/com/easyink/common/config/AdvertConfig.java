package com.easyink.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 广告数据同步配置
 *
 * @author admin
 * @date 2026-04-04
 */
@Component
@Data
@ConfigurationProperties(prefix = "advert")
public class AdvertConfig {

    /**
     * 广告数据同步接口token
     */
    private String syncToken;

    /**
     * 同步接口是否启用
     */
    private boolean syncEnabled = false;
}
