package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.live.WeLiveStatistic;

import java.util.List;

/**
 * 直播观看统计Service接口
 *
 * @author easyink
 */
public interface WeLiveStatisticService extends IService<WeLiveStatistic> {

    /**
     * 查询课程观看统计
     *
     * @param itemId 课程实例ID
     * @param corpId 企业ID
     * @return 统计列表
     */
    List<WeLiveStatistic> selectStatisticByItemId(Long itemId, String corpId);

    /**
     * 查询员工关联客户的观看统计
     *
     * @param livingid   直播ID(可选)
     * @param roomId     直播间ID(可选)
     * @param corpId     企业ID
     * @param beginTime  开始时间(可选)
     * @param endTime    结束时间(可选)
     * @return 统计列表
     */
    List<WeLiveStatistic> selectStatisticByEmployee(String livingid, Long roomId, String corpId,
                                                     String beginTime, String endTime);

    /**
     * 查询部门维度统计
     *
     * @param livingid   直播ID(可选)
     * @param roomId     直播间ID(可选)
     * @param corpId     企业ID
     * @param userIdList 部门下员工ID列表
     * @param beginTime  开始时间(可选)
     * @param endTime    结束时间(可选)
     * @return 统计列表
     */
    List<WeLiveStatistic> selectStatisticByDepartment(String livingid, Long roomId, String corpId,
                                                       List<String> userIdList, String beginTime, String endTime);

    /**
     * 保存或更新观看统计
     *
     * @param statistic 统计信息
     */
    void saveOrUpdateStatistic(WeLiveStatistic statistic);
}
