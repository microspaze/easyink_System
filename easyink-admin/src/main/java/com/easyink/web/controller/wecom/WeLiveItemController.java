package com.easyink.web.controller.wecom;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.wecom.domain.live.WeLiveItem;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeLiveItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 直播课程实例Controller
 *
 * @author easyink
 */
@RestController
@RequestMapping("/wecom/live/item")
@Slf4j
@Api(tags = "直播课程管理")
public class WeLiveItemController extends BaseController {

    @Autowired
    private WeLiveItemService weLiveItemService;

    @GetMapping("/list")
    @ApiOperation("查询课程列表")
    @PreAuthorize("@ss.hasPermi('live:item:list')")
    public TableDataInfo<WeLiveItem> list(WeLiveItem weLiveItem) {
        weLiveItem.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageInfoUtil.setPage();
        List<WeLiveItem> list = weLiveItemService.selectItemList(weLiveItem);
        return getDataTable(list);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取课程详情")
    @PreAuthorize("@ss.hasPermi('live:item:query')")
    public AjaxResult<WeLiveItem> getInfo(@PathVariable Long id) {
        return AjaxResult.success(weLiveItemService.getItemById(id));
    }

    @PostMapping("/cancel/{id}")
    @ApiOperation("取消课程")
    @PreAuthorize("@ss.hasPermi('live:item:cancel')")
    @Log(title = "取消课程", businessType = BusinessType.UPDATE)
    public AjaxResult cancel(@PathVariable Long id) {
        return toAjax(weLiveItemService.cancelItem(id) ? 1 : 0);
    }
}
