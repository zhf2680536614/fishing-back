package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.mapper.PostLikeMapper;
import com.fishing.mapper.PostMapper;
import com.fishing.mapper.UserMapper;
import com.fishing.pojo.dto.AirForceCheckinDTO;
import com.fishing.pojo.entity.PostEntity;
import com.fishing.pojo.entity.PostLikeEntity;
import com.fishing.pojo.entity.UserEntity;
import com.fishing.pojo.vo.AirForcePostVO;
import com.fishing.pojo.vo.AirForceStatsVO;
import com.fishing.service.AirForceService;
import com.fishing.service.PostService;
import com.fishing.utils.MinioUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AirForceServiceImpl extends ServiceImpl<PostMapper, PostEntity> implements AirForceService {

    private final PostService postService;
    private final MinioUtils minioUtils;
    private final UserMapper userMapper;
    private final PostLikeMapper postLikeMapper;

    // 预设的AI安慰语
    private static final String[] AI_COMFORTS = {
        "水太深，鱼太滑，不是技术差，纯属路不平。下次必爆护！",
        "天气不好不是你的错，空军是钓鱼的一部分，调整心态，下次再战！",
        "饵料不对口，鱼当然不开口，下次换个饵料试试，相信你一定能爆护！",
        "空军只是暂时的，爆护才是永恒的！坚持就是胜利！",
        "钓鱼钓的是心情，空军也是一种收获。享受过程，下次一定行！",
        "鱼不咬钩是它们没眼光，你的技术绝对一流！",
        "空军是为了让下次的爆护更甜蜜，加油！",
        "钓鱼佬从不空军，只是鱼还没准备好迎接你！",
        "今天的空军是为了明天的爆护做铺垫，相信自己！",
        "鱼都在水下开会讨论怎么避开你，说明你的技术让它们害怕了！"
    };

    @Override
    public AirForcePostVO checkin(AirForceCheckinDTO dto, Long userId) {
        log.info("用户 {} 进行空军打卡", userId);

        // 生成AI安慰语
        String aiComment = generateAIComment(dto.getContent());

        // 创建帖子
        PostEntity post = new PostEntity();
        post.setUserId(userId);
        post.setTypeDictTypeCode(dto.getTypeDictTypeCode());
        post.setTypeDictItemCode(dto.getTypeDictItemCode());
        post.setTitle("空军打卡");
        post.setContent(dto.getContent());
        post.setImageList(dto.getImages());
        post.setAiComment(aiComment);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setViewCount(0);
        post.setAiAuditStatusDictTypeCode(dto.getAiAuditStatusDictTypeCode());
        post.setAiAuditStatusDictItemCode(dto.getAiAuditStatusDictItemCode());
        post.setStatusDictTypeCode(dto.getStatusDictTypeCode());
        post.setStatusDictItemCode(dto.getStatusDictItemCode());
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        post.setIsDeleted(0);

        this.save(post);

        // 转换为VO
        return convertToVO(post, userId);
    }

    @Override
    public List<AirForcePostVO> getPostList(Integer pageNum, Integer pageSize, String sortType, String typeDictItemCode, Long userId) {
        LambdaQueryWrapper<PostEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostEntity::getTypeDictItemCode, typeDictItemCode) // 帖子类型
                .eq(PostEntity::getIsDeleted, 0);

        // 排序
        if ("hottest".equals(sortType)) {
            wrapper.orderByDesc(PostEntity::getLikeCount)
                    .orderByDesc(PostEntity::getCreateTime);
        } else {
            wrapper.orderByDesc(PostEntity::getCreateTime);
        }

        // 分页
        int offset = (pageNum - 1) * pageSize;
        wrapper.last("LIMIT " + offset + ", " + pageSize);

        List<PostEntity> posts = this.list(wrapper);

        return posts.stream()
                .map(post -> convertToVO(post, userId))
                .collect(Collectors.toList());
    }

    @Override
    public AirForceStatsVO getStats(Long userId, String airForceTypeCode, String catchTypeCode) {
        AirForceStatsVO stats = new AirForceStatsVO();

        // 今日空军人数
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LambdaQueryWrapper<PostEntity> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(PostEntity::getTypeDictItemCode, airForceTypeCode)
                .ge(PostEntity::getCreateTime, todayStart)
                .eq(PostEntity::getIsDeleted, 0);
        long todayCount = this.count(todayWrapper);
        stats.setTodayAirForce((int) todayCount);

        // 本周空军人数
        LocalDate weekStart = today.minusDays(7);
        LocalDateTime weekStartTime = weekStart.atStartOfDay();
        LambdaQueryWrapper<PostEntity> weekWrapper = new LambdaQueryWrapper<>();
        weekWrapper.eq(PostEntity::getTypeDictItemCode, airForceTypeCode)
                .ge(PostEntity::getCreateTime, weekStartTime)
                .eq(PostEntity::getIsDeleted, 0);
        long weekCount = this.count(weekWrapper);
        stats.setWeekAirForce((int) weekCount);

        // 空军率 = 空军帖子数 / (空军帖子数 + 鱼获战报帖子数) * 100
        LambdaQueryWrapper<PostEntity> airForceWrapper = new LambdaQueryWrapper<>();
        airForceWrapper.eq(PostEntity::getTypeDictItemCode, airForceTypeCode)
                .eq(PostEntity::getIsDeleted, 0);
        long airForceTotal = this.count(airForceWrapper);

        LambdaQueryWrapper<PostEntity> catchWrapper = new LambdaQueryWrapper<>();
        catchWrapper.eq(PostEntity::getTypeDictItemCode, catchTypeCode)
                .eq(PostEntity::getIsDeleted, 0);
        long catchTotal = this.count(catchWrapper);

        long totalPosts = airForceTotal + catchTotal;
        int airForceRate = totalPosts > 0 ? (int) ((airForceTotal * 100) / totalPosts) : 0;
        stats.setAirForceRate(airForceRate);

        // 最长连续空军天数（模拟数据）
        stats.setMaxStreak(7);

        // 我的空军次数
        LambdaQueryWrapper<PostEntity> myWrapper = new LambdaQueryWrapper<>();
        myWrapper.eq(PostEntity::getTypeDictItemCode, airForceTypeCode)
                .eq(PostEntity::getUserId, userId)
                .eq(PostEntity::getIsDeleted, 0);
        long myCount = this.count(myWrapper);
        stats.setMyAirForceCount((int) myCount);

        // 我的连续空军天数（模拟数据）
        stats.setMyStreak(calculateStreak(userId, airForceTypeCode));

        return stats;
    }

    @Override
    @Transactional
    public Boolean toggleLike(Long postId, Long userId) {
        PostEntity post = this.getById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            return false;
        }

        PostLikeEntity existingLike = postLikeMapper.selectByPostAndUser(postId, userId);
        if (existingLike == null) {
            PostLikeEntity newLike = new PostLikeEntity();
            newLike.setPostId(postId);
            newLike.setUserId(userId);
            postLikeMapper.insert(newLike);
        }
        post.setLikeCount(post.getLikeCount() + 1);
        this.updateById(post);
        return true;
    }

    @Override
    public String generateAIComment(String content) {
        // 随机选择一条安慰语
        Random random = new Random();
        int index = random.nextInt(AI_COMFORTS.length);
        return AI_COMFORTS[index];

        // TODO: 接入AI服务生成个性化安慰语
        // return aiUtils.generateComfort(content);
    }

    /**
     * 计算连续空军天数
     */
    private Integer calculateStreak(Long userId, String airForceTypeCode) {
        LambdaQueryWrapper<PostEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostEntity::getTypeDictItemCode, airForceTypeCode)
                .eq(PostEntity::getUserId, userId)
                .eq(PostEntity::getIsDeleted, 0)
                .orderByDesc(PostEntity::getCreateTime);

        List<PostEntity> posts = this.list(wrapper);
        if (posts.isEmpty()) {
            return 0;
        }

        int streak = 0;
        LocalDate currentDate = LocalDate.now();

        for (PostEntity post : posts) {
            LocalDate postDate = post.getCreateTime().toLocalDate();
            long daysDiff = ChronoUnit.DAYS.between(postDate, currentDate);

            if (daysDiff == streak) {
                streak++;
            } else if (daysDiff > streak) {
                break;
            }
        }

        return streak;
    }

    private AirForcePostVO convertToVO(PostEntity post, Long currentUserId) {
        AirForcePostVO vo = new AirForcePostVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setContent(post.getContent());
        vo.setImages(post.getImageList());
        vo.setAiComment(post.getAiComment());
        vo.setLikeCount(post.getLikeCount());
        vo.setCommentCount(post.getCommentCount());
        vo.setViewCount(post.getViewCount());
        vo.setCreateTime(post.getCreateTime());

        UserEntity user = userMapper.selectById(post.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        } else {
            vo.setUserNickname("匿名用户");
            vo.setUserAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
        }

        vo.setIsLiked(false);

        return vo;
    }
}
