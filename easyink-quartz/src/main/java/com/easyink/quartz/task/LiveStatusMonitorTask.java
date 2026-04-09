package com.easyink.quartz.task;

import com.easyink.wecom.service.WeLiveItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 直播状态监控定时任务
 * invokeTarget: liveStatusMonitorTask.execute
 * cron: 0 */5 * * * ?
 *
 * @author easyink
 */
@Slf4j
@Component("liveStatusMonitorTask")
public class LiveStatusMonitorTask {

    private final WeLiveItemService weLiveItemService;

    @Autowired
    public LiveStatusMonitorTask(WeLiveItemService weLiveItemService) {
        this.weLiveItemService = weLiveItemService;
    }

    /**
     * 执行定时任务: 监控直播状态变更
     */
    public void execute() {
        log.info("定时任务[直播状态监控]开始执行");
        try {
            weLiveItemService.monitorLiveStatus();
        } catch (Exception e) {
            log.error("定时任务[直播状态监控]执行异常: {}", e.getMessage(), e);
        }
        log.info("定时任务[直播状态监控]执行完成");
    }
}
