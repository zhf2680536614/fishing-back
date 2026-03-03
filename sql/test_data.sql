-- 测试数据插入脚本

-- 插入测试用户
INSERT INTO sys_user (id, username, password, nickname, avatar, phone, signature, role, is_master, exp_points, status, create_time, update_time, is_deleted)
VALUES 
(1, 'testuser1', 'password123', '钓鱼达人', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', '13800138001', '钓鱼使我快乐', 0, 1, 1000, 1, NOW(), NOW(), 0),
(2, 'testuser2', 'password123', '空军司令', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', '13800138002', '今天又空军了', 0, 0, 500, 1, NOW(), NOW(), 0),
(3, 'testuser3', 'password123', '路亚高手', 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', '13800138003', '路亚才是真爱', 0, 1, 800, 1, NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE username=VALUES(username);

-- 插入测试帖子（鱼获战报）
INSERT INTO biz_post (id, user_id, type, title, content, images, fish_species, fish_weight, spot_id, address_name, view_count, like_count, comment_count, ai_audit_status, ai_audit_reason, ai_comment, create_time, update_time, is_deleted)
VALUES 
(1, 1, 0, '今天爆护了！钓到一条大青鱼', '今天天气不错，早上6点就出发了，没想到运气这么好，钓到了一条12斤的大青鱼！', '["https://images.unsplash.com/photo-1544551763-77ef2d0cfc6c?q=80&w=800&auto=format&fit=crop"]', '青鱼', 12.00, NULL, '西湖边', 156, 23, 5, 1, NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0),
(2, 1, 0, '周末野钓收获满满', '周末和朋友一起去野钓，收获颇丰，一共钓了8条鲫鱼，最大的有2斤多', '["https://images.unsplash.com/photo-1544551763-77ef2d0cfc6c?q=80&w=800&auto=format&fit=crop"]', '鲫鱼', 2.50, NULL, '千岛湖', 89, 15, 3, 1, NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0),
(3, 3, 0, '路亚翘嘴成功！', '第一次尝试路亚翘嘴，没想到就成功了！这条翘嘴有5斤重，手感超棒', '["https://images.unsplash.com/photo-1544551763-77ef2d0cfc6c?q=80&w=800&auto=format&fit=crop"]', '翘嘴', 5.00, NULL, '钱塘江', 67, 8, 2, 1, NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW(), 0),
(4, 2, 0, '草鱼大丰收', '今天运气爆棚，连续钓到3条大草鱼，最大的有8斤！', '["https://images.unsplash.com/photo-1544551763-77ef2d0cfc6c?q=80&w=800&auto=format&fit=crop"]', '草鱼', 8.00, NULL, '湘湖', 45, 6, 1, 1, NULL, NULL, DATE_SUB(NOW(), INTERVAL 5 HOUR), NOW(), 0),
(5, 1, 0, '鲤鱼之王', '钓到一条巨物鲤鱼，足足15斤！这是我钓到的最大的鱼了', '["https://images.unsplash.com/photo-1544551763-77ef2d0cfc6c?q=80&w=800&auto=format&fit=crop"]', '鲤鱼', 15.00, NULL, '太湖', 234, 45, 12, 1, NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 WEEK), NOW(), 0)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 插入测试帖子（空军吐槽）
INSERT INTO biz_post (id, user_id, type, title, content, images, fish_species, fish_weight, spot_id, address_name, view_count, like_count, comment_count, ai_audit_status, ai_audit_reason, ai_comment, create_time, update_time, is_deleted)
VALUES 
(101, 2, 1, '今天又空军了', '早上5点就出门，钓了一整天，一条鱼都没钓到，太难受了', NULL, NULL, 0.00, NULL, '西湖边', 78, 12, 8, 1, NULL, '别灰心，空军是钓鱼人的必经之路，明天继续加油！', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0),
(102, 2, 1, '连续三天空军了', '这是什么运气啊，连续三天空军，我都怀疑人生了', NULL, NULL, 0.00, NULL, '钱塘江', 56, 8, 5, 1, NULL, '空军不可怕，可怕的是失去信心。调整心态，重新出发！', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0),
(103, 1, 1, '空军日记：第100次', '今天是我的第100次空军，已经习以为常了', NULL, NULL, 0.00, NULL, '千岛湖', 123, 25, 15, 1, NULL, '坚持就是胜利！100次空军意味着你离爆护越来越近了！', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), 0)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 插入测试评论
INSERT INTO biz_comment (id, post_id, user_id, parent_id, content, is_ai_generated, create_time, update_time, is_deleted)
VALUES 
(1, 1, 2, 0, '太厉害了！我也想去钓青鱼', 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0),
(2, 1, 3, 0, '青鱼手感确实不错，恭喜恭喜！', 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0),
(3, 1, 1, 2, '谢谢！下次一起去钓鱼', 0, DATE_SUB(NOW(), INTERVAL 20 HOUR), NOW(), 0),
(4, 2, 2, 0, '鲫鱼味道不错，羡慕啊', 0, DATE_SUB(NOW(), INTERVAL 12 HOUR), NOW(), 0),
(5, 3, 1, 0, '路亚翘嘴确实刺激，我也想试试', 0, DATE_SUB(NOW(), INTERVAL 6 HOUR), NOW(), 0),
(6, 5, 2, 0, '15斤的鲤鱼太牛了！', 0, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW(), 0),
(7, 5, 3, 0, '这是巨物啊！', 0, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW(), 0),
(8, 101, 1, 0, '别灰心，明天继续加油！', 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), 0),
(9, 102, 1, 0, '坚持就是胜利！', 0, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), 0),
(10, 103, 3, 0, '100次空军，太不容易了', 0, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), 0)
ON DUPLICATE KEY UPDATE content=VALUES(content);

-- 插入测试点赞记录
INSERT INTO biz_post_like (id, post_id, user_id, create_time)
VALUES 
(1, 1, 2, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 1, 3, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 2, 2, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(4, 3, 1, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(5, 5, 2, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(6, 5, 3, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(7, 101, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(8, 102, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(9, 103, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(10, 103, 3, DATE_SUB(NOW(), INTERVAL 3 DAY))
ON DUPLICATE KEY UPDATE post_id=VALUES(post_id);
