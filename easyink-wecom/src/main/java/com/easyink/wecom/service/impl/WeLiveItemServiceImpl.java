package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.enums.live.LiveStatusEnum;
import com.easyink.common.enums.live.TxTaskStatusEnum;
import com.easyink.common.utils.DateUtils;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.client.WeLiveClient;
import com.easyink.wecom.domain.dto.live.*;
import com.easyink.wecom.domain.live.WeLiveCourse;
import com.easyink.wecom.domain.live.WeLiveItem;
import com.easyink.wecom.domain.live.WeLiveRoom;
import com.easyink.wecom.domain.live.WeLiveStatistic;
import com.easyink.wecom.domain.vo.live.LivingCodeVO;
import com.easyink.wecom.mapper.WeLiveItemMapper;
import com.easyink.wecom.service.WeLiveCourseService;
import com.easyink.wecom.service.WeLiveItemService;
import com.easyink.wecom.service.WeLiveRoomService;
import com.easyink.wecom.service.WeLiveStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 直播课程实例Service实现
 *
 * @author easyink
 */
@Slf4j
@Service
public class WeLiveItemServiceImpl extends ServiceImpl<WeLiveItemMapper, WeLiveItem> implements WeLiveItemService {

    private final WeLiveRoomService weLiveRoomService;
    private final WeLiveCourseService weLiveCourseService;
    private final WeLiveStatisticService weLiveStatisticService;
    private final WeLiveClient weLiveClient;

    public WeLiveItemServiceImpl(WeLiveRoomService weLiveRoomService,
                                  WeLiveCourseService weLiveCourseService,
                                  WeLiveStatisticService weLiveStatisticService,
                                  WeLiveClient weLiveClient) {
        this.weLiveRoomService = weLiveRoomService;
        this.weLiveCourseService = weLiveCourseService;
        this.weLiveStatisticService = weLiveStatisticService;
        this.weLiveClient = weLiveClient;
    }

    @Override
    public List<WeLiveItem> selectItemList(WeLiveItem weLiveItem) {
        return baseMapper.selectItemListWithRoomName(weLiveItem);
    }

    @Override
    public WeLiveItem getItemById(Long id) {
        LambdaQueryWrapper<WeLiveItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeLiveItem::getId, id)
               .eq(WeLiveItem::getDelFlag, 0);
        return getOne(wrapper);
    }

    @Override
    public boolean cancelItem(Long id) {
        WeLiveItem item = getItemById(id);
        if (item == null) {
            return false;
        }
        // 调用企微取消直播接口
        if (StringUtils.isNotBlank(item.getLivingid())) {
            try {
                Map<String, String> req = new HashMap<>();
                req.put("livingid", item.getLivingid());
                weLiveClient.cancelLiving(req, item.getCorpId());
            } catch (Exception e) {
                log.error("取消企微直播失败, livingid={}: {}", item.getLivingid(), e.getMessage(), e);
            }
        }
        // 更新状态为取消
        LambdaUpdateWrapper<WeLiveItem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WeLiveItem::getId, id)
               .set(WeLiveItem::getLivingStatus, LiveStatusEnum.CANCELLED.getCode());
        return update(wrapper);
    }

    @Override
    public LivingCodeVO getLivingCodeByRoomId(Long roomId) {
        // 1. 查询最近2条课程
        List<WeLiveItem> items = baseMapper.selectLatestItemsByRoomId(roomId);
        if (CollectionUtils.isEmpty(items)) {
            return null;
        }

        Date now = new Date();
        Date after30Min = new Date(System.currentTimeMillis() + 30 * 60 * 1000);

        // 2. 优先: 半小时内即将开始
        WeLiveItem targetItem = null;
        for (WeLiveItem item : items) {
            if (LiveStatusEnum.NOT_START.getCode().equals(item.getLivingStatus())
                    && item.getCourseStartTime() != null
                    && item.getCourseStartTime().after(now)
                    && item.getCourseStartTime().before(after30Min)) {
                targetItem = item;
                break;
            }
        }

        // 3. 其次: 直播中
        if (targetItem == null) {
            for (WeLiveItem item : items) {
                if (LiveStatusEnum.LIVING.getCode().equals(item.getLivingStatus())) {
                    targetItem = item;
                    break;
                }
            }
        }

        // 4. 最后: 最新已结束
        if (targetItem == null) {
            for (WeLiveItem item : items) {
                if (LiveStatusEnum.ENDED.getCode().equals(item.getLivingStatus())) {
                    targetItem = item;
                    break;
                }
            }
        }

        if (targetItem == null || StringUtils.isBlank(targetItem.getLivingid())) {
            return null;
        }

        // 5. 调用企微获取living_code
        WeLivingCodeResp resp = weLiveClient.getLivingCode(targetItem.getLivingid(), targetItem.getCorpId());
        if (resp == null || !resp.isSuccess() || StringUtils.isBlank(resp.getLivingCode())) {
            log.error("获取living_code失败, livingid={}", targetItem.getLivingid());
            return null;
        }

        // 6. 构建返回
        LivingCodeVO vo = new LivingCodeVO();
        vo.setLivingCode(resp.getLivingCode());
        vo.setLivingid(targetItem.getLivingid());
        vo.setTitle(targetItem.getTitle());
        vo.setLivingStatus(targetItem.getLivingStatus());
        vo.setCourseStartTime(targetItem.getCourseStartTime());
        return vo;
    }

    @Override
    public void createDailyItems() {
        log.info("===== 开始创建每日直播课程 =====");
        // 1. 查询所有直播间(状态正常)
        LambdaQueryWrapper<WeLiveRoom> roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.eq(WeLiveRoom::getDelFlag, 0).eq(WeLiveRoom::getStatus, 0);
        List<WeLiveRoom> rooms = weLiveRoomService.list(roomWrapper);

        if (CollectionUtils.isEmpty(rooms)) {
            log.info("没有可用的直播间,跳过");
            return;
        }

        // 2. 按企业分组
        Map<String, List<WeLiveRoom>> corpRoomMap = rooms.stream()
                .collect(Collectors.groupingBy(WeLiveRoom::getCorpId));

        // 3. 获取今天是周几 (Calendar: 1=周日, 2=周一...7=周六, 转换为1=周一...7=周日)
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int customDay = dayOfWeek == 1 ? 7 : dayOfWeek - 1; // 转换为1=周一...7=周日
        String todayWeekDay = String.valueOf(customDay);
        String todayDate = DateUtils.dateTimeNow("yyyy-MM-dd");

        for (Map.Entry<String, List<WeLiveRoom>> entry : corpRoomMap.entrySet()) {
            String corpId = entry.getKey();
            List<WeLiveRoom> corpRooms = entry.getValue();

            for (WeLiveRoom room : corpRooms) {
                try {
                    // 查询该直播间下的课表
                    WeLiveCourse courseQuery = new WeLiveCourse();
                    courseQuery.setCorpId(corpId);
                    courseQuery.setRoomId(room.getId());
                    List<WeLiveCourse> courses = weLiveCourseService.selectCourseList(courseQuery);

                    for (WeLiveCourse course : courses) {
                        // 检查今天是否需要开课
                        if (!course.getWeekDays().contains(todayWeekDay)) {
                            continue;
                        }

                        // 检查是否已创建(幂等性)
                        if (baseMapper.countByCourseIdAndDate(course.getId(), todayDate) > 0) {
                            log.info("课程已存在,跳过. courseId={}, date={}", course.getId(), todayDate);
                            continue;
                        }

                        // 创建课程实例
                        createSingleItem(room, course, corpId);
                    }
                } catch (Exception e) {
                    log.error("处理直播间[{}]创建课程异常: {}", room.getId(), e.getMessage(), e);
                }
            }
        }
        log.info("===== 每日直播课程创建完成 =====");
    }

    /**
     * 创建单个课程实例
     */
    private void createSingleItem(WeLiveRoom room, WeLiveCourse course, String corpId) {
        try {
            // 构建今天的课程时间
            LocalDate today = LocalDate.now();
            LocalTime startTime = LocalTime.parse(course.getCourseStartTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            LocalTime endTime = LocalTime.parse(course.getCourseEndTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));

            Date startDateTime = Date.from(LocalDateTime.of(today, startTime).atZone(ZoneId.systemDefault()).toInstant());
            Date endDateTime = Date.from(LocalDateTime.of(today, endTime).atZone(ZoneId.systemDefault()).toInstant());

            // 先保存基础信息,将直播间的默认推流地址和播放地址复制到课程实例
            WeLiveItem item = WeLiveItem.builder()
                    .corpId(corpId)
                    .roomId(room.getId())
                    .courseId(course.getId())
                    .title(course.getTitle())
                    .description(course.getDescription())
                    .speakerName(room.getSpeakerName())
                    .operatorUserid(room.getOperatorUserid())
                    .posterMediaId(room.getDefaultPosterMediaId())
                    .posterUrl(room.getDefaultPosterUrl())
                    .pushStreamUrl(room.getDefaultPushUrl())
                    .playStreamUrl(room.getDefaultPlayUrl())
                    .courseStartTime(startDateTime)
                    .courseEndTime(endDateTime)
                    .generateRecording(course.getGenerateRecording())
                    .livingStatus(LiveStatusEnum.NOT_START.getCode())
                    .delFlag(0)
                    .build();
            save(item);

            // 调用企微创建预约直播
            WeCreateLivingReq req = WeCreateLivingReq.builder()
                    .anchorUserid(room.getOperatorUserid())
                    .posterMediaId(room.getDefaultPosterMediaId())
                    .description(course.getDescription())
                    .starttime(startDateTime.getTime() / 1000)
                    .endtime(endDateTime.getTime() / 1000)
                    .build();

            WeCreateLivingResp createResp = weLiveClient.createLiving(req, corpId);
            if (createResp == null || !createResp.isSuccess() || StringUtils.isBlank(createResp.getLivingid())) {
                log.error("创建企微预约直播失败, courseId={}", course.getId());
                return;
            }

            String livingid = createResp.getLivingid();

            // 获取直播详情(企微推流地址)
            String wecomPushStreamUrl = null;
            try {
                Thread.sleep(1000); // 等待1秒确保企微数据生效
                WeLivingInfoResp infoResp = weLiveClient.getLivingInfo(livingid, corpId);
                if (infoResp != null && infoResp.isSuccess()) {
                    wecomPushStreamUrl = infoResp.getPushStreamUrl();
                }
            } catch (Exception e) {
                log.error("获取直播详情失败, livingid={}: {}", livingid, e.getMessage(), e);
            }

            // 合并转推流地址列表: 企微推流地址作为转推目标 + 直播间配置的默认转推地址
            List<String> transPushUrls = new ArrayList<>();
            if (StringUtils.isNotBlank(wecomPushStreamUrl)) {
                transPushUrls.add(wecomPushStreamUrl);
            }
            if (CollectionUtils.isNotEmpty(room.getDefaultTransPushUrls())) {
                transPushUrls.addAll(room.getDefaultTransPushUrls());
            }

            // 更新课程实例(使用实体对象更新以正确处理TypeHandler)
            WeLiveItem updateItem = new WeLiveItem();
            updateItem.setId(item.getId());
            updateItem.setLivingid(livingid);
            updateItem.setTransPushUrls(transPushUrls);
            updateById(updateItem);

            // 当转推流地址不为空时,调用腾讯云直播API创建转推流任务
            // 拉流地址为playStreamUrl(直播间的默认播放地址),转推到transPushUrls中的地址(主要是企微直播)
            if (CollectionUtils.isNotEmpty(transPushUrls) && StringUtils.isNotBlank(item.getPlayStreamUrl())) {
                createTxPullStreamTask(item.getId(), item.getPlayStreamUrl(), transPushUrls, startDateTime, endDateTime, corpId);
            }

            log.info("创建课程实例成功, itemId={}, livingid={}", item.getId(), livingid);
        } catch (Exception e) {
            log.error("创建课程实例异常, roomId={}, courseId={}: {}", room.getId(), course.getId(), e.getMessage(), e);
        }
    }

    @Override
    public void monitorLiveStatus() {
        log.info("===== 开始监控直播状态 =====");
        // 查询未开始且近30分钟内即将开始的课程
        LambdaQueryWrapper<WeLiveItem> notStartWrapper = new LambdaQueryWrapper<>();
        notStartWrapper.eq(WeLiveItem::getDelFlag, 0)
                .eq(WeLiveItem::getLivingStatus, LiveStatusEnum.NOT_START.getCode())
                .isNotNull(WeLiveItem::getLivingid)
                .le(WeLiveItem::getCourseStartTime, new Date(System.currentTimeMillis() + 30 * 60 * 1000));
        List<WeLiveItem> notStartItems = list(notStartWrapper);

        for (WeLiveItem item : notStartItems) {
            try {
                WeLivingInfoResp resp = weLiveClient.getLivingInfo(item.getLivingid(), item.getCorpId());
                if (resp != null && resp.isSuccess()) {
                    Integer newStatus = mapWeComStatus(resp.getStatus());
                    if (newStatus != null && !newStatus.equals(item.getLivingStatus())) {
                        updateItemStatus(item.getId(), newStatus);
                    }
                }
            } catch (Exception e) {
                log.error("监控直播状态异常, itemId={}: {}", item.getId(), e.getMessage(), e);
            }
        }

        // 查询直播中的课程,检测是否已结束
        LambdaQueryWrapper<WeLiveItem> livingWrapper = new LambdaQueryWrapper<>();
        livingWrapper.eq(WeLiveItem::getDelFlag, 0)
                .eq(WeLiveItem::getLivingStatus, LiveStatusEnum.LIVING.getCode())
                .isNotNull(WeLiveItem::getLivingid);
        List<WeLiveItem> livingItems = list(livingWrapper);

        for (WeLiveItem item : livingItems) {
            try {
                WeLivingInfoResp resp = weLiveClient.getLivingInfo(item.getLivingid(), item.getCorpId());
                if (resp != null && resp.isSuccess()) {
                    if (resp.getStatus() != null && resp.getStatus() == 2) {
                        updateItemStatus(item.getId(), LiveStatusEnum.ENDED.getCode());
                    }
                }
            } catch (Exception e) {
                log.error("监控直播状态异常, itemId={}: {}", item.getId(), e.getMessage(), e);
            }
        }
        log.info("===== 直播状态监控完成 =====");
    }

    @Override
    public void collectWatchStatistic() {
        log.info("===== 开始采集直播观看统计 =====");
        // 查询直播中的课程
        LambdaQueryWrapper<WeLiveItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeLiveItem::getDelFlag, 0)
                .eq(WeLiveItem::getLivingStatus, LiveStatusEnum.LIVING.getCode())
                .isNotNull(WeLiveItem::getLivingid);
        List<WeLiveItem> livingItems = list(wrapper);

        for (WeLiveItem item : livingItems) {
            try {
                collectWatchStatForItem(item);
            } catch (Exception e) {
                log.error("采集观看统计异常, itemId={}: {}", item.getId(), e.getMessage(), e);
            }
        }

        // 也处理已结束但近1小时内的课程(获取最终数据)
        LambdaQueryWrapper<WeLiveItem> endedWrapper = new LambdaQueryWrapper<>();
        endedWrapper.eq(WeLiveItem::getDelFlag, 0)
                .eq(WeLiveItem::getLivingStatus, LiveStatusEnum.ENDED.getCode())
                .isNotNull(WeLiveItem::getLivingid)
                .ge(WeLiveItem::getUpdateTime, new Date(System.currentTimeMillis() - 60 * 60 * 1000));
        List<WeLiveItem> endedItems = list(endedWrapper);

        for (WeLiveItem item : endedItems) {
            try {
                collectWatchStatForItem(item);
            } catch (Exception e) {
                log.error("采集已结束课程观看统计异常, itemId={}: {}", item.getId(), e.getMessage(), e);
            }
        }
        log.info("===== 直播观看统计采集完成 =====");
    }

    /**
     * 采集单个课程的观看统计
     */
    private void collectWatchStatForItem(WeLiveItem item) {
        String nextKey = null;
        do {
            WeWatchStatReq req = WeWatchStatReq.builder()
                    .livingid(item.getLivingid())
                    .nextKey(nextKey)
                    .build();
            WeWatchStatResp resp = weLiveClient.getWatchStat(req, item.getCorpId());
            if (resp == null || !resp.isSuccess() || CollectionUtils.isEmpty(resp.getStatInfo())) {
                break;
            }

            for (WeWatchStatResp.WatchStatItem statItem : resp.getStatInfo()) {
                WeLiveStatistic statistic = WeLiveStatistic.builder()
                        .corpId(item.getCorpId())
                        .livingid(item.getLivingid())
                        .itemId(item.getId())
                        .externalUserid(statItem.getExternalUserid())
                        .enterTime(statItem.getEnterTime() != null ? new Date(statItem.getEnterTime() * 1000L) : null)
                        .watchTime(statItem.getWatchTime())
                        .commentCount(statItem.getCommentCount())
                        .isInternal(statItem.getIsInternal())
                        .inviterUserid(statItem.getInviterUserid())
                        .inviterExternalUserid(statItem.getInviterExternalUserid())
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();
                weLiveStatisticService.saveOrUpdateStatistic(statistic);
            }

            // 判断是否还有更多数据
            if (resp.getEnding() != null && resp.getEnding() == 0 && StringUtils.isNotBlank(resp.getNextKey())) {
                nextKey = resp.getNextKey();
            } else {
                break;
            }
        } while (true);
    }

    /**
     * 更新课程直播状态
     */
    private void updateItemStatus(Long itemId, Integer newStatus) {
        LambdaUpdateWrapper<WeLiveItem> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(WeLiveItem::getId, itemId)
               .set(WeLiveItem::getLivingStatus, newStatus);
        update(wrapper);
        log.info("更新课程状态, itemId={}, newStatus={}", itemId, newStatus);
    }

    /**
     * 企微直播状态映射
     */
    private Integer mapWeComStatus(Integer wecomStatus) {
        if (wecomStatus == null) {
            return null;
        }
        switch (wecomStatus) {
            case 0:
                return LiveStatusEnum.NOT_START.getCode();
            case 1:
                return LiveStatusEnum.LIVING.getCode();
            case 2:
                return LiveStatusEnum.ENDED.getCode();
            case 3:
                return LiveStatusEnum.EXPIRED.getCode();
            default:
                return null;
        }
    }

    /**
     * 调用腾讯云直播API创建转推流任务
     * 以playStreamUrl为拉流源,转推到transPushUrls中的地址
     *
     * @param itemId        课程实例ID
     * @param playStreamUrl 拉流地址(播放地址)
     * @param transPushUrls 转推目标地址列表
     * @param startTime     课程开始时间
     * @param endTime       课程结束时间
     * @param corpId        企业ID
     */
    private void createTxPullStreamTask(Long itemId, String playStreamUrl, List<String> transPushUrls,
                                         Date startTime, Date endTime, String corpId) {
        try {
            // TODO: 调用腾讯云直播 CreateLivePullStreamTask API
            //  请求参数:
            //   - PullStreamUrl: playStreamUrl (拉流地址)
            //   - PushUrlList: transPushUrls (转推目标地址列表)
            //   - StartTime: startTime.getTime() / 1000
            //   - EndTime: endTime.getTime() / 1000
            //  返回值中获取 TaskId
            String taskId = null; // 临时占位,等API调用实现后赋值

            if (StringUtils.isNotBlank(taskId)) {
                // 更新转推流任务ID和状态
                LambdaUpdateWrapper<WeLiveItem> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(WeLiveItem::getId, itemId)
                       .set(WeLiveItem::getTxTaskId, taskId)
                       .set(WeLiveItem::getTxTaskStatus, TxTaskStatusEnum.RUNNING.getCode());
                update(wrapper);
                log.info("创建腾讯云转推流任务成功, itemId={}, taskId={}, pullUrl={}, pushUrls={}",
                        itemId, taskId, playStreamUrl, transPushUrls);
            } else {
                log.warn("创建腾讯云转推流任务未返回taskId, itemId={}", itemId);
            }
        } catch (Exception e) {
            // 创建失败,更新任务状态为失败
            LambdaUpdateWrapper<WeLiveItem> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(WeLiveItem::getId, itemId)
                   .set(WeLiveItem::getTxTaskStatus, TxTaskStatusEnum.FAILED.getCode());
            update(wrapper);
            log.error("创建腾讯云转推流任务异常, itemId={}: {}", itemId, e.getMessage(), e);
        }
    }
}
