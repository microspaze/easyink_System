package com.easyink.web.controller.wecom;

import com.easyink.common.annotation.Log;
import com.easyink.common.core.controller.BaseController;
import com.easyink.common.core.domain.AjaxResult;
import com.easyink.common.core.page.TableDataInfo;
import com.easyink.common.enums.BusinessType;
import com.easyink.common.utils.PageInfoUtil;
import com.easyink.wecom.domain.live.WeLiveCourse;
import com.easyink.wecom.login.util.LoginTokenService;
import com.easyink.wecom.service.WeLiveCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 直播课表Controller
 *
 * @author easyink
 */
@RestController
@RequestMapping("/wecom/live/course")
@Slf4j
@Api(tags = "直播课表管理")
public class WeLiveCourseController extends BaseController {

    @Autowired
    private WeLiveCourseService weLiveCourseService;

    @PostMapping("/add")
    @ApiOperation("添加课表")
    @PreAuthorize("@ss.hasPermi('live:course:add')")
    @Log(title = "添加课表", businessType = BusinessType.INSERT)
    public AjaxResult add(@Validated @RequestBody WeLiveCourse weLiveCourse) {
        weLiveCourse.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        weLiveCourse.setCreateBy(LoginTokenService.getLoginUser());
        return toAjax(weLiveCourseService.addCourse(weLiveCourse) ? 1 : 0);
    }

    @PutMapping("/edit")
    @ApiOperation("修改课表")
    @PreAuthorize("@ss.hasPermi('live:course:edit')")
    @Log(title = "修改课表", businessType = BusinessType.UPDATE)
    public AjaxResult edit(@Validated @RequestBody WeLiveCourse weLiveCourse) {
        weLiveCourse.setUpdateBy(LoginTokenService.getLoginUser());
        return toAjax(weLiveCourseService.editCourse(weLiveCourse) ? 1 : 0);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除课表")
    @PreAuthorize("@ss.hasPermi('live:course:remove')")
    @Log(title = "删除课表", businessType = BusinessType.DELETE)
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(weLiveCourseService.deleteCourseById(id) ? 1 : 0);
    }

    @GetMapping("/list")
    @ApiOperation("查询课表列表")
    @PreAuthorize("@ss.hasPermi('live:course:list')")
    public TableDataInfo<WeLiveCourse> list(WeLiveCourse weLiveCourse) {
        weLiveCourse.setCorpId(LoginTokenService.getLoginUser().getCorpId());
        PageInfoUtil.setPage();
        List<WeLiveCourse> list = weLiveCourseService.selectCourseList(weLiveCourse);
        return getDataTable(list);
    }
}
