package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.live.WeLiveItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 直播课程实例Mapper接口
 *
 * @author easyink
 */
@Repository
public interface WeLiveItemMapper extends BaseMapper<WeLiveItem> {

    /**
     * 查询直播间最近2条有效课程(按start_time DESC)
     *
     * @param roomId 直播间ID
     * @return 课程列表
     */
    List<WeLiveItem> selectLatestItemsByRoomId(@Param("roomId") Long roomId);

    /**
     * 查询课程列表(含直播间名称)
     *
     * @param item 查询条件
     * @return 课程列表
     */
    List<WeLiveItem> selectItemListWithRoomName(WeLiveItem item);

    /**
     * 检查同一天同一课表是否已创建课程
     *
     * @param courseId  课表ID
     * @param startTime 开始时间(yyyy-MM-dd)
     * @return 记录数
     */
    int countByCourseIdAndDate(@Param("courseId") Long courseId, @Param("startTime") String startTime);
}
