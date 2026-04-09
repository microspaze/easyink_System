package com.easyink.common.enums.live;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 直播状态枚举
 *
 * @author easyink
 */
@AllArgsConstructor
public enum LiveStatusEnum {

    NOT_START(0, "未开始"),
    LIVING(1, "直播中"),
    ENDED(2, "已结束"),
    EXPIRED(3, "已过期"),
    CANCELLED(4, "已取消"),
    ;

    @Getter
    private final Integer code;

    @Getter
    private final String msg;

    public static Optional<LiveStatusEnum> getByCode(Integer code) {
        return Arrays.stream(values()).filter(v -> v.code.equals(code)).findFirst();
    }
}
