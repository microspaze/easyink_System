package com.easyink.quartz.task;

import com.easyink.wecom.service.WeLiveItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 每天凌晨创建直播课程实例定时任务
 * invokeTarget: liveCourseScheduleTask.execute
 * cron: 0 0 2 * * ?
 *
 * @author easyink
 */
@Slf4j
@Component("liveCourseScheduleTask")
public class LiveCourseScheduleTask {

    private final WeLiveItemService weLiveItemService;

    @Autowired
    public LiveCourseScheduleTask(WeLiveItemService weLiveItemService) {
        this.weLiveItemService = weLiveItemService;
    }

    /**
     * 执行定时任务: 创建当天直播课程实例
     */
    public void execute() {
        log.info("定时任务[创建每日直播课程]开始执行");
        try {
            weLiveItemService.createDailyItems();
        } catch (Exception e) {
            log.error("定时任务[创建每日直播课程]执行异常: {}", e.getMessage(), e);
        }
        log.info("定时任务[创建每日直播课程]执行完成");
    }
}
