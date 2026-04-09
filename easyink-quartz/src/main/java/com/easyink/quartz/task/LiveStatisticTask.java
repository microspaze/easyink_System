package com.easyink.quartz.task;

import com.easyink.wecom.service.WeLiveItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 直播观看统计采集定时任务
 * invokeTarget: liveStatisticTask.execute
 * cron: 0 */1 * * * ?
 *
 * @author easyink
 */
@Slf4j
@Component("liveStatisticTask")
public class LiveStatisticTask {

    private final WeLiveItemService weLiveItemService;

    @Autowired
    public LiveStatisticTask(WeLiveItemService weLiveItemService) {
        this.weLiveItemService = weLiveItemService;
    }

    /**
     * 执行定时任务: 采集直播观看统计
     */
    public void execute() {
        log.info("定时任务[直播观看统计采集]开始执行");
        try {
            weLiveItemService.collectWatchStatistic();
        } catch (Exception e) {
            log.error("定时任务[直播观看统计采集]执行异常: {}", e.getMessage(), e);
        }
        log.info("定时任务[直播观看统计采集]执行完成");
    }
}
