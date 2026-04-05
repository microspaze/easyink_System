package com.easyink.wecom.service.impl.advert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyink.common.config.OceanEngineConfig;
import com.easyink.wecom.service.AdvertCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 抖音巨量引擎广告回调服务实现
 *
 * @author admin
 * @date 2026-04-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OceanEngineCallbackServiceImpl implements AdvertCallbackService {

    private final OceanEngineConfig oceanEngineConfig;

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    @Override
    public boolean executeCallback(String unionid, String callback, String eventType) {
        if (!oceanEngineConfig.isEnabled()) {
            log.info("[巨量引擎回调] 巨量引擎回调未启用，跳过回调");
            return false;
        }

        if (StringUtils.isBlank(callback)) {
            log.warn("[巨量引擎回调] callback参数为空");
            return false;
        }

        try {
            // 构建请求数据
            Map<String, Object> requestBody = buildRequestBody(callback, eventType);
            String jsonBody = JSON.toJSONString(requestBody);

            log.info("[巨量引擎回调] 开始回调，请求数据: {}", jsonBody);

            // 发送HTTP请求
            String response = sendRequest(jsonBody);

            log.info("[巨量引擎回调] 回调响应: {}", response);

            // 解析响应判断是否成功
            return parseResponse(response);
        } catch (Exception e) {
            log.error("[巨量引擎回调] 回调异常，callback: {}, eventType: {}, error: {}",
                    callback, eventType, e.getMessage());
            return false;
        }
    }

    @Override
    public String getPlatform() {
        return "juldy";
    }

    /**
     * 构建请求体
     */
    private Map<String, Object> buildRequestBody(String callback, String eventType) {
        Map<String, Object> body = new HashMap<>();
        body.put("event_type", StringUtils.isNotBlank(eventType) ? eventType : "active");

        // 构建context.ad
        Map<String, Object> context = new HashMap<>();
        Map<String, Object> ad = new HashMap<>();
        ad.put("callback", callback);
        context.put("ad", ad);

        body.put("context", context);
        body.put("timestamp", System.currentTimeMillis());

        return body;
    }

    /**
     * 发送HTTP请求
     */
    private String sendRequest(String jsonBody) throws IOException {
        String url = oceanEngineConfig.getCallbackUrl();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(JSON_TYPE, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return response.body().string();
            }
            return null;
        }
    }

    /**
     * 解析响应
     */
    private boolean parseResponse(String response) {
        if (StringUtils.isBlank(response)) {
            return false;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            Integer code = jsonObject.getInteger("code");
            // 巨量引擎返回0表示成功
            return code != null && code == 0;
        } catch (Exception e) {
            log.error("[巨量引擎回调] 解析响应异常: {}", e.getMessage());
            return false;
        }
    }

    private static class StringUtils {
        public static boolean isBlank(String str) {
            return str == null || str.trim().isEmpty();
        }

        public static boolean isNotBlank(String str) {
            return !isBlank(str);
        }
    }
}
