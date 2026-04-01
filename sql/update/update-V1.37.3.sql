-- 渠道统计表添加24小时、48小时客户流失数、新客流失数
ALTER TABLE we_emple_code_statistic 
ADD COLUMN loss_24h_customer_cnt INT NOT NULL DEFAULT 0 COMMENT '24小时客户流失数',
ADD COLUMN loss_48h_customer_cnt INT NOT NULL DEFAULT 0 COMMENT '48小时客户流失数',
ADD COLUMN loss_new_customer_cnt INT NOT NULL DEFAULT 0 COMMENT '新客流失数';
