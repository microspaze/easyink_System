package com.easyink.web.controller.wecom;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.wecom.domain.live.WeLiveRoom;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeLiveRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 直播间Controller
 *
 * @author easyink
 */
@RestController
@RequestMapping("/wecom/live/room")
@Slf4j
@Api(tags = "直播间管理")
public class WeLiveRoomController extends BaseController {

    @Autowired
    private WeLiveRoomService weLiveRoomService;

    @PostMapping("/add")
    @ApiOperation("创建直播间")
    @PreAuthorize("@ss.hasPermi('live:room:add')")
    @Log(title = "创建直播间", businessType = BusinessType.INSERT)
    public AjaxResult add(@Validated @RequestBody WeLiveRoom weLiveRoom) {
        weLiveRoom.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weLiveRoom.setCreateBy(LoginTokenService.getLoginUser());
        return toAjax(weLiveRoomService.addRoom(weLiveRoom) ? 1 : 0);
    }

    @PutMapping("/edit")
    @ApiOperation("修改直播间")
    @PreAuthorize("@ss.hasPermi('live:room:edit')")
    @Log(title = "修改直播间", businessType = BusinessType.UPDATE)
    public AjaxResult edit(@Validated @RequestBody WeLiveRoom weLiveRoom) {
        weLiveRoom.setUpdateBy(LoginTokenService.getLoginUser());
        return toAjax(weLiveRoomService.editRoom(weLiveRoom) ? 1 : 0);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除直播间")
    @PreAuthorize("@ss.hasPermi('live:room:remove')")
    @Log(title = "删除直播间", businessType = BusinessType.DELETE)
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(weLiveRoomService.deleteRoomById(id) ? 1 : 0);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取直播间详情")
    @PreAuthorize("@ss.hasPermi('live:room:query')")
    public AjaxResult<WeLiveRoom> getInfo(@PathVariable Long id) {
        return AjaxResult.success(weLiveRoomService.getRoomById(id));
    }

    @GetMapping("/list")
    @ApiOperation("查询直播间列表")
    @PreAuthorize("@ss.hasPermi('live:room:list')")
    public TableDataInfo<WeLiveRoom> list(WeLiveRoom weLiveRoom) {
        weLiveRoom.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageInfoUtil.setPage();
        List<WeLiveRoom> list = weLiveRoomService.selectRoomList(weLiveRoom);
        return getDataTable(list);
    }
}
