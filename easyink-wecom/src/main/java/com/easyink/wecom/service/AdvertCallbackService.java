package com.easyink.wecom.service;

/**
 * 广告回调服务接口
 *
 * @author admin
 * @date 2026-04-04
 */
public interface AdvertCallbackService {

    /**
     * 执行广告回调
     *
     * @param unionid     客户的unionId
     * @param clickId     广告点击ID (source字段)
     * @param callbackType 回调类型
     * @return 是否回调成功
     */
    boolean executeCallback(String unionid, String clickId, String callbackType);

    /**
     * 获取对应的平台标识
     *
     * @return 平台标识 (gdtwx/gdtqz/juldy)
     */
    String getPlatform();
}
