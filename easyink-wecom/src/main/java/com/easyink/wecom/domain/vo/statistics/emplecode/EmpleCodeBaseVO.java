package com.easyink.wecom.domain.vo.statistics.emplecode;

import com.easyink.common.annotation.Excel;
import com.easyink.common.constant.GenConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 活码统计-基类VO
 *
 * @author lichaoyu
 * @date 2023/7/4 10:08
 */
@Data
@NoArgsConstructor
public class EmpleCodeBaseVO {

    @ApiModelProperty("累计添加客户")
    @Excel(name = "累计添加客户", sort = 2)
    private Integer accumulateCustomerCnt = 0;

    @ApiModelProperty("留存客户总数")
    @Excel(name = "留存客户总数", sort = 3)
    private Integer retainCustomerCnt = 0;
    
    @ApiModelProperty("新增客户数含已流失")
    @Excel(name = "新增客户数含已流失", sort = 4)
    private Integer newCustomerCnt = 0;

    @ApiModelProperty("流失客户数")
    @Excel(name = "流失客户数", sort = 5)
    private Integer lossCustomerCnt = 0;

    @ApiModelProperty("24小时客户流失数")
    @Excel(name = "24小时客户流失数", sort = 5)
    private Integer loss24hCustomerCnt = 0;

    @ApiModelProperty("48小时客户流失数")
    @Excel(name = "48小时客户流失数", sort = 5)
    private Integer loss48hCustomerCnt = 0;

    @ApiModelProperty("时间段内新增客户的流失数（用于计算新客留存率）")
    @Excel(name = "新增客户流失数", sort = 6)
    private Integer lossNewCustomerCnt = 0;

    @ApiModelProperty("截止当前时间，新增客户数")
    @Excel(name = "截止当前时间，新增客户数", sort = 6)
    private Integer currentNewCustomerCnt = 0;

    @ApiModelProperty("重复客户数")
    @Excel(name = "重复客户数", sort = 7)
    private Integer duplicateCustomerCnt = 0;

    @ApiModelProperty("新客留存率")
    @Excel(name = "新客留存率", sort = 8)
    private String retainNewCustomerRate;

    /**
     * 留存率为空时返回的值
     */
    private final String NULL_VALUE = "-";


    /**
     * 新客留存率 公式：截止当前时间，新增客户数 / 新增客户数
     */
    public String getRetainNewCustomerRate() {
        if (currentNewCustomerCnt == null || newCustomerCnt == null) {
            return NULL_VALUE;
        }
        BigDecimal percent = new BigDecimal(100);
        if(newCustomerCnt == 0) {
            return NULL_VALUE;
        }
        // 百分比
        BigDecimal currCntDecimal = new BigDecimal(currentNewCustomerCnt);
        BigDecimal newCntDecimal = new BigDecimal(newCustomerCnt);
        int scale = 2;
        // 计算留存率  截止当前时间,新增客户数 / 新客数
        return currCntDecimal
                .multiply(percent)
                .divide(newCntDecimal, scale, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    /**
     * 绑定导出数据
     * 导出框架不能直接使用get方法获取属性值
     */
    public void bindExportData() {
        if (newCustomerCnt == 0) {
            retainNewCustomerRate = NULL_VALUE;
        } else {
            retainNewCustomerRate = getRetainNewCustomerRate() + GenConstants.PERCENT;
        }
    }

    public EmpleCodeBaseVO(Integer accumulateCustomerCnt, Integer retainCustomerCnt, Integer newCustomerCnt, Integer lossCustomerCnt, Integer currentNewCustomerCnt) {
        this.accumulateCustomerCnt = accumulateCustomerCnt;
        this.retainCustomerCnt = retainCustomerCnt;
        this.newCustomerCnt = newCustomerCnt;
        this.lossCustomerCnt = lossCustomerCnt;
        this.currentNewCustomerCnt = currentNewCustomerCnt;
        this.retainNewCustomerRate = getRetainNewCustomerRate();
    }

    /**
     * 处理Redis数据
     *
     * @param redisNewCustomerCnt redis中的新增客户数
     * @param redisLossCustomerCnt redis中的流失客户数
     */
    public void handleRedisData(int redisNewCustomerCnt, int redisLossCustomerCnt) {
        // 累计客户数： Redis的新增客户数 + 原来的累计客户数
        this.accumulateCustomerCnt += redisNewCustomerCnt;
        // 留存客户数： (Redis的新增客户数 - Redis的流失客户数) + 原来的留存客户数
        this.retainCustomerCnt += (redisNewCustomerCnt - redisLossCustomerCnt);
        // 新增客户数： Redis的新增客户数 + 原来的新增客户数
        this.newCustomerCnt += redisNewCustomerCnt;
        // 流失客户数： Redis的流失客户数 + 原来的流失客户数
        this.lossCustomerCnt += redisLossCustomerCnt;
    }

    /**
     * 处理Redis数据（包含24h和48h流失）
     *
     * @param redisNewCustomerCnt redis中的新增客户数
     * @param redisLossCustomerCnt redis中的流失客户数
     * @param redisLoss24hCustomerCnt redis中的24h流失客户数
     * @param redisLoss48hCustomerCnt redis中的48h流失客户数
     */
    public void handleRedisData(int redisNewCustomerCnt, int redisLossCustomerCnt, int redisLoss24hCustomerCnt, int redisLoss48hCustomerCnt) {
        // 累计客户数： Redis的新增客户数 + 原来的累计客户数
        this.accumulateCustomerCnt += redisNewCustomerCnt;
        // 留存客户数： (Redis的新增客户数 - Redis的流失客户数) + 原来的留存客户数
        this.retainCustomerCnt += (redisNewCustomerCnt - redisLossCustomerCnt);
        // 新增客户数： Redis的新增客户数 + 原来的新增客户数
        this.newCustomerCnt += redisNewCustomerCnt;
        // 流失客户数： Redis的流失客户数 + 原来的流失客户数
        this.lossCustomerCnt += redisLossCustomerCnt;
        // 24h流失客户数： Redis的24h流失客户数 + 原来的24h流失客户数
        this.loss24hCustomerCnt += redisLoss24hCustomerCnt;
        // 48h流失客户数： Redis的48h流失客户数 + 原来的48h流失客户数
        this.loss48hCustomerCnt += redisLoss48hCustomerCnt;
    }

}
