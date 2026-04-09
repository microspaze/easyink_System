package com.easyink.wecom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easyink.common.exception.CustomException;
import com.easyink.common.exception.wecom.WeComException;
import com.easyink.common.utils.StringUtils;
import com.easyink.wecom.client.WeLiveClient;
import com.easyink.wecom.domain.dto.live.WeMediaUploadResp;
import com.easyink.wecom.domain.live.WeLiveRoom;
import com.easyink.wecom.mapper.WeLiveRoomMapper;
import com.easyink.wecom.service.WeLiveRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * 直播间Service实现
 *
 * @author easyink
 */
@Slf4j
@Service
public class WeLiveRoomServiceImpl extends ServiceImpl<WeLiveRoomMapper, WeLiveRoom> implements WeLiveRoomService {

    private final WeLiveClient weLiveClient;

    public WeLiveRoomServiceImpl(WeLiveClient weLiveClient) {
        this.weLiveClient = weLiveClient;
    }

    @Override
    public boolean addRoom(WeLiveRoom weLiveRoom) {
        // 如果有海报图片URL但没有media_id,调用企微素材接口上传获取media_id
        if (StringUtils.isNotBlank(weLiveRoom.getDefaultPosterUrl())
                && StringUtils.isBlank(weLiveRoom.getDefaultPosterMediaId())) {
            try {
                File posterFile = new File(weLiveRoom.getDefaultPosterUrl());
                if (posterFile.exists()) {
                    WeMediaUploadResp resp = weLiveClient.uploadMedia(posterFile, "image", weLiveRoom.getCorpId());
                    if (resp != null && resp.isSuccess()) {
                        weLiveRoom.setDefaultPosterMediaId(resp.getMediaId());
                    }
                }
            } catch (Exception e) {
                log.error("上传海报图片到企微失败: {}", e.getMessage(), e);
            }
        }
        return save(weLiveRoom);
    }

    @Override
    public boolean editRoom(WeLiveRoom weLiveRoom) {
        // 如果海报图片URL变更,重新上传获取media_id
        WeLiveRoom existing = getById(weLiveRoom.getId());
        if (existing != null && StringUtils.isNotBlank(weLiveRoom.getDefaultPosterUrl())
                && !weLiveRoom.getDefaultPosterUrl().equals(existing.getDefaultPosterUrl())) {
            try {
                File posterFile = new File(weLiveRoom.getDefaultPosterUrl());
                if (posterFile.exists()) {
                    WeMediaUploadResp resp = weLiveClient.uploadMedia(posterFile, "image", weLiveRoom.getCorpId());
                    if (resp != null && resp.isSuccess()) {
                        weLiveRoom.setDefaultPosterMediaId(resp.getMediaId());
                    }
                }
            } catch (Exception e) {
                log.error("上传海报图片到企微失败: {}", e.getMessage(), e);
            }
        }
        return updateById(weLiveRoom);
    }

    @Override
    public boolean deleteRoomById(Long id) {
        WeLiveRoom room = new WeLiveRoom();
        room.setId(id);
        room.setDelFlag(1);
        return updateById(room);
    }

    @Override
    public WeLiveRoom getRoomById(Long id) {
        LambdaQueryWrapper<WeLiveRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeLiveRoom::getId, id)
               .eq(WeLiveRoom::getDelFlag, 0);
        return getOne(wrapper);
    }

    @Override
    public List<WeLiveRoom> selectRoomList(WeLiveRoom weLiveRoom) {
        LambdaQueryWrapper<WeLiveRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WeLiveRoom::getDelFlag, 0)
               .eq(StringUtils.isNotBlank(weLiveRoom.getCorpId()), WeLiveRoom::getCorpId, weLiveRoom.getCorpId())
               .like(StringUtils.isNotBlank(weLiveRoom.getRoomName()), WeLiveRoom::getRoomName, weLiveRoom.getRoomName())
               .orderByDesc(WeLiveRoom::getCreateTime);
        return list(wrapper);
    }
}
