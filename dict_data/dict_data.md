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
('common_status', '通用状态', 0, '通用启用禁用状态', 12),
('fish_species', '鱼种分类', 0, '常见鱼种分类', 13),
('ai_generated', 'AI生成', 0, '是否AI生成内容', 14);

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

-- 插入鱼种分类字典项（淡水鱼）
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`, `description`) VALUES
(13, 'crucian_carp', '鲫鱼', '1', 1, '淡水鱼，常见小型鱼类'),
(13, 'common_carp', '鲤鱼', '2', 2, '淡水鱼，常见中型鱼类'),
(13, 'grass_carp', '草鱼', '3', 3, '淡水鱼，草食性大型鱼类'),
(13, 'silver_carp', '鲢鱼', '4', 4, '淡水鱼，滤食性鱼类'),
(13, 'bighead_carp', '鳙鱼', '5', 5, '淡水鱼，又称胖头鱼'),
(13, 'catfish', '鲶鱼', '6', 6, '淡水鱼，底层肉食性鱼类'),
(13, 'yellow_catfish', '黄颡鱼', '7', 7, '淡水鱼，又称黄辣丁'),
(13, 'mandarin_fish', '鳜鱼', '8', 8, '淡水鱼，名贵肉食性鱼类'),
(13, 'largemouth_bass', '鲈鱼', '9', 9, '淡水鱼，路亚常见目标鱼'),
(13, 'snakehead', '黑鱼', '10', 10, '淡水鱼，又称乌鱼'),
(13, 'tilapia', '罗非鱼', '11', 11, '淡水鱼，外来引进品种'),
(13, 'crucian_carp_gold', '黄金鲫', '12', 12, '淡水鱼，鲫鱼变种'),
(13, 'wuchang_fish', '武昌鱼', '13', 13, '淡水鱼，又称团头鲂'),
(13, 'bream', '鳊鱼', '14', 14, '淡水鱼，常见中型鱼类'),
(13, 'topmouth_gudgeon', '白条', '15', 15, '淡水鱼，小型上层鱼类'),
(13, 'chinese_perch', '翘嘴', '16', 16, '淡水鱼，路亚目标鱼'),
(13, 'bluegill', '蓝鳃太阳鱼', '17', 17, '淡水鱼，外来引进品种'),
(13, 'red_drum', '美国红鱼', '18', 18, '淡水鱼，外来引进品种'),
(13, 'grass_carp_black', '青鱼', '19', 19, '淡水鱼，大型底层鱼类'),
(13, 'loach', '泥鳅', '20', 20, '淡水鱼，小型底层鱼类'),
(13, 'eel', '黄鳝', '21', 21, '淡水鱼，穴居鱼类'),
(13, 'crucian_carp_crucian', '土鲮', '22', 22, '淡水鱼，南方常见鱼类'),
(13, 'barbel', '鲮鱼', '23', 23, '淡水鱼，南方常见鱼类'),
(13, 'minnow', '马口', '24', 24, '淡水鱼，小型溪流鱼类'),
(13, 'crucian_carp_white', '白鲫', '25', 25, '淡水鱼，鲫鱼变种'),
(13, 'crucian_carp_engineering', '工程鲫', '26', 26, '淡水鱼，人工培育品种'),
(13, 'catfish_southern', '南方大口鲶', '27', 27, '淡水鱼，大型鲶鱼'),
(13, 'catfish_channel', '斑点叉尾鮰', '28', 28, '淡水鱼，外来引进品种'),
(13, 'sturgeon', '鲟鱼', '29', 29, '淡水鱼，珍稀大型鱼类'),
(13, 'trout', '虹鳟', '30', 30, '淡水鱼，冷水性鱼类'),
(13, 'crucian_carp_crucian_2', '彭泽鲫', '31', 31, '淡水鱼，鲫鱼优良品种'),
(13, 'crucian_carp_crucian_3', '湘云鲫', '32', 32, '淡水鱼，人工培育品种'),
(13, 'crucian_carp_crucian_4', '异育银鲫', '33', 33, '淡水鱼，人工培育品种'),
(13, 'carp_mirror', '镜鲤', '34', 34, '淡水鱼，鲤鱼变种'),
(13, 'carp_koi', '锦鲤', '35', 35, '淡水鱼，观赏鲤鱼'),
(13, 'carp_german', '德国镜鲤', '36', 36, '淡水鱼，外来引进品种'),
(13, 'bream_white', '白鲂', '37', 37, '淡水鱼，鳊鱼近亲'),
(13, 'bream_triangle', '三角鲂', '38', 38, '淡水鱼，鳊鱼近亲'),
(13, 'dace', '鲴鱼', '39', 39, '淡水鱼，中小型鱼类'),
(13, 'dace_yellow', '黄尾鲴', '40', 40, '淡水鱼，鲴鱼一种'),
(13, 'dace_silver', '银鲴', '41', 41, '淡水鱼，鲴鱼一种'),
(13, 'dace_round', '圆吻鲴', '42', 42, '淡水鱼，鲴鱼一种');

-- 插入鱼种分类字典项（海水鱼）
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`, `description`) VALUES
(13, 'seabass', '海鲈鱼', '43', 43, '海水鱼，近海常见鱼类'),
(13, 'grouper', '石斑鱼', '44', 44, '海水鱼，名贵海鱼'),
(13, 'sea_bream', '鲷鱼', '45', 45, '海水鱼，常见海鱼'),
(13, 'yellow_croaker', '黄花鱼', '46', 46, '海水鱼，经济鱼类'),
(13, 'hairtail', '带鱼', '47', 47, '海水鱼，常见经济鱼类'),
(13, 'mackerel', '鲅鱼', '48', 48, '海水鱼，常见经济鱼类'),
(13, 'flatfish', '比目鱼', '49', 49, '海水鱼，底层鱼类'),
(13, 'pufferfish', '河豚', '50', 50, '海水/淡水，需专业处理'),
(13, 'mullet', '鲻鱼', '51', 51, '海水鱼，近岸常见'),
(13, 'pomfret', '鲳鱼', '52', 52, '海水鱼，经济鱼类'),
(13, 'conger_eel', '海鳗', '53', 53, '海水鱼，底层鱼类'),
(13, 'snapper', '红鲷', '54', 54, '海水鱼，名贵鱼种'),
(13, 'tuna', '金枪鱼', '55', 55, '海水鱼，深海鱼类'),
(13, 'sardine', '沙丁鱼', '56', 56, '海水鱼，小型群游鱼类'),
(13, 'flounder', '牙鲆', '57', 57, '海水鱼，比目鱼科'),
(13, 'sole', '舌鳎', '58', 58, '海水鱼，比目鱼科'),
(13, 'croaker', '白姑鱼', '59', 59, '海水鱼，石首鱼科'),
(13, 'drum', '美国红鼓鱼', '60', 60, '海水鱼，外来引进品种');

-- 插入AI生成字典项
INSERT INTO `fish_club_db`.`sys_dict_item` (`dict_type_id`, `item_code`, `item_name`, `value`, `sort_order`) VALUES
(14, 'yes', '是', '1', 1),
(14, 'no', '否', '2', 2);