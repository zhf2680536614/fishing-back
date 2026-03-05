SET FOREIGN_KEY_CHECKS = 0;
SET NAMES utf8mb4;
-- fish_club_db DDL
CREATE DATABASE `fish_club_db`
CHARACTER SET utf8mb4
COLLATE utf8mb4_0900_ai_ci;;
use `fish_club_db`;

-- fish_club_db.base_fish_encyclopedia DDL
CREATE TABLE `fish_club_db`.`base_fish_encyclopedia` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `name` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "鱼类名称",
  `alias` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "别名",
  `category` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "科属",
  `protection_level` TINYINT NULL DEFAULT 0 COMMENT "保护级别: 0-普通, 1-保护动物(需放流)",
  `habits` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "生活习性",
  `edible_value` VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "食用价值",
  `img_url` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "标准图鉴",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  INDEX `idx_name`(`name` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "鱼类百科表";

-- fish_club_db.biz_comment DDL
CREATE TABLE `fish_club_db`.`biz_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `post_id` BIGINT NOT NULL COMMENT "帖子ID",
  `user_id` BIGINT NOT NULL COMMENT "评论者ID (如果是AI, 则为系统ID)",
  `parent_id` BIGINT NULL DEFAULT 0 COMMENT "父评论ID",
  `content` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "评论内容",
  `is_ai_generated` TINYINT NULL DEFAULT 0 COMMENT "是否AI生成的回复: 0-否, 1-是",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "帖子评论表";

-- fish_club_db.biz_fishing_spot DDL
CREATE TABLE `fish_club_db`.`biz_fishing_spot` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `name` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "钓点名称",
  `type` BIGINT NOT NULL COMMENT "钓点类型，关联sys_dict_item.id",
  `longitude` DECIMAL(10,6) NOT NULL COMMENT "经度 (高德/百度坐标)",
  `latitude` DECIMAL(10,6) NOT NULL COMMENT "纬度",
  `province` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "省",
  `city` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "市",
  `address` VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "详细地址",
  `price_desc` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '免费' COMMENT "收费描述",
  `fish_info` JSON NULL COMMENT "常见鱼种，JSON数组存储鱼类百科ID(base_fish_encyclopedia.id)",
  `images` JSON NULL COMMENT "钓点图片列表 (JSON数组存储MinIO地址)",
  `best_position_desc` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "AI推荐的最佳钓位描述",
  `creator_id` BIGINT NULL COMMENT "创建人ID",
  `status` BIGINT NULL DEFAULT 1 COMMENT "钓点状态，关联sys_dict_item.id",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  INDEX `idx_city`(`city` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "钓点地图表";

-- fish_club_db.biz_gear_market DDL
CREATE TABLE `fish_club_db`.`biz_gear_market` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `user_id` BIGINT NOT NULL COMMENT "卖家ID",
  `title` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "商品标题",
  `description` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "商品描述",
  `price` DECIMAL(10,2) NOT NULL COMMENT "价格",
  `original_price` DECIMAL(10,2) NULL COMMENT "原价",
  `images` JSON NULL COMMENT "商品图片",
  `status` BIGINT NULL DEFAULT 1 COMMENT "商品状态，关联sys_dict_item.id",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  `category` BIGINT NULL COMMENT "装备分类，关联sys_dict_item.id",
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "二手装备交易表";

-- fish_club_db.biz_gear_review DDL
CREATE TABLE `fish_club_db`.`biz_gear_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `user_id` BIGINT NOT NULL COMMENT "测评用户ID",
  `title` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "测评标题",
  `content` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "测评内容",
  `rating` DECIMAL(2,1) NOT NULL COMMENT "评分 (1-5分)",
  `gear_name` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "装备名称",
  `category` BIGINT NULL COMMENT "装备分类，关联sys_dict_item.id",
  `status` BIGINT NULL DEFAULT 1 COMMENT "测评状态，关联sys_dict_item.id",
  `ai_analysis` JSON NULL COMMENT "AI分析结果",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  `images` JSON NULL COMMENT "测评图片 (JSON数组存储MinIO地址)",
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "装备测评表";

-- fish_club_db.biz_order DDL
CREATE TABLE `fish_club_db`.`biz_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `user_id` BIGINT NOT NULL COMMENT "用户ID",
  `gear_id` BIGINT NOT NULL COMMENT "装备ID",
  `gear_title` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "装备标题",
  `gear_price` DECIMAL(10,2) NOT NULL COMMENT "装备价格",
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT "总金额",
  `status` BIGINT NOT NULL DEFAULT 1 COMMENT "订单状态，关联sys_dict_item.id",
  `address` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "收货地址",
  `contact_phone` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "联系电话",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  INDEX `idx_gear_id`(`gear_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "订单表";

-- fish_club_db.biz_post DDL
CREATE TABLE `fish_club_db`.`biz_post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `user_id` BIGINT NOT NULL COMMENT "发布者ID",
  `type` BIGINT NOT NULL COMMENT "帖子类型，关联sys_dict_item.id",
  `title` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "标题",
  `content` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "内容",
  `images` JSON NULL COMMENT "图片列表 (JSON数组存储MinIO地址)",
  `fish_species` BIGINT NULL COMMENT "鱼种，关联base_fish_encyclopedia.id",
  `fish_weight` DECIMAL(10,2) NULL DEFAULT 0.00 COMMENT "鱼获重量 (斤)",
  `spot_id` BIGINT NULL COMMENT "关联钓点ID (可选)",
  `address_name` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "地理位置名称",
  `view_count` INT NULL DEFAULT 0 COMMENT "浏览量",
  `like_count` INT NULL DEFAULT 0 COMMENT "点赞量",
  `comment_count` INT NULL DEFAULT 0 COMMENT "评论量",
  `ai_audit_status` BIGINT NULL DEFAULT 2 COMMENT "AI审核状态，关联sys_dict_item.id",
  `ai_audit_reason` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "AI审核反馈原因",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  `ai_comment` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "AI安慰语（空军帖子专用）",
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "社区帖子表";

-- fish_club_db.biz_post_like DDL
CREATE TABLE `fish_club_db`.`biz_post_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `post_id` BIGINT NOT NULL COMMENT "帖子ID",
  `user_id` BIGINT NOT NULL COMMENT "用户ID",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  UNIQUE INDEX `uk_post_user`(`post_id` ASC,`user_id` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "点赞记录表";

-- fish_club_db.biz_user_address DDL
CREATE TABLE `fish_club_db`.`biz_user_address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `user_id` BIGINT NOT NULL COMMENT "用户ID",
  `name` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "收货人姓名",
  `phone` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "联系电话",
  `province` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "省份",
  `city` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "城市",
  `district` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "区县",
  `detail_address` VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "详细地址",
  `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT "是否默认地址：0-否，1-是",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "用户收货地址表";

-- fish_club_db.sys_ai_log DDL
CREATE TABLE `fish_club_db`.`sys_ai_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `user_id` BIGINT NOT NULL COMMENT "调用用户ID",
  `function_type` BIGINT NOT NULL COMMENT "功能类型，关联sys_dict_item.id",
  `input_content` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "用户输入内容(简略)",
  `output_content` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "AI返回内容(简略)",
  `status` BIGINT NULL DEFAULT 1 COMMENT "调用状态，关联sys_dict_item.id",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "AI调用日志表";

-- fish_club_db.sys_badge_definition DDL
CREATE TABLE `fish_club_db`.`sys_badge_definition` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `badge_name` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "勋章名称",
  `badge_icon` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "勋章图标URL",
  `description` VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "勋章描述",
  `requirement_type` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "需求类型: fishing_days, fish_days, air_force_days, total_weight",
  `requirement_value` DECIMAL(10,2) NOT NULL COMMENT "需求值",
  `sort_order` INT NULL DEFAULT 0 COMMENT "排序顺序",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  UNIQUE INDEX `uk_badge_name`(`badge_name` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "勋章定义表";

-- fish_club_db.sys_dict_type DDL
CREATE TABLE `fish_club_db`.`sys_dict_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `dict_code` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "字典类型编码",
  `dict_name` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "字典类型名称",
  `parent_id` BIGINT NULL DEFAULT 0 COMMENT "父级字典类型ID，0表示顶级",
  `description` VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "描述",
  `sort_order` INT NULL DEFAULT 0 COMMENT "排序顺序",
  `status` TINYINT NULL DEFAULT 1 COMMENT "状态：1-启用，0-禁用",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  UNIQUE INDEX `uk_dict_code` (`dict_code` ASC) USING BTREE,
  INDEX `idx_parent_id` (`parent_id` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "数据字典类型表";

-- fish_club_db.sys_dict_item DDL
CREATE TABLE `fish_club_db`.`sys_dict_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `dict_type_id` BIGINT NOT NULL COMMENT "字典类型ID",
  `item_code` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "字典项编码",
  `item_name` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "字典项名称",
  `value` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "字典项值",
  `description` VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "描述",
  `sort_order` INT NULL DEFAULT 0 COMMENT "排序顺序",
  `status` TINYINT NULL DEFAULT 1 COMMENT "状态：1-启用，0-禁用",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  UNIQUE INDEX `uk_dict_type_item` (`dict_type_id` ASC, `item_code` ASC) USING BTREE,
  INDEX `idx_dict_type_id` (`dict_type_id` ASC) USING BTREE,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_dict_item_type` FOREIGN KEY (`dict_type_id`) REFERENCES `sys_dict_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "数据字典项表";

-- fish_club_db.sys_user DDL
CREATE TABLE `fish_club_db`.`sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `username` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "用户名/账号",
  `password` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT "密码 (加密存储)",
  `nickname` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "昵称",
  `avatar` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "头像URL (MinIO)",
  `phone` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "手机号",
  `signature` VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT "个性签名",
  `role` BIGINT NULL DEFAULT 1 COMMENT "角色，关联sys_dict_item.id",
  `is_master` TINYINT NULL DEFAULT 0 COMMENT "是否认证大师: 0-否, 1-是",
  `exp_points` INT NULL DEFAULT 0 COMMENT "经验值 (用于等级)",
  `status` BIGINT NULL DEFAULT 1 COMMENT "用户状态，关联sys_dict_item.id",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "用户信息表";

-- fish_club_db.sys_user_badge DDL
CREATE TABLE `fish_club_db`.`sys_user_badge` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT "主键ID",
  `user_id` BIGINT NOT NULL COMMENT "用户ID",
  `badge_id` BIGINT NOT NULL COMMENT "勋章定义ID",
  `obtain_date` DATETIME NULL COMMENT "获得时间",
  `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间",
  `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT "更新时间",
  `is_deleted` TINYINT NULL DEFAULT 0 COMMENT "是否删除: 0-未删, 1-已删",
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_badge_id`(`badge_id` ASC) USING BTREE,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_user_badge_badge` FOREIGN KEY (`badge_id`) REFERENCES `sys_badge_definition` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci AUTO_INCREMENT = 1 ROW_FORMAT = Dynamic COMMENT = "用户勋章表";

SET FOREIGN_KEY_CHECKS = 1;
