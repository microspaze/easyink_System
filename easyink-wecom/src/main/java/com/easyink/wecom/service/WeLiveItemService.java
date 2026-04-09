package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.live.WeLiveItem;
import com.easyink.wecom.domain.vo.live.LivingCodeVO;

import java.util.List;

/**
 * 直播课程实例Service接口
 *
 * @author easyink
 */
public interface WeLiveItemService extends IService<WeLiveItem> {

    /**
     * 查询课程列表
     *
     * @param weLiveItem 查询条件
     * @return 课程列表
     */
    List<WeLiveItem> selectItemList(WeLiveItem weLiveItem);

    /**
     * 获取课程详情
     *
     * @param id 课程实例ID
     * @return 课程详情
     */
    WeLiveItem getItemById(Long id);

    /**
     * 取消课程
     *
     * @param id 课程实例ID
     * @return 是否成功
     */
    boolean cancelItem(Long id);

    /**
     * 根据roomId获取最合适的直播课程,获取living_code
     * 优先级: 半小时内即将开始 > 直播中 > 最新已结束
     *
     * @param roomId 直播间ID
     * @return 直播凭证VO
     */
    LivingCodeVO getLivingCodeByRoomId(Long roomId);

    /**
     * 每天凌晨定时创建课程实例
     */
    void createDailyItems();

    /**
     * 直播状态监控(定时任务)
     */
    void monitorLiveStatus();

    /**
     * 采集直播观看统计(定时任务)
     */
    void collectWatchStatistic();
}
