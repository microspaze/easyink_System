package com.easyink.wecom.domain.live;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import com.easyink.common.utils.SnowFlakeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 直播课程实例对象 we_live_item
 *
 * @author easyink
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "we_live_item", autoResultMap = true)
@ApiModel("直播课程实例对象")
public class WeLiveItem extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("企业ID")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("关联直播间ID")
    @TableField("room_id")
    private Long roomId;

    @ApiModelProperty("关联课表ID")
    @TableField("course_id")
    private Long courseId;

    @ApiModelProperty("企微直播ID")
    @TableField("livingid")
    private String livingid;

    @ApiModelProperty("课程标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("课程描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("讲课人姓名(冗余)")
    @TableField("speaker_name")
    private String speakerName;

    @ApiModelProperty("运营人userid(冗余)")
    @TableField("operator_userid")
    private String operatorUserid;

    @ApiModelProperty("海报企微素材ID")
    @TableField("poster_media_id")
    private String posterMediaId;

    @ApiModelProperty("海报图片URL")
    @TableField("poster_url")
    private String posterUrl;

    @ApiModelProperty("企微推流地址")
    @TableField("push_stream_url")
    private String pushStreamUrl;

    @ApiModelProperty("转推流地址JSON数组")
    @TableField(value = "trans_push_urls", typeHandler = com.easyink.wecom.handler.JsonStringListTypeHandler.class)
    private List<String> transPushUrls;

    @ApiModelProperty("课程开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty("课程结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty("是否生成录音: 0-否 1-是")
    @TableField("generate_recording")
    private Integer generateRecording;

    @ApiModelProperty("直播状态: 0-未开始 1-直播中 2-已结束 3-已过期 4-取消")
    @TableField("living_status")
    private Integer livingStatus;

    @ApiModelProperty("腾讯云转推流任务ID")
    @TableField("tx_task_id")
    private String txTaskId;

    @ApiModelProperty("转推流任务状态: 0-未创建 1-运行中 2-已停止 3-失败")
    @TableField("tx_task_status")
    private Integer txTaskStatus;

    @ApiModelProperty("删除标志: 0-未删除 1-已删除")
    @TableField("del_flag")
    private Integer delFlag;

    /**
     * 直播间名称(非数据库字段)
     */
    @TableField(exist = false)
    private String roomName;
}
