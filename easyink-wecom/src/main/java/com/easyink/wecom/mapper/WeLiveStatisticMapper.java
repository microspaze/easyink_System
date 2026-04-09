package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.live.WeLiveStatistic;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 直播观看统计Mapper接口
 *
 * @author easyink
 */
@Repository
public interface WeLiveStatisticMapper extends BaseMapper<WeLiveStatistic> {

    /**
     * 查询课程观看统计列表(含客户名称)
     *
     * @param itemId 课程实例ID
     * @param corpId 企业ID
     * @return 统计列表
     */
    List<WeLiveStatistic> selectStatisticByItemId(@Param("itemId") Long itemId, @Param("corpId") String corpId);

    /**
     * 查询员工关联客户的观看统计
     *
     * @param livingid    直播ID(可选)
     * @param roomId      直播间ID(可选)
     * @param corpId      企业ID
     * @param userIdList  员工userId列表
     * @param beginTime   开始时间(可选)
     * @param endTime     结束时间(可选)
     * @return 统计列表
     */
    List<WeLiveStatistic> selectStatisticByUserIds(@Param("livingid") String livingid,
                                                    @Param("roomId") Long roomId,
                                                    @Param("corpId") String corpId,
                                                    @Param("userIdList") List<String> userIdList,
                                                    @Param("beginTime") String beginTime,
                                                    @Param("endTime") String endTime);

    /**
     * 查询部门维度统计(按部门数据范围过滤)
     *
     * @param livingid            直播ID(可选)
     * @param roomId              直播间ID(可选)
     * @param corpId              企业ID
     * @param departmentDataScope 部门数据范围(逗号分隔的部门ID,超管传null)
     * @param beginTime           开始时间(可选)
     * @param endTime             结束时间(可选)
     * @return 统计列表
     */
    List<WeLiveStatistic> selectStatisticByDepartmentScope(@Param("livingid") String livingid,
                                                            @Param("roomId") Long roomId,
                                                            @Param("corpId") String corpId,
                                                            @Param("departmentDataScope") String departmentDataScope,
                                                            @Param("beginTime") String beginTime,
                                                            @Param("endTime") String endTime);
}
