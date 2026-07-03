package com.leyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leyu.entity.Song;
import com.leyu.entity.User;
import com.leyu.entity.UserBehavior;
import com.leyu.mapper.SongMapper;
import com.leyu.mapper.UserBehaviorMapper;
import com.leyu.mapper.UserMapper;
import com.leyu.mapper.OrderMapper;
import com.leyu.service.CommentService;
import com.leyu.service.SongService;
import com.leyu.vo.DashboardVO;
import com.leyu.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘控制器
 * 提供后台管理系统的统计数据和图表数据
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/dashboard")
@Tag(name = "仪表盘")
public class DashboardController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SongService songService;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取仪表盘统计数据
     * 包括: 总用户数、今日新增用户、总歌曲数、播放量、累计收入、热门歌曲TOP10
     * @return 统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取统计数据")
    public Result<DashboardVO> getStats() {
        log.info("========== 开始获取仪表盘统计数据 ==========");
        DashboardVO stats = new DashboardVO();
        
        try {
            // 总用户数
            Long totalUsers = userMapper.selectCount(new QueryWrapper<>());
            log.info("总用户数: {}", totalUsers);
            stats.setTotalUsers(totalUsers);
            
            // 今日新增用户
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            Long todayNewUsers = userMapper.selectCount(new QueryWrapper<User>()
                    .ge("create_time", todayStart));
            log.info("今日新增用户: {}", todayNewUsers);
            stats.setTodayNewUsers(todayNewUsers);
            
            // 总歌曲数
            Long totalSongs = songMapper.selectCount(new QueryWrapper<>());
            log.info("总歌曲数: {}", totalSongs);
            stats.setTotalSongs(totalSongs);
            
            // 歌曲播放量（所有歌曲播放量总和）
            Long totalPlayCount = songMapper.getTotalPlayCount();
            log.info("歌曲播放量: {}", totalPlayCount);
            stats.setTodayPlayCount(totalPlayCount);
            
            // 累计收入（已支付订单金额总和）
            java.math.BigDecimal totalRevenue = orderMapper.getTotalRevenue();
            log.info("累计收入: {}", totalRevenue);
            stats.setTotalRevenue(totalRevenue);
            
            // 热门歌曲TOP10
            stats.setHotSongs(songService.getHot(10));
            log.info("热门歌曲数量: {}", stats.getHotSongs() != null ? stats.getHotSongs().size() : 0);
            
            log.info("========== 仪表盘统计数据获取完成 ==========");
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取仪表盘统计数据失败", e);
            // 返回默认值
            stats.setTotalUsers(0L);
            stats.setTotalSongs(0L);
            stats.setTodayPlayCount(0L);
            stats.setTotalRevenue(java.math.BigDecimal.ZERO);
            return Result.success(stats);
        }
    }

    /**
     * 获取图表数据
     * 包括最近7天的用户增长、订单金额、播放量趋势
     * @return 图表数据(dates、userTrend、orderTrend、playTrend)
     */
    @GetMapping("/charts")
    @Operation(summary = "获取图表数据")
    public Result<Map<String, Object>> getChartData() {
        log.info("========== 开始获取图表数据 ==========");
        
        try {
            // 生成最近7天的日期列表
            List<String> dates = new ArrayList<>();
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
            for (int i = 6; i >= 0; i--) {
                dates.add(today.minusDays(i).format(formatter));
            }

            // 获取用户增长趋势
            List<Map<String, Object>> userTrendData = userMapper.getUserTrendLast7Days();
            List<Long> userTrend = buildTrendList(userTrendData, dates);

            // 获取订单金额趋势
            List<Map<String, Object>> orderTrendData = orderMapper.getOrderTrendLast7Days();
            List<Long> orderTrend = buildTrendList(orderTrendData, dates);

            // 获取播放量趋势
            List<Map<String, Object>> playTrendData = orderMapper.getPlayTrendLast7Days();
            List<Long> playTrend = buildTrendList(playTrendData, dates);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("dates", dates);
            result.put("userTrend", userTrend);
            result.put("orderTrend", orderTrend);
            result.put("playTrend", playTrend);
            
            log.info("========== 图表数据获取完成 ==========");
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取图表数据失败", e);
            return Result.success(new java.util.HashMap<>());
        }
    }

    /**
     * 将数据库查询结果转换为趋势列表
     */
    private List<Long> buildTrendList(List<Map<String, Object>> trendData, List<String> dates) {
        List<Long> result = new ArrayList<>();
        java.util.Map<String, Long> dataMap = new java.util.HashMap<>();
        
        for (Map<String, Object> item : trendData) {
            Object dateObj = item.get("date");
            Object valueObj = item.get("value");
            String dateStr = dateObj != null ? dateObj.toString().substring(5) : "";
            Long value = 0L;
            if (valueObj instanceof Number) {
                value = ((Number) valueObj).longValue();
            }
            dataMap.put(dateStr, value);
        }
        
        for (String date : dates) {
            result.add(dataMap.getOrDefault(date, 0L));
        }
        
        return result;
    }
}
