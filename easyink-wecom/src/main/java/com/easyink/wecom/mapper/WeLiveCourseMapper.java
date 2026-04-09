package com.easyink.wecom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easyink.wecom.domain.live.WeLiveCourse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 直播课表Mapper接口
 *
 * @author easyink
 */
@Repository
public interface WeLiveCourseMapper extends BaseMapper<WeLiveCourse> {

    /**
     * 查询指定企业下状态正常的课表列表(含直播间名称)
     *
     * @param corpId 企业ID
     * @return 课表列表
     */
    List<WeLiveCourse> selectCourseListWithRoomName(@Param("corpId") String corpId);
}
