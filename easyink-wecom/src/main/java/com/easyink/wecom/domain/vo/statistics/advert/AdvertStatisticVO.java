package com.easyink.wecom.domain.vo.statistics.advert;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 广告统计-数据总览VO
 *
 * @author admin
 * @date 2026-04-01
 */
@Data
public class AdvertStatisticVO {

    @ApiModelProperty("进线数")
    private Integer totalCnt = 0;

    @ApiModelProperty("表单提交数")
    private Integer formedCnt = 0;

    @ApiModelProperty("表单提交率")
    private String formedRate;

    @ApiModelProperty("订单支付数")
    private Integer paidCnt = 0;

    @ApiModelProperty("订单支付率")
    private String paidRate;

    @ApiModelProperty("企微添加数")
    private Integer addedCnt = 0;

    @ApiModelProperty("企微添加率")
    private String addedRate;

    @ApiModelProperty("删除数")
    private Integer deletedCnt = 0;

    @ApiModelProperty("删除率")
    private String deletedRate;

    @ApiModelProperty("无效数")
    private Integer invalidCnt = 0;

    @ApiModelProperty("无效率")
    private String invalidRate;

    /**
     * 百分比为空时返回的值
     */
    @ApiModelProperty("空值占位符")
    private final String NULL_VALUE = "-";

    /**
     * 计算并设置各项比率
     */
    public void calculateRates() {
        if (totalCnt == null || totalCnt == 0) {
            formedRate = NULL_VALUE;
            paidRate = NULL_VALUE;
            addedRate = NULL_VALUE;
            deletedRate = NULL_VALUE;
            invalidRate = NULL_VALUE;
            return;
        }

        formedRate = calculateRate(formedCnt, totalCnt);
        paidRate = calculateRate(paidCnt, totalCnt);
        addedRate = calculateRate(addedCnt, totalCnt);
        deletedRate = calculateRate(deletedCnt, totalCnt);
        invalidCnt = totalCnt - (addedCnt != null ? addedCnt : 0);
        invalidRate = calculateRate(invalidCnt, totalCnt);
    }

    /**
     * 计算比率
     *
     * @param numerator   分子
     * @param denominator 分母
     * @return 百分比字符串
     */
    private String calculateRate(Integer numerator, Integer denominator) {
        if (numerator == null || denominator == 0) {
            return NULL_VALUE;
        }
        BigDecimal percent = new BigDecimal(100);
        BigDecimal numeratorDecimal = new BigDecimal(numerator);
        BigDecimal denominatorDecimal = new BigDecimal(denominator);
        return numeratorDecimal
                .multiply(percent)
                .divide(denominatorDecimal, 2, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }
}