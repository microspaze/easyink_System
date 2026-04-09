-- =============================================
-- 企微直播集成 数据库表
-- =============================================

-- ----------------------------
-- 1. 直播间表
-- ----------------------------
DROP TABLE IF EXISTS `we_live_room`;
CREATE TABLE `we_live_room` (
    `id`                        BIGINT          NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
    `corp_id`                   VARCHAR(64)     NOT NULL                    COMMENT '企业ID',
    `room_name`                 VARCHAR(128)    NOT NULL                    COMMENT '直播间名称',
    `speaker_name`              VARCHAR(64)     NOT NULL                    COMMENT '讲课人姓名',
    `description`               VARCHAR(512)    DEFAULT NULL                COMMENT '概要描述',
    `operator_userid`           VARCHAR(64)     DEFAULT NULL                COMMENT '运营人企微账号userid',
    `default_poster_url`        VARCHAR(512)    DEFAULT NULL                COMMENT '默认海报图片URL(原始上传地址)',
    `default_poster_media_id`   VARCHAR(128)    DEFAULT NULL                COMMENT '默认海报企微素材ID(调用素材接口后获取)',
    `default_push_url`          VARCHAR(512)    DEFAULT NULL                COMMENT '默认推流地址(企微)',
    `default_trans_push_urls`   VARCHAR(2048)   DEFAULT NULL                COMMENT '默认转推流地址JSON数组,如["rtmp://qiniu.xxx/live1","rtmp://qiniu.xxx/live2"]',
    `status`                    TINYINT         DEFAULT 0                   COMMENT '状态: 0-正常 1-停用',
    `del_flag`                  TINYINT         DEFAULT 0                   COMMENT '删除标志: 0-未删除 1-已删除',
    `create_by`                 VARCHAR(64)     DEFAULT ''                  COMMENT '创建者',
    `create_time`               DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    `update_by`                 VARCHAR(64)     DEFAULT ''                  COMMENT '更新者',
    `update_time`               DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_corp_id` (`corp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播间表';

-- ----------------------------
-- 2. 直播课表(课表)
-- ----------------------------
DROP TABLE IF EXISTS `we_live_course`;
CREATE TABLE `we_live_course` (
    `id`                    BIGINT          NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
    `corp_id`               VARCHAR(64)     NOT NULL                    COMMENT '企业ID',
    `room_id`               BIGINT          NOT NULL                    COMMENT '关联直播间ID',
    `title`                 VARCHAR(128)    NOT NULL                    COMMENT '课程标题',
    `description`           VARCHAR(512)    DEFAULT NULL                COMMENT '课程描述',
    `start_time`            TIME            NOT NULL                    COMMENT '开课时间(如09:00:00)',
    `end_time`              TIME            NOT NULL                    COMMENT '结课时间(如11:00:00)',
    `week_days`             VARCHAR(20)     NOT NULL                    COMMENT '开课周几,多个用逗号分隔(1-7,1=周一)',
    `generate_recording`    TINYINT         DEFAULT 0                   COMMENT '是否生成录音: 0-否 1-是',
    `status`                TINYINT         DEFAULT 0                   COMMENT '状态: 0-正常 1-停用',
    `del_flag`              TINYINT         DEFAULT 0                   COMMENT '删除标志: 0-未删除 1-已删除',
    `create_by`             VARCHAR(64)     DEFAULT ''                  COMMENT '创建者',
    `create_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    `update_by`             VARCHAR(64)     DEFAULT ''                  COMMENT '更新者',
    `update_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_corp_id` (`corp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播课表';

-- ----------------------------
-- 3. 直播课程实例表(每天生成的课程)
-- ----------------------------
DROP TABLE IF EXISTS `we_live_item`;
CREATE TABLE `we_live_item` (
    `id`                    BIGINT          NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
    `corp_id`               VARCHAR(64)     NOT NULL                    COMMENT '企业ID',
    `room_id`               BIGINT          NOT NULL                    COMMENT '关联直播间ID',
    `course_id`             BIGINT          NOT NULL                    COMMENT '关联课表ID',
    `livingid`              VARCHAR(128)    DEFAULT NULL                COMMENT '企微直播ID(创建预约直播后返回)',
    `title`                 VARCHAR(128)    NOT NULL                    COMMENT '课程标题',
    `description`           VARCHAR(512)    DEFAULT NULL                COMMENT '课程描述',
    `speaker_name`          VARCHAR(64)     DEFAULT NULL                COMMENT '讲课人姓名(冗余)',
    `operator_userid`       VARCHAR(64)     DEFAULT NULL                COMMENT '运营人userid(冗余)',
    `poster_media_id`       VARCHAR(128)    DEFAULT NULL                COMMENT '海报企微素材ID',
    `poster_url`            VARCHAR(512)    DEFAULT NULL                COMMENT '海报图片URL',
    `push_stream_url`       VARCHAR(512)    DEFAULT NULL                COMMENT '企微推流地址(从直播详情获取)',
    `trans_push_urls`       VARCHAR(2048)   DEFAULT NULL                COMMENT '转推流地址JSON数组,如["rtmp://wecom.xxx","rtmp://qiniu.xxx/live1"]',
    `start_time`            DATETIME        NOT NULL                    COMMENT '课程开始时间',
    `end_time`              DATETIME        NOT NULL                    COMMENT '课程结束时间',
    `generate_recording`    TINYINT         DEFAULT 0                   COMMENT '是否生成录音: 0-否 1-是',
    `living_status`         TINYINT         DEFAULT 0                   COMMENT '直播状态: 0-未开始 1-直播中 2-已结束 3-已过期 4-取消',
    `tx_task_id`            VARCHAR(128)    DEFAULT NULL                COMMENT '腾讯云转推流任务ID',
    `tx_task_status`        TINYINT         DEFAULT 0                   COMMENT '转推流任务状态: 0-未创建 1-运行中 2-已停止 3-失败',
    `del_flag`              TINYINT         DEFAULT 0                   COMMENT '删除标志: 0-未删除 1-已删除',
    `create_by`             VARCHAR(64)     DEFAULT ''                  COMMENT '创建者',
    `create_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    `update_by`             VARCHAR(64)     DEFAULT ''                  COMMENT '更新者',
    `update_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_corp_id` (`corp_id`),
    KEY `idx_livingid` (`livingid`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_living_status` (`living_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播课程实例表';

-- ----------------------------
-- 4. 直播观看统计表
-- ----------------------------
DROP TABLE IF EXISTS `we_live_statistic`;
CREATE TABLE `we_live_statistic` (
    `id`                        BIGINT          NOT NULL AUTO_INCREMENT    COMMENT '主键ID(自增)',
    `corp_id`                   VARCHAR(64)     NOT NULL                    COMMENT '企业ID',
    `livingid`                  VARCHAR(128)    NOT NULL                    COMMENT '企微直播ID',
    `item_id`                   BIGINT          DEFAULT NULL                COMMENT '关联课程实例ID',
    `external_userid`           VARCHAR(128)    DEFAULT NULL                COMMENT '观看者外部联系人ID',
    `enter_time`                DATETIME        DEFAULT NULL                COMMENT '进入直播间时间',
    `watch_time`                INT             DEFAULT 0                   COMMENT '观看时长(秒)',
    `comment_count`             INT             DEFAULT 0                   COMMENT '评论次数',
    `is_internal`               TINYINT         DEFAULT 0                   COMMENT '是否内部员工: 0-否 1-是',
    `inviter_userid`            VARCHAR(64)     DEFAULT NULL                COMMENT '邀请人userid',
    `inviter_external_userid`   VARCHAR(128)    DEFAULT NULL                COMMENT '邀请人external_userid',
    `create_time`               DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    `update_time`               DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_livingid_external_userid` (`livingid`, `external_userid`),
    KEY `idx_corp_id` (`corp_id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_inviter_userid` (`inviter_userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播观看统计表';

-- ----------------------------
-- 企微直播 菜单权限记录
-- 父级: 运营中心(menu_id=2188)
-- 菜单ID从2400开始,避免与现有数据冲突(当前最大2338)
-- ----------------------------

-- 一级目录: 企微直播(挂在运营中心下)
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2400, '企微直播', 2188, 32, 'live', NULL, 1, 'M', '0', '0', '', 'live', 'admin', NOW(), '', NULL, '企微直播目录');

-- 二级菜单: 直播间管理
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2401, '直播间管理', 2400, 1, 'room', 'live/room/index', 1, 'C', '0', '0', 'live:room:list', '#', 'admin', NOW(), '', NULL, '直播间管理菜单');

-- 三级按钮: 直播间管理按钮权限
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2402, '直播间查询', 2401, 1, '', NULL, 1, 'F', '0', '0', 'live:room:list', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2403, '新增直播间', 2401, 2, '', NULL, 1, 'F', '0', '0', 'live:room:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2404, '编辑直播间', 2401, 3, '', NULL, 1, 'F', '0', '0', 'live:room:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2405, '删除直播间', 2401, 4, '', NULL, 1, 'F', '0', '0', 'live:room:remove', '#', 'admin', NOW(), '', NULL, '');

-- 二级菜单: 课表管理
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2406, '课表管理', 2400, 2, 'course', 'live/course/index', 1, 'C', '0', '0', 'live:course:list', '#', 'admin', NOW(), '', NULL, '课表管理菜单');

-- 三级按钮: 课表管理按钮权限
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2407, '课表查询', 2406, 1, '', NULL, 1, 'F', '0', '0', 'live:course:list', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2408, '新增课表', 2406, 2, '', NULL, 1, 'F', '0', '0', 'live:course:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2409, '编辑课表', 2406, 3, '', NULL, 1, 'F', '0', '0', 'live:course:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2410, '删除课表', 2406, 4, '', NULL, 1, 'F', '0', '0', 'live:course:remove', '#', 'admin', NOW(), '', NULL, '');

-- 二级菜单: 课程列表
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2411, '课程列表', 2400, 3, 'item', 'live/item/index', 1, 'C', '0', '0', 'live:item:list', '#', 'admin', NOW(), '', NULL, '课程列表菜单');

-- 三级按钮: 课程列表按钮权限
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2412, '课程查询', 2411, 1, '', NULL, 1, 'F', '0', '0', 'live:item:list', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2413, '课程详情', 2411, 2, '', NULL, 1, 'F', '0', '0', 'live:item:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2414, '取消直播', 2411, 3, '', NULL, 1, 'F', '0', '0', 'live:item:cancel', '#', 'admin', NOW(), '', NULL, '');

-- 二级菜单: 观看统计
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2415, '观看统计', 2400, 4, 'statistic', 'live/statistic/index', 1, 'C', '0', '0', 'live:statistic:customer', '#', 'admin', NOW(), '', NULL, '观看统计菜单');

-- 三级按钮: 观看统计按钮权限
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2416, '客户维度统计', 2415, 1, '', NULL, 1, 'F', '0', '0', 'live:statistic:customer', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2417, '部门维度统计', 2415, 2, '', NULL, 1, 'F', '0', '0', 'live:statistic:department', '#', 'admin', NOW(), '', NULL, '');

