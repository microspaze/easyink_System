package com.easyink.web.controller.wecom;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.common.core.domain.model.LoginUser;
import com.easyink.wecom.domain.live.WeLiveStatistic;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeLiveStatisticService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 直播观看统计Controller
 *
 * @author easyink
 */
@RestController
@RequestMapping("/wecom/live/statistic")
@Slf4j
@Api(tags = "直播观看统计")
public class WeLiveStatisticController extends BaseController {

    @Autowired
    private WeLiveStatisticService weLiveStatisticService;

    @GetMapping("/item/{itemId}")
    @ApiOperation("查询课程观看统计")
    @PreAuthorize("@ss.hasPermi('live:statistic:query')")
    public AjaxResult<List<WeLiveStatistic>> statisticByItem(@PathVariable Long itemId) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        return AjaxResult.success(weLiveStatisticService.selectStatisticByItemId(itemId, corpId));
    }

    @GetMapping("/customer")
    @ApiOperation("查询客户维度统计(员工查看关联客户)")
    @PreAuthorize("@ss.hasPermi('live:statistic:customer')")
    public TableDataInfo<WeLiveStatistic> statisticByCustomer(
            @RequestParam(required = false) String livingid,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime) {
        String corpId = LoginTokenService.getLoginUser().getCorpId();
        PageInfoUtil.setPage();
        List<WeLiveStatistic> list = weLiveStatisticService.selectStatisticByEmployee(
                livingid, roomId, corpId, beginTime, endTime);
        return getDataTable(list);
    }

    @GetMapping("/department")
    @ApiOperation("查询部门维度统计(部门管理员查看)")
    @PreAuthorize("@ss.hasPermi('live:statistic:department')")
    public TableDataInfo<WeLiveStatistic> statisticByDepartment(
            @RequestParam(required = false) String livingid,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime) {
        LoginUser loginUser = LoginTokenService.getLoginUser();
        String corpId = loginUser.getCorpId();
        List<String> dataScopeUserIds = loginUser.isSuperAdmin() ? null : loginUser.getDataScopeUserIds();
        PageInfoUtil.setPage();
        List<WeLiveStatistic> list = weLiveStatisticService.selectStatisticByDepartment(
                livingid, roomId, corpId, dataScopeUserIds, beginTime, endTime);
        return getDataTable(list);
    }
}
