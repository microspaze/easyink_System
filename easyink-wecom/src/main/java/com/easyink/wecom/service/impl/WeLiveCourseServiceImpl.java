package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.live.WeLiveCourse;
import com.easyink.wecom.mapper.WeLiveCourseMapper;
import com.easyink.wecom.service.WeLiveCourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 直播课表Service实现
 *
 * @author easyink
 */
@Slf4j
@Service
public class WeLiveCourseServiceImpl extends ServiceImpl<WeLiveCourseMapper, WeLiveCourse> implements WeLiveCourseService {

    @Override
    public boolean addCourse(WeLiveCourse weLiveCourse) {
        return save(weLiveCourse);
    }

    @Override
    public boolean editCourse(WeLiveCourse weLiveCourse) {
        return updateById(weLiveCourse);
    }

    @Override
    public boolean deleteCourseById(Long id) {
        WeLiveCourse course = new WeLiveCourse();
        course.setId(id);
        course.setDelFlag(1);
        return updateById(course);
    }

    @Override
    public List<WeLiveCourse> selectCourseList(WeLiveCourse weLiveCourse) {
        LambdaQueryWrapper<WeLiveCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeLiveCourse::getDelFlag, 0)
               .eq(StringUtils.isNotBlank(weLiveCourse.getCorpId()), WeLiveCourse::getCorpId, weLiveCourse.getCorpId())
               .eq(weLiveCourse.getRoomId() != null, WeLiveCourse::getRoomId, weLiveCourse.getRoomId())
               .orderByDesc(WeLiveCourse::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<WeLiveCourse> selectActiveCourseList(String corpId) {
        return baseMapper.selectCourseListWithRoomName(corpId);
    }
}
