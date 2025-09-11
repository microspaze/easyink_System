package com.easyink.wecom.domain.dto.emplecode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 类名：FindWeEmpleCodeAnalyseDTO
 *
 * @author Society my sister Li
 * @date 2021-11-04 14:24
 */
@Data
@ApiModel("查询员工活码统计数据")
public class FindWeEmpleCodeAnalyseDTO {

    @ApiModelProperty(value = "state", required = true)
    @NotBlank(message = "state不能为空")
    private String state;

    @ApiModelProperty(value = "开始时间", required = true)
    @NotBlank(message = "beginTime不能为空")
    private String beginTime;

    @ApiModelProperty(value = "结束时间", required = true)
    @NotBlank(message = "endTime不能为空")
    private String endTime;

    @ApiModelProperty(value = "员工ID")
    private String userId;

    @ApiModelProperty(value = "部门ID")
    private String departmentId;

    @ApiModelProperty(value = "userId列表")
    private List<String> userIdList;

    @ApiModelProperty(hidden = true)
    private String corpId;

    @ApiModelProperty(value = "按员工统计")
    private Boolean byUser = false;
}
