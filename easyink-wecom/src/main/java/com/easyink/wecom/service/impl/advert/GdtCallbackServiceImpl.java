package com.easyink.wecom.service.impl.advert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easyink.common.config.GdtConfig;
import com.easyink.wecom.service.AdvertCallbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 广点通广告回调服务实现
 *
 * @author admin
 * @date 2026-04-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GdtCallbackServiceImpl implements AdvertCallbackService {

    private final GdtConfig gdtConfig;

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    @Override
    public boolean executeCallback(String unionid, String clickId, String callbackType) {
        if (!gdtConfig.isEnabled()) {
            log.info("[广点通回调] 广点通回调未启用，跳过回调");
            return false;
        }

        if (StringUtils.isBlank(unionid) || StringUtils.isBlank(clickId)) {
            log.warn("[广点通回调] 参数不完整，unionid: {}, clickId: {}", unionid, clickId);
            return false;
        }

        try {
            // 构建请求数据
            Map<String, Object> requestBody = buildRequestBody(unionid, clickId, callbackType);
            String jsonBody = JSON.toJSONString(requestBody);

            log.info("[广点通回调] 开始回调，请求数据: {}", jsonBody);

            // 发送HTTP请求
            String response = sendRequest(jsonBody);

            log.info("[广点通回调] 回调响应: {}", response);

            // 解析响应判断是否成功
            return parseResponse(response);
        } catch (Exception e) {
            log.error("[广点通回调] 回调异常，unionid: {}, clickId: {}, error: {}",
                    unionid, clickId, e.getMessage());
            return false;
        }
    }

    @Override
    public String getPlatform() {
        return "gdt";
    }

    /**
     * 构建请求体
     */
    private Map<String, Object> buildRequestBody(String unionid, String clickId, String callbackType) {
        Map<String, Object> body = new HashMap<>();
        body.put("account_id", gdtConfig.getAccountId());
        body.put("user_action_set_id", gdtConfig.getUserActionSetId());

        // 构建actions数组
        Map<String, Object> action = new HashMap<>();
        action.put("action_time", System.currentTimeMillis() / 1000);
        action.put("click_id", clickId);

        // 构建user_id
        Map<String, String> userId = new HashMap<>();
        userId.put("wechat_unionid", unionid);
        action.put("user_id", userId);

        action.put("action_type", StringUtils.isNotBlank(callbackType) ? callbackType : "ACTIVE");

        body.put("actions", new Object[]{action});

        return body;
    }

    /**
     * 发送HTTP请求
     */
    private String sendRequest(String jsonBody) throws IOException {
        String url = gdtConfig.getUserActionApi() + "?access_token=" + gdtConfig.getAccessToken();

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
            // 广点通返回0表示成功
            return code != null && code == 0;
        } catch (Exception e) {
            log.error("[广点通回调] 解析响应异常: {}", e.getMessage());
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
