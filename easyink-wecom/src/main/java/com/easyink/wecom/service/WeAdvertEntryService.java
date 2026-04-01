package com.easyink.wecom.service;

import com.easyink.wecom.domain.WeAdvertEntry;
import com.easyink.wecom.domain.dto.statistics.AdvertStatisticDTO;
import com.easyink.wecom.domain.vo.statistics.advert.AdvertChannelVO;
import com.easyink.wecom.domain.vo.statistics.advert.AdvertStatisticVO;

import java.util.List;

/**
 * 广告记录表Service接口
 *
 * @author admin
 * @date 2026-04-01
 */
public interface WeAdvertEntryService {

    /**
     * 根据客户unionid更新is_added字段（最近2小时内的记录）
     *
     * @param unionid 客户unionid
     * @return 更新条数
     */
    int updateIsAddedByUnionid(String unionid);

    /**
     * 根据客户unionid更新is_deleted字段（最近24小时内的记录）
     *
     * @param unionid 客户unionid
     * @return 更新条数
     */
    int updateIsDeletedByUnionid(String unionid);

    /**
     * 获取广告统计数据总览
     *
     * @param dto 查询条件
     * @return 统计数据总览
     */
    AdvertStatisticVO getAdvertTotal(AdvertStatisticDTO dto);

    /**
     * 获取广告统计数据（按渠道分组）
     *
     * @param dto 查询条件
     * @return 按渠道分组的统计数据
     */
    List<AdvertChannelVO> getAdvertChannelList(AdvertStatisticDTO dto);

    /**
     * 根据empleCodeIdList获取对应的state列表
     *
     * @param corpId         企业ID
     * @param empleCodeIdList 活码ID列表
     * @return state列表
     */
    List<String> getStateListByEmpleCodeIds(String corpId, List<Long> empleCodeIdList);
}