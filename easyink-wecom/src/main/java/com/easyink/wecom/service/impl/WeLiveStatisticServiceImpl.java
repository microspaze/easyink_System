package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.wecom.domain.live.WeLiveStatistic;
import com.easyink.wecom.mapper.WeLiveStatisticMapper;
import com.easyink.wecom.service.WeLiveStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 直播观看统计Service实现
 *
 * @author easyink
 */
@Slf4j
@Service
public class WeLiveStatisticServiceImpl extends ServiceImpl<WeLiveStatisticMapper, WeLiveStatistic> implements WeLiveStatisticService {

    @Override
    public List<WeLiveStatistic> selectStatisticByItemId(Long itemId, String corpId) {
        return baseMapper.selectStatisticByItemId(itemId, corpId);
    }

    @Override
    public List<WeLiveStatistic> selectStatisticByEmployee(String livingid, Long roomId, String corpId,
                                                           String beginTime, String endTime) {
        // 获取当前登录员工关联的客户统计
        return baseMapper.selectStatisticByUserIds(livingid, roomId, corpId, null, beginTime, endTime);
    }

    @Override
    public List<WeLiveStatistic> selectStatisticByDepartment(String livingid, Long roomId, String corpId,
                                                             List<String> userIdList, String beginTime, String endTime) {
        return baseMapper.selectStatisticByUserIds(livingid, roomId, corpId, userIdList, beginTime, endTime);
    }

    @Override
    public void saveOrUpdateStatistic(WeLiveStatistic statistic) {
        if (statistic == null || StringUtils.isBlank(statistic.getLivingid())
                || StringUtils.isBlank(statistic.getExternalUserid())) {
            return;
        }

        // 查询是否已存在(uk: livingid + external_userid)
        LambdaQueryWrapper<WeLiveStatistic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeLiveStatistic::getLivingid, statistic.getLivingid())
               .eq(WeLiveStatistic::getExternalUserid, statistic.getExternalUserid());
        WeLiveStatistic existing = getOne(wrapper);

        if (existing != null) {
            // 更新观看时长和评论次数(取较大值)
            LambdaQueryWrapper<WeLiveStatistic> updateWrapper = new LambdaQueryWrapper<>();
            updateWrapper.eq(WeLiveStatistic::getId, existing.getId());
            WeLiveStatistic update = new WeLiveStatistic();
            update.setId(existing.getId());
            update.setWatchTime(Math.max(existing.getWatchTime() != null ? existing.getWatchTime() : 0,
                    statistic.getWatchTime() != null ? statistic.getWatchTime() : 0));
            update.setCommentCount(Math.max(existing.getCommentCount() != null ? existing.getCommentCount() : 0,
                    statistic.getCommentCount() != null ? statistic.getCommentCount() : 0));
            update.setUpdateTime(new java.util.Date());
            updateById(update);
        } else {
            save(statistic);
        }
    }
}
