package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.live.WeLiveCourse;

import java.util.List;

/**
 * 直播课表Service接口
 *
 * @author easyink
 */
public interface WeLiveCourseService extends IService<WeLiveCourse> {

    /**
     * 添加课表
     *
     * @param weLiveCourse 课表信息
     * @return 是否成功
     */
    boolean addCourse(WeLiveCourse weLiveCourse);

    /**
     * 修改课表
     *
     * @param weLiveCourse 课表信息
     * @return 是否成功
     */
    boolean editCourse(WeLiveCourse weLiveCourse);

    /**
     * 删除课表(逻辑删除)
     *
     * @param id 课表ID
     * @return 是否成功
     */
    boolean deleteCourseById(Long id);

    /**
     * 查询课表列表(按直播间)
     *
     * @param weLiveCourse 查询条件
     * @return 课表列表
     */
    List<WeLiveCourse> selectCourseList(WeLiveCourse weLiveCourse);

    /**
     * 查询指定企业下状态正常的课表列表
     *
     * @param corpId 企业ID
     * @return 课表列表
     */
    List<WeLiveCourse> selectActiveCourseList(String corpId);
}
