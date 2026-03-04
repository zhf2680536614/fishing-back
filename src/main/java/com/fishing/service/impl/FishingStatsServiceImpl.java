package com.fishing.service.impl;

import com.fishing.mapper.PostMapper;
import com.fishing.pojo.vo.FishingStatsVO;
import com.fishing.service.FishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 钓鱼统计服务实现
 */
@Slf4j
@Service
public class FishingStatsServiceImpl implements FishingStatsService {

    @Resource
    private PostMapper postMapper;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter MONTH_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");
    private static final DateTimeFormatter FULL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public FishingStatsVO getFishingStats(Long userId) {
        log.info("开始获取用户钓鱼统计数据：userId={}", userId);

        FishingStatsVO statsVO = new FishingStatsVO();

        // 获取每月出钓率（最近12个月）
        statsVO.setMonthlyFishingRates(getMonthlyFishingRates(userId));

        // 获取每日鱼获重量趋势（最近15天）
        statsVO.setDailyFishWeights(getDailyFishWeights(userId));

        // 获取每月空军率（最近12个月）
        statsVO.setMonthlyAirForceRates(getMonthlyAirForceRates(userId));

        log.info("获取用户钓鱼统计数据完成：userId={}", userId);
        return statsVO;
    }

    /**
     * 获取每月出钓率（最近12个月）
     */
    private List<FishingStatsVO.MonthlyFishingRateVO> getMonthlyFishingRates(Long userId) {
        List<FishingStatsVO.MonthlyFishingRateVO> result = new ArrayList<>();

        // 获取最近12个月的出钓天数
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(11).withDayOfMonth(1);

        List<Map<String, Object>> fishingDaysList = postMapper.selectMonthlyFishingDays(
                userId, startDate.toString(), endDate.toString());

        // 转换为Map方便查询
        Map<String, Integer> fishingDaysMap = fishingDaysList.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("month"),
                        m -> ((Number) m.get("days")).intValue(),
                        (a, b) -> a
                ));

        // 生成最近12个月的数据
        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            String monthKey = yearMonth.format(MONTH_FORMATTER);
            String monthName = yearMonth.format(MONTH_NAME_FORMATTER);

            FishingStatsVO.MonthlyFishingRateVO vo = new FishingStatsVO.MonthlyFishingRateVO();
            vo.setMonth(monthKey);
            vo.setMonthName(monthName);

            int fishingDays = fishingDaysMap.getOrDefault(monthKey, 0);
            int totalDays = yearMonth.lengthOfMonth();

            vo.setFishingDays(fishingDays);
            vo.setTotalDays(totalDays);
            vo.setRate(Math.round((double) fishingDays / totalDays * 10000) / 100.0);

            result.add(vo);
        }

        return result;
    }

    /**
     * 获取每日鱼获重量趋势（最近15天）
     */
    private List<FishingStatsVO.DailyFishWeightVO> getDailyFishWeights(Long userId) {
        List<FishingStatsVO.DailyFishWeightVO> result = new ArrayList<>();

        // 获取最近15天的鱼获重量
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(14);

        List<Map<String, Object>> weightList = postMapper.selectDailyFishWeight(
                userId, startDate.toString(), endDate.toString());

        // 转换为Map方便查询
        Map<String, Double> weightMap = weightList.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("date"),
                        m -> ((Number) m.get("weight")).doubleValue(),
                        (a, b) -> a
                ));

        // 生成最近15天的数据
        for (int i = 14; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateKey = date.format(FULL_DATE_FORMATTER);
            String dateLabel = date.format(DATE_FORMATTER);

            FishingStatsVO.DailyFishWeightVO vo = new FishingStatsVO.DailyFishWeightVO();
            vo.setDate(dateLabel);
            vo.setFullDate(dateKey);

            Double weight = weightMap.getOrDefault(dateKey, 0.0);
            vo.setTotalWeight(Math.round(weight * 100) / 100.0);
            vo.setHasRecord(weight > 0);

            result.add(vo);
        }

        return result;
    }

    /**
     * 获取每月空军率（最近12个月）
     */
    private List<FishingStatsVO.MonthlyAirForceRateVO> getMonthlyAirForceRates(Long userId) {
        List<FishingStatsVO.MonthlyAirForceRateVO> result = new ArrayList<>();

        // 获取最近12个月的出钓和空军数据
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(11).withDayOfMonth(1);

        List<Map<String, Object>> statsList = postMapper.selectMonthlyAirForceStats(
                userId, startDate.toString(), endDate.toString());

        // 转换为Map方便查询
        Map<String, MonthlyStats> statsMap = statsList.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("month"),
                        m -> {
                            MonthlyStats stats = new MonthlyStats();
                            stats.totalTrips = ((Number) m.get("total_trips")).intValue();
                            stats.airForceCount = ((Number) m.get("air_force_count")).intValue();
                            return stats;
                        },
                        (a, b) -> a
                ));

        // 生成最近12个月的数据
        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.now().minusMonths(i);
            String monthKey = yearMonth.format(MONTH_FORMATTER);
            String monthName = yearMonth.format(MONTH_NAME_FORMATTER);

            FishingStatsVO.MonthlyAirForceRateVO vo = new FishingStatsVO.MonthlyAirForceRateVO();
            vo.setMonth(monthKey);
            vo.setMonthName(monthName);

            MonthlyStats stats = statsMap.getOrDefault(monthKey, new MonthlyStats());
            vo.setTotalTrips(stats.totalTrips);
            vo.setAirForceCount(stats.airForceCount);

            if (stats.totalTrips > 0) {
                vo.setRate(Math.round((double) stats.airForceCount / stats.totalTrips * 10000) / 100.0);
            } else {
                vo.setRate(0.0);
            }

            result.add(vo);
        }

        return result;
    }

    /**
     * 月度统计数据内部类
     */
    private static class MonthlyStats {
        int totalTrips = 0;
        int airForceCount = 0;
    }
}
