package com.easyink.wecom.service.impl;

import com.easyink.common.exception.CustomException;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeAdvertEntry;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.dto.statistics.AdvertStatisticDTO;
import com.easyink.wecom.domain.vo.statistics.advert.AdvertChannelVO;
import com.easyink.wecom.domain.vo.statistics.advert.AdvertStatisticVO;
import com.easyink.wecom.mapper.WeAdvertEntryMapper;
import com.easyink.wecom.mapper.WeEmpleCodeMapper;
import com.easyink.wecom.service.WeAdvertEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 广告记录表Service业务处理
 *
 * @author admin
 * @date 2026-04-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeAdvertEntryServiceImpl implements WeAdvertEntryService {

    private final WeAdvertEntryMapper weAdvertEntryMapper;
    private final WeEmpleCodeMapper weEmpleCodeMapper;

    /**
     * 添加企微回调时更新is_added（最近2小时内的记录）
     */
    private static final int ADDED_HOURS = 2;

    /**
     * 删除企微回调时更新is_deleted（最近24小时内的记录）
     */
    private static final int DELETED_HOURS = 24;

    @Override
    public int updateIsAddedByUnionid(String unionid) {
        if (StringUtils.isBlank(unionid)) {
            log.warn("[广告记录] 更新is_added失败，unionid为空");
            return 0;
        }
        try {
            int updated = weAdvertEntryMapper.updateIsAddedByUnionid(unionid, ADDED_HOURS);
            if (updated > 0) {
                log.info("[广告记录] 更新is_added成功，unionid:{}, 更新条数:{}", unionid, updated);
            }
            return updated;
        } catch (Exception e) {
            log.error("[广告记录] 更新is_added异常，unionid:{}", unionid, e);
            return 0;
        }
    }

    @Override
    public int updateIsDeletedByUnionid(String unionid) {
        if (StringUtils.isBlank(unionid)) {
            log.warn("[广告记录] 更新is_deleted失败，unionid为空");
            return 0;
        }
        try {
            int updated = weAdvertEntryMapper.updateIsDeletedByUnionid(unionid, DELETED_HOURS);
            if (updated > 0) {
                log.info("[广告记录] 更新is_deleted成功，unionid:{}, 更新条数:{}", unionid, updated);
            }
            return updated;
        } catch (Exception e) {
            log.error("[广告记录] 更新is_deleted异常，unionid:{}", unionid, e);
            return 0;
        }
    }

    @Override
    public AdvertStatisticVO getAdvertTotal(AdvertStatisticDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (dto.getEmpleCodeIdList() == null || dto.getEmpleCodeIdList().isEmpty()) {
            return new AdvertStatisticVO();
        }

        // 获取state列表
        List<String> stateList = getStateListByEmpleCodeIds(dto.getCorpId(), dto.getEmpleCodeIdList());
        if (stateList == null || stateList.isEmpty()) {
            return new AdvertStatisticVO();
        }

        // 查询统计数据
        List<WeAdvertEntry> statisticList = weAdvertEntryMapper.selectAdvertStatisticByChannels(
                stateList, dto.getBeginDate(), dto.getEndDate());

        // 封装结果
        AdvertStatisticVO vo = new AdvertStatisticVO();
        if (statisticList != null && !statisticList.isEmpty()) {
            WeAdvertEntry entry = statisticList.get(0);
            vo.setTotalCnt(entry.getTotalCnt() != null ? entry.getTotalCnt().intValue() : 0);
            vo.setFormedCnt(entry.getFormedCnt() != null ? entry.getFormedCnt().intValue() : 0);
            vo.setPaidCnt(entry.getPaidCnt() != null ? entry.getPaidCnt().intValue() : 0);
            vo.setAddedCnt(entry.getAddedCnt() != null ? entry.getAddedCnt().intValue() : 0);
            vo.setDeletedCnt(entry.getDeletedCnt() != null ? entry.getDeletedCnt().intValue() : 0);
        }
        // 计算比率
        vo.calculateRates();

        return vo;
    }

    @Override
    public List<AdvertChannelVO> getAdvertChannelList(AdvertStatisticDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getCorpId())) {
            throw new CustomException(ResultTip.TIP_MISS_CORP_ID);
        }
        if (dto.getEmpleCodeIdList() == null || dto.getEmpleCodeIdList().isEmpty()) {
            return new ArrayList<>();
        }

        // 获取state列表
        List<String> stateList = getStateListByEmpleCodeIds(dto.getCorpId(), dto.getEmpleCodeIdList());
        if (stateList == null || stateList.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询按渠道分组的统计数据
        List<WeAdvertEntry> statisticList = weAdvertEntryMapper.selectAdvertStatisticGroupByChannel(
                stateList, dto.getBeginDate(), dto.getEndDate());

        if (statisticList == null || statisticList.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取活码ID和state的映射关系
        Map<String, WeEmpleCode> stateToEmpleCodeMap = getStateToEmpleCodeMap(dto.getCorpId(), dto.getEmpleCodeIdList());

        // 封装结果
        List<AdvertChannelVO> resultList = new ArrayList<>();
        for (WeAdvertEntry entry : statisticList) {
            AdvertChannelVO vo = new AdvertChannelVO();
            WeEmpleCode empleCode = stateToEmpleCodeMap.get(entry.getChannel());
            if (empleCode != null) {
                vo.setEmpleName(empleCode.getScenario());
                vo.setEmpleCodeId(empleCode.getId().toString());
            }
            vo.setTotalCnt(entry.getTotalCnt() != null ? entry.getTotalCnt().intValue() : 0);
            vo.setFormedCnt(entry.getFormedCnt() != null ? entry.getFormedCnt().intValue() : 0);
            vo.setPaidCnt(entry.getPaidCnt() != null ? entry.getPaidCnt().intValue() : 0);
            vo.setAddedCnt(entry.getAddedCnt() != null ? entry.getAddedCnt().intValue() : 0);
            vo.setDeletedCnt(entry.getDeletedCnt() != null ? entry.getDeletedCnt().intValue() : 0);
            // 计算比率
            vo.calculateRates();
            resultList.add(vo);
        }

        return resultList;
    }

    @Override
    public List<String> getStateListByEmpleCodeIds(String corpId, List<Long> empleCodeIdList) {
        if (StringUtils.isBlank(corpId) || empleCodeIdList == null || empleCodeIdList.isEmpty()) {
            return new ArrayList<>();
        }
        // 查询活码信息
        List<WeEmpleCode> empleCodeList = weEmpleCodeMapper.selectBatchIds(empleCodeIdList);
        if (empleCodeList == null || empleCodeList.isEmpty()) {
            return new ArrayList<>();
        }
        // 过滤出有效的state
        return empleCodeList.stream()
                .filter(e -> e.getState() != null && !e.getState().isEmpty())
                .map(WeEmpleCode::getState)
                .collect(Collectors.toList());
    }

    /**
     * 获取state到活码的映射
     */
    private Map<String, WeEmpleCode> getStateToEmpleCodeMap(String corpId, List<Long> empleCodeIdList) {
        List<WeEmpleCode> empleCodeList = weEmpleCodeMapper.selectBatchIds(empleCodeIdList);
        if (empleCodeList == null || empleCodeList.isEmpty()) {
            return new java.util.HashMap<>();
        }
        return empleCodeList.stream()
                .filter(e -> e.getState() != null && !e.getState().isEmpty())
                .collect(Collectors.toMap(
                        WeEmpleCode::getState,
                        e -> e,
                        (v1, v2) -> v1
                ));
    }
}