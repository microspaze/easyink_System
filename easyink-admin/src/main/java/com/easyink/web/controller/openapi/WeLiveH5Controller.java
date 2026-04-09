package com.easyink.web.controller.openapi;

import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.wecom.domain.vo.live.LivingCodeVO;
import com.easyink.wecom.service.WeLiveItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * H5开放接口 - 直播凭证
 *
 * @author easyink
 */
@RestController
@RequestMapping("/open/live")
@Slf4j
@Api(tags = "H5直播开放接口")
public class WeLiveH5Controller extends BaseController {

    @Autowired
    private WeLiveItemService weLiveItemService;

    @GetMapping("/livingCode")
    @ApiOperation("通过roomId获取直播凭证living_code(5分钟有效)")
    public AjaxResult<LivingCodeVO> getLivingCode(
            @ApiParam("直播间ID") @NotNull(message = "直播间ID不能为空") @RequestParam Long roomId) {
        LivingCodeVO vo = weLiveItemService.getLivingCodeByRoomId(roomId);
        if (vo == null) {
            return AjaxResult.error("暂无可用的直播课程");
        }
        return AjaxResult.success(vo);
    }
}
