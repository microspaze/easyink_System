package com.easyink.web.controller.system;

import com.easyink.common.config.AdvertConfig;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.enums.ResultTip;
import com.easyink.common.exception.CustomException;
import com.easyink.wecom.domain.dto.statistics.AdvertSyncDTO;
import com.easyink.wecom.service.WeAdvertEntryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 广告数据Controller
 *
 * @author admin
 */
@Api(tags = "广告数据接口")
@RestController
@RequestMapping("/system/advert")
public class SysAdvertController {

    private final WeAdvertEntryService weAdvertEntryService;
    private final AdvertConfig advertConfig;

    @Autowired
    public SysAdvertController(WeAdvertEntryService weAdvertEntryService, AdvertConfig advertConfig) {
        this.weAdvertEntryService = weAdvertEntryService;
        this.advertConfig = advertConfig;
    }

    @GetMapping("/sync")
    @ApiOperation("广告数据同步接口")
    public AjaxResult<Void> syncAdvertData(@RequestParam String token, @RequestBody @Validated AdvertSyncDTO dto) {
        // 验证token
        if (advertConfig == null || !advertConfig.getSyncToken().equals(token)) {
            throw new CustomException(ResultTip.TIP_GENERAL_FORBIDDEN);
        }
        boolean success = weAdvertEntryService.syncAdvertData(dto);
        if (success) {
            return AjaxResult.success("数据同步成功");
        } else {
            return AjaxResult.error("数据同步失败");
        }
    }
}
