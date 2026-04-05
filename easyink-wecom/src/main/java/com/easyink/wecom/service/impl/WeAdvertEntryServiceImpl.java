package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.exception.CustomException;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.domain.WeAdvertEntry;
import com.easyink.wecom.domain.WeEmpleCode;
import com.easyink.wecom.domain.dto.statistics.AdvertStatisticDTO;
import com.easyink.wecom.domain.dto.statistics.AdvertSyncDTO;
import com.easyink.wecom.domain.vo.statistics.advert.AdvertChannelVO;
import com.easyink.wecom.domain.vo.statistics.advert.AdvertStatisticVO;
import com.easyink.wecom.mapper.WeAdvertEntryMapper;
import com.easyink.wecom.mapper.WeEmpleCodeMapper;
import com.easyink.wecom.service.AdvertCallbackService;
import com.easyink.wecom.service.WeAdvertEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Resource
    private List<AdvertCallbackService> advertCallbackServices;

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

    @Override
    public boolean syncAdvertData(AdvertSyncDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getClickid())) {
            log.warn("[广告同步] 参数不完整，无法同步");
            return false;
        }

        try {
            // 根据clickid查询记录
            WeAdvertEntry existEntry = weAdvertEntryMapper.selectByClickid(dto.getClickid());

            if (existEntry != null) {
                // 更新现有记录
                updateExistEntry(existEntry, dto);
                log.info("[广告同步] 更新广告记录成功，clickid: {}", dto.getClickid());
            } else {
                // 创建新记录
                WeAdvertEntry newEntry = buildNewEntry(dto);
                weAdvertEntryMapper.insert(newEntry);
                log.info("[广告同步] 新增广告记录成功，clickid: {}", dto.getClickid());
            }
            return true;
        } catch (Exception e) {
            log.error("[广告同步] 同步广告数据异常，clickid: {}, error: {}", dto.getClickid(), e.getMessage());
            return false;
        }
    }

    /**
     * 更新现有记录
     */
    private void updateExistEntry(WeAdvertEntry existEntry, AdvertSyncDTO dto) {
        if (StringUtils.isNotBlank(dto.getPlatform())) {
            existEntry.setPlatform(dto.getPlatform());
        }
        if (StringUtils.isNotBlank(dto.getChannel())) {
            existEntry.setChannel(dto.getChannel());
        }
        if (StringUtils.isNotBlank(dto.getUnionid())) {
            existEntry.setUnionid(dto.getUnionid());
        }
        if (StringUtils.isNotBlank(dto.getMobile())) {
            existEntry.setMobile(dto.getMobile());
        }
        if (StringUtils.isNotBlank(dto.getRemark())) {
            existEntry.setRemark(dto.getRemark());
        }
        if (dto.getIsFormed() != null) {
            existEntry.setIsFormed(dto.getIsFormed());
        }
        if (dto.getIsPaid() != null) {
            existEntry.setIsPaid(dto.getIsPaid());
        }
        if (StringUtils.isNotBlank(dto.getCallbackType())) {
            existEntry.setCallbackType(dto.getCallbackType());
        }
        existEntry.setUpdateTime(new java.util.Date());
        weAdvertEntryMapper.updateById(existEntry);
    }

    /**
     * 构建新记录
     */
    private WeAdvertEntry buildNewEntry(AdvertSyncDTO dto) {
        WeAdvertEntry entry = new WeAdvertEntry();
        entry.setPlatform(dto.getPlatform());
        entry.setChannel(dto.getChannel());
        entry.setClickid(dto.getClickid());
        entry.setUnionid(dto.getUnionid());
        entry.setMobile(dto.getMobile());
        entry.setRemark(dto.getRemark());
        entry.setIsFormed(dto.getIsFormed() != null ? dto.getIsFormed() : 0);
        entry.setIsPaid(dto.getIsPaid() != null ? dto.getIsPaid() : 0);
        entry.setIsAdded(0);
        entry.setIsDeleted(0);
        entry.setIsCallbacked(0);
        entry.setCallbackType(dto.getCallbackType());
        entry.setCreateTime(new java.util.Date());
        return entry;
    }

    @Override
    public boolean executeAdvertCallback(String state, String unionid) {
        if (StringUtils.isBlank(state)) {
            log.warn("[广告回调] state为空，无法执行回调");
            return false;
        }

        // 查找需要回调的广告记录
        List<WeAdvertEntry> advertEntryList = new ArrayList<>();
        int hours = 2; // 最近2小时

        if (state.contains("_tg_")) {
            // 情况1：state包含_tg_，按_tg_切分
            String[] parts = state.split("_tg_");
            if (parts.length >= 2) {
                String channelState = parts[0];
                String secondPart = parts[1];

                // 第二部分如果长度为11则为手机号
                if (secondPart.length() == 11) {
                    String mobile = secondPart;
                    List<WeAdvertEntry> entries = weAdvertEntryMapper.selectByChannelMobileAndHours(channelState, mobile, hours);
                    if (entries != null && !entries.isEmpty()) {
                        advertEntryList.addAll(entries);
                    }
                    log.info("[广告回调] _tg_模式查找，channelState: {}, mobile: {}, 找到记录数: {}", channelState, mobile, advertEntryList.size());
                }
            }
        } else {
            // 情况2：通过unionid查找
            if (StringUtils.isNotBlank(unionid)) {
                List<WeAdvertEntry> entries = weAdvertEntryMapper.selectByUnionidAndHours(unionid, hours);
                if (entries != null && !entries.isEmpty()) {
                    advertEntryList.addAll(entries);
                }
                log.info("[广告回调] unionid模式查找，unionid: {}, 找到记录数: {}", unionid, advertEntryList.size());
            }
        }

        if (advertEntryList.isEmpty()) {
            log.info("[广告回调] 未找到需要回调的广告记录，state: {}, unionid: {}", state, unionid);
            return false;
        }

        // 执行回调
        boolean allSuccess = true;
        for (WeAdvertEntry entry : advertEntryList) {
            if (entry.getIsCallbacked() != null && entry.getIsCallbacked() == 1) {
                log.info("[广告回调] 记录已回调过，跳过，id: {}", entry.getId());
                continue;
            }

            boolean success = doCallback(entry);
            // 不管回调是否成功，都更新unionid、is_callbacked和is_added状态
            int isAdded = StringUtils.isNotBlank(unionid) ? 1 : 0;
            weAdvertEntryMapper.updateIsCallbacked(entry.getId(), success ? 1 : 0, unionid, isAdded);
            if (success) {
                log.info("[广告回调] 回调成功，记录id: {}", entry.getId());
            } else {
                allSuccess = false;
                log.warn("[广告回调] 回调失败，记录id: {}", entry.getId());
            }
        }

        return allSuccess;
    }

    /**
     * 执行具体的回调操作
     */
    private boolean doCallback(WeAdvertEntry entry) {
        if (entry == null || StringUtils.isBlank(entry.getPlatform())) {
            return false;
        }

        String platform = entry.getPlatform().toLowerCase();
        String clickId = entry.getClickid();
        String unionid = entry.getUnionid();
        String callbackType = entry.getCallbackType();

        // 获取对应的回调服务
        AdvertCallbackService callbackService = getCallbackService(platform);
        if (callbackService == null) {
            log.warn("[广告回调] 未找到对应的回调服务，platform: {}", platform);
            return false;
        }

        // 根据平台调用不同的回调方法
        if ("juldy".equals(platform)) {
            // 巨量引擎使用clickid作为callback参数
            return callbackService.executeCallback(unionid, clickId, callbackType);
        } else {
            // 广点通使用clickid
            return callbackService.executeCallback(unionid, clickId, callbackType);
        }
    }

    /**
     * 根据平台标识获取对应的回调服务
     */
    private AdvertCallbackService getCallbackService(String platform) {
        if (advertCallbackServices == null || advertCallbackServices.isEmpty()) {
            return null;
        }

        for (AdvertCallbackService service : advertCallbackServices) {
            if (service.getPlatform().equalsIgnoreCase(platform)) {
                return service;
            }
        }

        // 对于广点通，gdtwx和gdtqz都使用gdt的回调服务
        if ("gdtwx".equalsIgnoreCase(platform) || "gdtqz".equalsIgnoreCase(platform)) {
            for (AdvertCallbackService service : advertCallbackServices) {
                if ("gdt".equalsIgnoreCase(service.getPlatform())) {
                    return service;
                }
            }
        }

        return null;
    }
}