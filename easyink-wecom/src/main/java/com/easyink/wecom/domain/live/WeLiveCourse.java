package com.easyink.wecom.domain.live;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.utils.SnowFlakeUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * 直播课表对象 we_live_course
 *
 * @author easyink
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_live_course")
@ApiModel("直播课表对象")
public class WeLiveCourse extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("企业ID")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("关联直播间ID")
    @NotNull(message = "直播间ID不能为空")
    @TableField("room_id")
    private Long roomId;

    @ApiModelProperty("课程标题")
    @NotBlank(message = "课程标题不能为空")
    @TableField("title")
    private String title;

    @ApiModelProperty("课程描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("开课时间(如09:00:00)")
    @NotBlank(message = "开课时间不能为空")
    @TableField("start_time")
    private String startTime;

    @ApiModelProperty("结课时间(如11:00:00)")
    @NotBlank(message = "结课时间不能为空")
    @TableField("end_time")
    private String endTime;

    @ApiModelProperty("开课周几,多个用逗号分隔(1-7,1=周一)")
    @NotBlank(message = "开课周几不能为空")
    @TableField("week_days")
    private String weekDays;

    @ApiModelProperty("是否生成录音: 0-否 1-是")
    @TableField("generate_recording")
    private Integer generateRecording;

    @ApiModelProperty("状态: 0-正常 1-停用")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("删除标志: 0-未删除 1-已删除")
    @TableField("del_flag")
    private Integer delFlag;

    /**
     * 直播间名称(非数据库字段,用于列表展示)
     */
    @TableField(exist = false)
    private String roomName;
}
