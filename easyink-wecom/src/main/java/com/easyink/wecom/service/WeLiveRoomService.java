package com.easyink.wecom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easyink.wecom.domain.live.WeLiveRoom;

import java.util.List;

/**
 * 直播间Service接口
 *
 * @author easyink
 */
public interface WeLiveRoomService extends IService<WeLiveRoom> {

    /**
     * 创建直播间
     *
     * @param weLiveRoom 直播间信息
     * @return 是否成功
     */
    boolean addRoom(WeLiveRoom weLiveRoom);

    /**
     * 修改直播间
     *
     * @param weLiveRoom 直播间信息
     * @return 是否成功
     */
    boolean editRoom(WeLiveRoom weLiveRoom);

    /**
     * 删除直播间(逻辑删除)
     *
     * @param id 直播间ID
     * @return 是否成功
     */
    boolean deleteRoomById(Long id);

    /**
     * 获取直播间详情
     *
     * @param id 直播间ID
     * @return 直播间信息
     */
    WeLiveRoom getRoomById(Long id);

    /**
     * 查询直播间列表
     *
     * @param weLiveRoom 查询条件
     * @return 直播间列表
     */
    List<WeLiveRoom> selectRoomList(WeLiveRoom weLiveRoom);
}
