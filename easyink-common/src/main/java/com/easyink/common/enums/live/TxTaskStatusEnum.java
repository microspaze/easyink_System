package com.easyink.common.enums.live;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 腾讯云转推流任务状态枚举
 *
 * @author easyink
 */
@AllArgsConstructor
public enum TxTaskStatusEnum {

    NOT_CREATED(0, "未创建"),
    RUNNING(1, "运行中"),
    STOPPED(2, "已停止"),
    FAILED(3, "失败"),
    ;

    @Getter
    private final Integer code;

    @Getter
    private final String msg;
}
