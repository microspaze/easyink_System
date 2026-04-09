package com.easyink.wecom.domain.live;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.easyink.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 直播间对象 we_live_room
 *
 * @author easyink
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "we_live_room", autoResultMap = true)
@ApiModel("直播间对象")
public class WeLiveRoom extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("企业ID")
    @TableField("corp_id")
    private String corpId;

    @ApiModelProperty("直播间名称")
    @NotBlank(message = "直播间名称不能为空")
    @TableField("room_name")
    private String roomName;

    @ApiModelProperty("讲课人姓名")
    @NotBlank(message = "讲课人姓名不能为空")
    @TableField("speaker_name")
    private String speakerName;

    @ApiModelProperty("概要描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("运营人企微账号userid")
    @TableField("operator_userid")
    private String operatorUserid;

    @ApiModelProperty("默认海报图片URL(原始上传地址)")
    @TableField("default_poster_url")
    private String defaultPosterUrl;

    @ApiModelProperty("默认海报企微素材ID")
    @TableField("default_poster_media_id")
    private String defaultPosterMediaId;

    @ApiModelProperty("默认推流地址(企微)")
    @TableField("default_push_url")
    private String defaultPushUrl;

    @ApiModelProperty("默认转推流地址JSON数组")
    @TableField(value = "default_trans_push_urls", typeHandler = com.easyink.wecom.handler.JsonStringListTypeHandler.class)
    private List<String> defaultTransPushUrls;

    @ApiModelProperty("状态: 0-正常 1-停用")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("删除标志: 0-未删除 1-已删除")
    @TableField("del_flag")
    private Integer delFlag;
}
