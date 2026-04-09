package com.easyink.wecom.client;

import com.dtflys.forest.annotation.*;
import com.easyink.common.exception.RetryException;
import com.easyink.wecom.client.retry.EnableRetry;
import com.easyink.wecom.domain.dto.live.*;
import com.easyink.wecom.interceptor.WeAccessTokenInterceptor;
import org.springframework.stereotype.Component;

/**
 * 企微直播接口客户端
 *
 * @author easyink
 */
@Component
@BaseRequest(baseURL = "${weComServerUrl}${weComePrefix}")
public interface WeLiveClient {

    /**
     * 上传临时素材(海报图片)
     * POST https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=IMAGE
     */
    @Post(url = "/media/upload", interceptor = WeAccessTokenInterceptor.class)
    @EnableRetry(retryExceptionClass = RetryException.class)
    WeMediaUploadResp uploadMedia(@DataFile("media") java.io.File file, @Query("type") String type, @Header("corpid") String corpId);

    /**
     * 创建预约直播
     * POST https://qyapi.weixin.qq.com/cgi-bin/living/create_living
     */
    @Post(url = "/living/create_living", interceptor = WeAccessTokenInterceptor.class)
    @EnableRetry(retryExceptionClass = RetryException.class)
    WeCreateLivingResp createLiving(@Body WeCreateLivingReq req, @Header("corpid") String corpId);

    /**
     * 获取直播详情
     * GET https://qyapi.weixin.qq.com/cgi-bin/living/get_living_info?livingid=LIVINGID
     */
    @Get(url = "/living/get_living_info", interceptor = WeAccessTokenInterceptor.class)
    @EnableRetry(retryExceptionClass = RetryException.class)
    WeLivingInfoResp getLivingInfo(@Query("livingid") String livingid, @Header("corpid") String corpId);

    /**
     * 获取直播观看明细
     * POST https://qyapi.weixin.qq.com/cgi-bin/living/get_watch_stat
     */
    @Post(url = "/living/get_watch_stat", interceptor = WeAccessTokenInterceptor.class)
    @EnableRetry(retryExceptionClass = RetryException.class)
    WeWatchStatResp getWatchStat(@Body WeWatchStatReq req, @Header("corpid") String corpId);

    /**
     * 获取直播凭证living_code
     * GET https://qyapi.weixin.qq.com/cgi-bin/living/get_living_code?livingid=LIVINGID
     */
    @Get(url = "/living/get_living_code", interceptor = WeAccessTokenInterceptor.class)
    @EnableRetry(retryExceptionClass = RetryException.class)
    WeLivingCodeResp getLivingCode(@Query("livingid") String livingid, @Header("corpid") String corpId);

    /**
     * 修改预约直播
     * POST https://qyapi.weixin.qq.com/cgi-bin/living/modify_living
     */
    @Post(url = "/living/modify_living", interceptor = WeAccessTokenInterceptor.class)
    @EnableRetry(retryExceptionClass = RetryException.class)
    com.easyink.wecom.domain.dto.WeResultDTO modifyLiving(@Body java.util.Map<String, Object> req, @Header("corpid") String corpId);

    /**
     * 取消预约直播
     * POST https://qyapi.weixin.qq.com/cgi-bin/living/cancel_living
     */
    @Post(url = "/living/cancel_living", interceptor = WeAccessTokenInterceptor.class)
    @EnableRetry(retryExceptionClass = RetryException.class)
    com.easyink.wecom.domain.dto.WeResultDTO cancelLiving(@Body java.util.Map<String, String> req, @Header("corpid") String corpId);
}
