/*
 * 数据库名: fish_club_db
 * 字符集: utf8mb4
 * 排序规则: utf8mb4_general_ci
 * 描述: 钓鱼佬与空军俱乐部 - 核心数据库设计
 */
SET
    NAMES utf8mb4;

SET
    FOREIGN_KEY_CHECKS = 0;

-- =================================================================
-- 1. 系统基础模块 (用户、权限)
-- =================================================================
-- 表：用户信息表
DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
    `id` bigint NOT NULL COMMENT '主键ID (雪花算法)',
    `username` varchar(50) NOT NULL COMMENT '用户名/账号',
    `password` varchar(100) NOT NULL COMMENT '密码 (加密存储)',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL (MinIO)',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `signature` varchar(200) DEFAULT NULL COMMENT '个性签名',
    `role` tinyint DEFAULT 0 COMMENT '角色: 0-普通用户, 1-管理员',
    `is_master` tinyint DEFAULT 0 COMMENT '是否认证大师: 0-否, 1-是',
    `exp_points` int DEFAULT 0 COMMENT '经验值 (用于等级)',
    `status` tinyint DEFAULT 1 COMMENT '状态: 1-正常, 0-封禁',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint DEFAULT 0 COMMENT '逻辑删除: 0-未删, 1-已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户信息表';

-- 表：用户勋章表 (游戏化功能)
DROP TABLE IF EXISTS `sys_user_badge`;

CREATE TABLE `sys_user_badge` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `badge_name` varchar(50) NOT NULL COMMENT '勋章名称 (如: 空军司令)',
    `badge_icon` varchar(255) DEFAULT NULL COMMENT '勋章图标URL',
    `obtain_date` datetime DEFAULT NULL COMMENT '获得时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户勋章表';

-- =================================================================
-- 2. 核心业务模块 (社区、战报、空军)
-- =================================================================
-- 表：社区帖子表 (核心表)
DROP TABLE IF EXISTS `biz_post`;

CREATE TABLE `biz_post` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '发布者ID',
    `type` tinyint NOT NULL COMMENT '帖子类型: 0-鱼获战报, 1-空军吐槽, 2-装备测评',
    `title` varchar(100) DEFAULT NULL COMMENT '标题',
    `content` text COMMENT '内容',
    `images` json DEFAULT NULL COMMENT '图片列表 (JSON数组存储MinIO地址)',
    `fish_species` varchar(50) DEFAULT NULL COMMENT '鱼种 (AI识别或手动填写)',
    `fish_weight` decimal(10, 2) DEFAULT 0.00 COMMENT '鱼获重量 (斤)',
    `spot_id` bigint DEFAULT NULL COMMENT '关联钓点ID (可选)',
    `address_name` varchar(100) DEFAULT NULL COMMENT '地理位置名称',
    `view_count` int DEFAULT 0 COMMENT '浏览量',
    `like_count` int DEFAULT 0 COMMENT '点赞量',
    `comment_count` int DEFAULT 0 COMMENT '评论量',
    `ai_audit_status` tinyint DEFAULT 1 COMMENT 'AI审核状态: 0-违规, 1-正常, 2-疑似',
    `ai_audit_reason` varchar(255) DEFAULT NULL COMMENT 'AI审核反馈原因',
    `ai_comment` varchar(500) DEFAULT NULL COMMENT 'AI安慰语（空军帖子专用）',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_type` (`type`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '社区帖子表';

-- 表：帖子评论表
DROP TABLE IF EXISTS `biz_comment`;

CREATE TABLE `biz_comment` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `post_id` bigint NOT NULL COMMENT '帖子ID',
    `user_id` bigint NOT NULL COMMENT '评论者ID (如果是AI, 则为系统ID)',
    `parent_id` bigint DEFAULT 0 COMMENT '父评论ID',
    `content` varchar(500) NOT NULL COMMENT '评论内容',
    `is_ai_generated` tinyint DEFAULT 0 COMMENT '是否AI生成的回复: 0-否, 1-是',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '帖子评论表';

-- 表：点赞记录表 (防止重复点赞)
DROP TABLE IF EXISTS `biz_post_like`;

CREATE TABLE `biz_post_like` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `post_id` bigint NOT NULL COMMENT '帖子ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user` (`post_id`, `user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '点赞记录表';

-- =================================================================
-- 3. 地图与钓点模块
-- =================================================================
-- 表：钓点信息表
DROP TABLE IF EXISTS `biz_fishing_spot`;

CREATE TABLE `biz_fishing_spot` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `name` varchar(100) NOT NULL COMMENT '钓点名称',
    `type` tinyint NOT NULL COMMENT '类型: 0-野钓, 1-黑坑/收费, 2-路亚基地',
    `longitude` decimal(10, 6) NOT NULL COMMENT '经度 (高德/百度坐标)',
    `latitude` decimal(10, 6) NOT NULL COMMENT '纬度',
    `province` varchar(50) DEFAULT NULL COMMENT '省',
    `city` varchar(50) DEFAULT NULL COMMENT '市',
    `address` varchar(200) DEFAULT NULL COMMENT '详细地址',
    `price_desc` varchar(100) DEFAULT '免费' COMMENT '收费描述',
    `fish_info` varchar(200) DEFAULT NULL COMMENT '常见鱼种',
    `best_position_desc` text COMMENT 'AI推荐的最佳钓位描述',
    `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
    `status` tinyint DEFAULT 0 COMMENT '状态: 0-审核中, 1-已发布, 2-已下架',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_city` (`city`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '钓点地图表';

-- =================================================================
-- 4. 辅助数据与AI模块
-- =================================================================
-- 表：鱼类百科 (用于AI识鱼后的关联展示)
DROP TABLE IF EXISTS `base_fish_encyclopedia`;

CREATE TABLE `base_fish_encyclopedia` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `name` varchar(50) NOT NULL COMMENT '鱼类名称',
    `alias` varchar(100) DEFAULT NULL COMMENT '别名',
    `category` varchar(50) DEFAULT NULL COMMENT '科属',
    `protection_level` tinyint DEFAULT 0 COMMENT '保护级别: 0-普通, 1-保护动物(需放流)',
    `habits` text COMMENT '生活习性',
    `edible_value` varchar(200) DEFAULT NULL COMMENT '食用价值',
    `img_url` varchar(255) DEFAULT NULL COMMENT '标准图鉴',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '鱼类百科表';

-- 表：AI 使用日志 (用于管理端统计 Token 消耗)
DROP TABLE IF EXISTS `sys_ai_log`;

CREATE TABLE `sys_ai_log` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '调用用户ID',
    `function_type` varchar(50) NOT NULL COMMENT '功能类型: FISH_ID(识鱼), CHAT(聊天), AUDIT(审核)',
    `input_content` text COMMENT '用户输入内容(简略)',
    `output_content` text COMMENT 'AI返回内容(简略)',
    `status` tinyint DEFAULT 1 COMMENT '调用状态: 1-成功, 0-失败',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `is_deleted` tinyint DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'AI调用日志表';

-- 表：二手装备/闲置表
DROP TABLE IF EXISTS `biz_gear_market`;

CREATE TABLE `biz_gear_market` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '卖家ID',
    `title` varchar(100) NOT NULL COMMENT '商品标题',
    `description` text COMMENT '商品描述',
    `price` decimal(10, 2) NOT NULL COMMENT '价格',
    `original_price` decimal(10, 2) DEFAULT NULL COMMENT '原价',
    `images` json DEFAULT NULL COMMENT '商品图片',
    `category` varchar(20) DEFAULT NULL COMMENT '装备分类: rod-鱼竿, box-钓箱, bait-饵料, other-其他',
    `status` tinyint DEFAULT 0 COMMENT '状态: 0-在售, 1-已售出, 2-下架',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category` (`category`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '二手装备交易表';

-- 表：装备测评表
DROP TABLE IF EXISTS `biz_gear_review`;

CREATE TABLE `biz_gear_review` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `user_id` bigint NOT NULL COMMENT '测评用户ID',
    `title` varchar(100) NOT NULL COMMENT '测评标题',
    `content` text NOT NULL COMMENT '测评内容',
    `rating` decimal(2, 1) NOT NULL COMMENT '评分 (1-5分)',
    `gear_name` varchar(100) NOT NULL COMMENT '装备名称',
    `category` varchar(20) DEFAULT NULL COMMENT '装备分类: rod-鱼竿, box-钓箱, bait-饵料, other-其他',
    `views` int DEFAULT 0 COMMENT '浏览量',
    `likes` int DEFAULT 0 COMMENT '点赞量',
    `comments` int DEFAULT 0 COMMENT '评论量',
    `ai_analysis` json DEFAULT NULL COMMENT 'AI分析结果',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category` (`category`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '装备测评表';

-- 表：测评点赞记录表
DROP TABLE IF EXISTS `biz_review_like`;

CREATE TABLE `biz_review_like` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `review_id` bigint NOT NULL COMMENT '测评ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_review_user` (`review_id`, `user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '测评点赞记录表';

-- 表：测评评论表
DROP TABLE IF EXISTS `biz_review_comment`;

CREATE TABLE `biz_review_comment` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `review_id` bigint NOT NULL COMMENT '测评ID',
    `user_id` bigint NOT NULL COMMENT '评论者ID',
    `content` varchar(500) NOT NULL COMMENT '评论内容',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_review_id` (`review_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '测评评论表';