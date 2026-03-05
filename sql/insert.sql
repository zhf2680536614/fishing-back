SET FOREIGN_KEY_CHECKS = 0;
SET NAMES utf8mb4;
use `fish_club_db`;

-- 插入12个勋章定义
INSERT INTO `fish_club_db`.`sys_badge_definition` (`badge_name`, `badge_icon`, `description`, `requirement_type`, `requirement_value`, `sort_order`) VALUES
('初出茅庐', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20beginner%2C%20bronze%20color%2C%20simple%20icon&image_size=square', '完成第一次出钓', 'fishing_days', 1, 1),
('坚持不懈', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20persistence%2C%20silver%20color%2C%20simple%20icon&image_size=square', '累计出钓10天', 'fishing_days', 10, 2),
('钓鱼达人', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20master%2C%20gold%20color%2C%20simple%20icon&image_size=square', '累计出钓30天', 'fishing_days', 30, 3),
('钓鱼大师', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20grandmaster%2C%20platinum%20color%2C%20simple%20icon&image_size=square', '累计出钓100天', 'fishing_days', 100, 4),
('收获颇丰', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20good%20catch%2C%20green%20color%2C%20simple%20icon&image_size=square', '累计有鱼获5天', 'fish_days', 5, 5),
('渔获高手', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20expert%20catch%2C%20blue%20color%2C%20simple%20icon&image_size=square', '累计有鱼获20天', 'fish_days', 20, 6),
('空军少尉', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20air%20force%20lieutenant%2C%20gray%20color%2C%20simple%20icon&image_size=square', '累计空军5天', 'air_force_days', 5, 7),
('空军上校', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20air%20force%20colonel%2C%20dark%20gray%20color%2C%20simple%20icon&image_size=square', '累计空军15天', 'air_force_days', 15, 8),
('空军司令', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20air%20force%20commander%2C%20black%20color%2C%20simple%20icon&image_size=square', '累计空军30天', 'air_force_days', 30, 9),
('小鱼猎手', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20small%20fish%20hunter%2C%20yellow%20color%2C%20simple%20icon&image_size=square', '累计鱼获重量达到10斤', 'total_weight', 10, 10),
('大鱼克星', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20big%20fish%20hunter%2C%20orange%20color%2C%20simple%20icon&image_size=square', '累计鱼获重量达到50斤', 'total_weight', 50, 11),
('巨物终结者', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=simple%20fishing%20badge%20for%20giant%20fish%20hunter%2C%20red%20color%2C%20simple%20icon&image_size=square', '累计鱼获重量达到100斤', 'total_weight', 100, 12);

-- 插入基础数据字典类型
INSERT INTO `fish_club_db`.`sys_dict_type` (`dict_code`, `dict_name`, `parent_id`, `description`, `sort_order`) VALUES
('user_role', '用户角色', 0, '用户角色类型', 1),
('user_status', '用户状态', 0, '用户账号状态', 2),
('fishing_spot_type', '钓点类型', 0, '钓点类型', 3),
('fishing_spot_status', '钓点状态', 0, '钓点审核发布状态', 4),
('gear_category', '装备分类', 0, '钓鱼装备分类', 5),
('gear_market_status', '装备市场状态', 0, '二手装备交易状态', 6),
('gear_review_status', '装备测评状态', 0, '装备测评审核状态', 7),
('order_status', '订单状态', 0, '订单处理状态', 8),
('post_type', '帖子类型', 0, '社区帖子类型', 9),
('ai_audit_status', 'AI审核状态', 0, 'AI内容审核状态', 10),
('ai_function_type', 'AI功能类型', 0, 'AI调用功能类型', 11),
('common_status', '通用状态', 0, '通用启用禁用状态', 12);

-- 插入用户角色字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(1, 'user', '普通用户', '1', 1),
(1, 'admin', '管理员', '2', 2);

-- 插入用户状态字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(2, 'normal', '正常', '1', 1),
(2, 'banned', '封禁', '2', 2);

-- 插入钓点类型字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(3, 'wild', '野钓', '1', 1),
(3, 'pay', '黑坑/收费', '2', 2),
(3, 'lure_base', '路亚基地', '3', 3);

-- 插入钓点状态字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(4, 'pending', '审核中', '1', 1),
(4, 'published', '已发布', '2', 2),
(4, 'offline', '已下架', '3', 3);

-- 插入装备分类字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(5, 'rod', '鱼竿', '1', 1),
(5, 'reel', '渔轮', '2', 2),
(5, 'line', '鱼线', '3', 3),
(5, 'hook', '鱼钩', '4', 4),
(5, 'bait', '饵料', '5', 5),
(5, 'box', '钓箱', '6', 6),
(5, 'chair', '钓椅', '7', 7),
(5, 'other', '其他', '8', 8);

-- 插入装备市场状态字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(6, 'on_sale', '在售', '1', 1),
(6, 'sold', '已售出', '2', 2),
(6, 'off_shelf', '已下架', '3', 3);

-- 插入装备测评状态字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(7, 'pending', '审核中', '1', 1),
(7, 'published', '已发布', '2', 2),
(7, 'offline', '已下架', '3', 3);

-- 插入订单状态字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(8, 'unpaid', '待付款', '1', 1),
(8, 'paid', '已付款', '2', 2),
(8, 'shipped', '已发货', '3', 3),
(8, 'completed', '已完成', '4', 4),
(8, 'cancelled', '已取消', '5', 5);

-- 插入帖子类型字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(9, 'catch_report', '鱼获战报', '1', 1),
(9, 'air_force', '空军吐槽', '2', 2),
(9, 'gear_review', '装备测评', '3', 3);

-- 插入AI审核状态字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(10, 'violation', '违规', '1', 1),
(10, 'normal', '正常', '2', 2),
(10, 'suspected', '疑似违规', '3', 3);

-- 插入AI功能类型字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(11, 'fish_id', '识鱼', '1', 1),
(11, 'chat', '聊天', '2', 2),
(11, 'audit', '审核', '3', 3);

-- 插入通用状态字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(12, 'enabled', '启用', '1', 1),
(12, 'disabled', '禁用', '2', 2);

SET FOREIGN_KEY_CHECKS = 1;
