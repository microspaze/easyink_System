package com.easyink.wecom.domain.live;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 直播观看统计对象 we_live_statistic
 *
 * @author easyink
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("we_live_statistic")
@ApiModel("直播观看统计对象")
public class WeLiveStatistic {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("企业ID")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("企微直播ID")
    @TableField("livingid")
    private String livingid;

    @ApiModelProperty("关联课程实例ID")
    @TableField("item_id")
    private Long itemId;

    @ApiModelProperty("观看者外部联系人ID")
    @TableField("external_userid")
    private String externalUserid;

    @ApiModelProperty("进入直播间时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("enter_time")
    private Date enterTime;

    @ApiModelProperty("观看时长(秒)")
    @TableField("watch_time")
    private Integer watchTime;

    @ApiModelProperty("评论次数")
    @TableField("comment_count")
    private Integer commentCount;

    @ApiModelProperty("是否内部员工: 0-否 1-是")
    @TableField("is_internal")
    private Integer isInternal;

    @ApiModelProperty("邀请人userid")
    @TableField("inviter_userid")
    private String inviterUserid;

    @ApiModelProperty("邀请人external_userid")
    @TableField("inviter_external_userid")
    private String inviterExternalUserid;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private Date updateTime;

    /**
     * 观看者客户名称(非数据库字段)
     */
    @TableField(exist = false)
    private String customerName;

    /**
     * 邀请人名称(非数据库字段)
     */
    @TableField(exist = false)
    private String inviterName;
}
