package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.WeAdvertEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 广告记录表Mapper接口
 *
 * @author admin
 * @date 2026-04-01
 */
@Mapper
public interface WeAdvertEntryMapper extends BaseMapper<WeAdvertEntry> {

    /**
     * 根据unionid更新is_added字段
     *
     * @param unionid 客户unionid
     * @param hours   几个小时内的记录
     * @return 更新条数
     */
    @Update("UPDATE we_advert_entry SET is_added = 1, update_time = NOW() " +
            "WHERE unionid = #{unionid} " +
            "AND create_time >= DATE_SUB(NOW(), INTERVAL #{hours} HOUR) " +
            "AND is_added = 0")
    int updateIsAddedByUnionid(@Param("unionid") String unionid, @Param("hours") int hours);

    /**
     * 根据unionid更新is_deleted字段
     *
     * @param unionid 客户unionid
     * @param hours   多少小时内的记录
     * @return 更新条数
     */
    @Update("UPDATE we_advert_entry SET is_deleted = 1, update_time = NOW() " +
            "WHERE unionid = #{unionid} " +
            "AND create_time >= DATE_SUB(NOW(), INTERVAL #{hours} HOUR) " +
            "AND is_deleted = 0")
    int updateIsDeletedByUnionid(@Param("unionid") String unionid, @Param("hours") int hours);

    /**
     * 根据渠道列表获取广告记录统计数据
     *
     * @param channelList 渠道列表
     * @param beginDate   开始日期
     * @param endDate     结束日期
     * @return 统计结果列表
     */
    List<WeAdvertEntry> selectAdvertStatisticByChannels(@Param("channelList") List<String> channelList,
                                                        @Param("beginDate") String beginDate,
                                                        @Param("endDate") String endDate);

    /**
     * 根据渠道列表获取广告记录统计数据（按渠道分组）
     *
     * @param channelList 渠道列表
     * @param beginDate   开始日期
     * @param endDate     结束日期
     * @return 按渠道分组的统计结果
     */
    List<WeAdvertEntry> selectAdvertStatisticGroupByChannel(@Param("channelList") List<String> channelList,
                                                            @Param("beginDate") String beginDate,
                                                            @Param("endDate") String endDate);

    /**
     * 根据unionid和创建时间范围查询广告记录
     *
     * @param unionid 客户unionid
     * @param hours   几个小时内的记录
     * @return 广告记录列表
     */
    @Select("SELECT * FROM we_advert_entry " +
            "WHERE unionid = #{unionid} " +
            "AND create_time >= DATE_SUB(NOW(), INTERVAL #{hours} HOUR) " +
            "AND is_callbacked = 0")
    List<WeAdvertEntry> selectByUnionidAndHours(@Param("unionid") String unionid, @Param("hours") int hours);

    /**
     * 根据channel、mobile和创建时间范围查询广告记录
     *
     * @param channel 渠道state
     * @param mobile  手机号
     * @param hours   几个小时内的记录
     * @return 广告记录列表
     */
    @Select("SELECT * FROM we_advert_entry " +
            "WHERE channel = #{channel} " +
            "AND mobile = #{mobile} " +
            "AND create_time >= DATE_SUB(NOW(), INTERVAL #{hours} HOUR) " +
            "AND is_callbacked = 0")
    List<WeAdvertEntry> selectByChannelMobileAndHours(@Param("channel") String channel,
                                                        @Param("mobile") String mobile,
                                                        @Param("hours") int hours);

    /**
     * 根据clickid查询广告记录
     *
     * @param clickid 广告点击ID
     * @return 广告记录
     */
    @Select("SELECT * FROM we_advert_entry WHERE clickid = #{clickid} LIMIT 1")
    WeAdvertEntry selectByClickid(@Param("clickid") String clickid);

    /**
     * 更新is_callbacked状态
     *
     * @param id           记录ID
     * @param isCallbacked 是否回调成功
     * @return 更新条数
     */
    @Update("UPDATE we_advert_entry SET is_callbacked = #{isCallbacked}, " +
            "unionid = #{unionid}, is_added = #{isAdded}, callback_time = NOW(), update_time = NOW() " +
            "WHERE id = #{id}")
    int updateIsCallbacked(@Param("id") Long id, @Param("isCallbacked") Integer isCallbacked, @Param("unionid") String unionid, @Param("isAdded") Integer isAdded);
}