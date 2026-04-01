package com.easyink.wecom.factory.impl.customer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easyink.common.constant.Constants;
import com.easyink.common.constant.GenConstants;
import com.easyink.common.core.redis.RedisCache;
import com.easyink.common.enums.WeSensitiveActEnum;
import com.easyink.wecom.domain.WeCustomer;
import com.easyink.wecom.domain.WeSensitiveAct;
import com.easyink.wecom.domain.WeSensitiveActHit;
import com.easyink.wecom.domain.vo.WxCpXmlMessageVO;
import com.easyink.wecom.factory.WeEventStrategy;
import com.easyink.wecom.service.WeAdvertEntryService;
import com.easyink.wecom.service.WeCustomerService;
import com.easyink.wecom.service.WeCustomerTransferRecordService;
import com.easyink.wecom.service.WeFlowerCustomerRelService;
import com.easyink.wecom.service.WeSensitiveActHitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author admin
 * @description 删除企业客户事件
 * @date 2021/1/20 23:33
 **/
@Slf4j
@Component("del_external_contact")
public class WeCallBackDelExternalContactImpl extends WeEventStrategy {
    @Autowired
    private WeCustomerService weCustomerService;
    @Autowired
    private WeFlowerCustomerRelService weFlowerCustomerRelService;
    @Autowired
    private WeSensitiveActHitService weSensitiveActHitService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private WeCustomerTransferRecordService weCustomerTransferRecordService;
    @Autowired
    private WeAdvertEntryService weAdvertEntryService;
    /**
     * 表示此客户是因在职继承自动被转接成员删除
     */
    private final static String DELETE_BY_TRANSFER = "DELETE_BY_TRANSFER";

    @Override
    public void eventHandle(WxCpXmlMessageVO message) {
        if (message == null || StringUtils.isBlank(message.getToUserName())) {
            log.error("[del_external_contact]删除外部联系人回调消息缺失,message:{}", message);
            return;
        }
        if (!redisCache.addLock(message.getUniqueKey(message.getExternalUserId() + message.getUserId()), "", Constants.CALLBACK_HANDLE_LOCK_TIME)) {
            log.info("[del_external_contact]删除外部联系人事件回调,该回调已处理,不重复处理,message:{}", message);
            // 不重复处理
            return;
        }
        if (DELETE_BY_TRANSFER.equals(message.getSource())) {
            // 在职继承成功处理
            weCustomerTransferRecordService.handleTransferSuccess(message.getToUserName(), message.getUserId(), message.getExternalUserId());
        }
        if (message.getExternalUserId() != null && message.getUserId() != null) {
            weFlowerCustomerRelService.deleteCustomer(message.getUserId(), message.getExternalUserId(), Constants.DELETE_CODE, message.getToUserName());
            // 如果是在职继承的回调，不记录敏感记录，只更新客户状态
            if (DELETE_BY_TRANSFER.equals(message.getSource())) {
                return;
            }
            //增加敏感行为记录，员工删除客户
            WeSensitiveAct weSensitiveAct = weSensitiveActHitService.getSensitiveActType(WeSensitiveActEnum.DELETE.getInfo(), message.getToUserName());
            if (weSensitiveAct != null && WeSensitiveActEnum.OPEN.getCode().equals(weSensitiveAct.getEnableFlag())) {
                WeSensitiveActHit weSensitiveActHit = new WeSensitiveActHit();
                weSensitiveActHit.setSensitiveActId(weSensitiveAct.getId());
                weSensitiveActHit.setSensitiveAct(weSensitiveAct.getActName());
                weSensitiveActHit.setCreateTime(new Date(message.getCreateTime() * 1000L));
                weSensitiveActHit.setCreateBy("admin");
                weSensitiveActHit.setOperatorId(message.getUserId());
                weSensitiveActHit.setCorpId(message.getToUserName());
                weSensitiveActHit.setOperateTargetId(message.getExternalUserId());
                weSensitiveActHitService.setUserOrCustomerInfo(weSensitiveActHit);
                weSensitiveActHitService.insertWeSensitiveActHit(weSensitiveActHit);
            }
            // 更新广告记录的is_deleted字段
            updateAdvertEntryIsDeleted(message.getToUserName(), message.getExternalUserId());
        }
    }

    /**
     * 更新广告记录的is_deleted字段
     *
     * @param corpId         企业ID
     * @param externalUserId 客户externalUserId
     */
    private void updateAdvertEntryIsDeleted(String corpId, String externalUserId) {
        try {
            // 获取客户信息以获取unionid
            WeCustomer weCustomer = weCustomerService.getOne(new LambdaQueryWrapper<WeCustomer>()
                    .eq(WeCustomer::getExternalUserid, externalUserId)
                    .eq(WeCustomer::getCorpId, corpId)
                    .last(GenConstants.LIMIT_1));
            if (weCustomer != null && StringUtils.isNotBlank(weCustomer.getUnionid())) {
                weAdvertEntryService.updateIsDeletedByUnionid(weCustomer.getUnionid());
                log.info("[广告记录] 更新is_deleted成功，externalUserId:{}, unionid:{}", externalUserId, weCustomer.getUnionid());
            }
        } catch (Exception e) {
            log.error("[广告记录] 更新is_deleted异常，externalUserId:{}, e:{}", externalUserId, ExceptionUtils.getStackTrace(e));
        }
    }
}
