package com.leyu.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 仪表盘视图对象
 * 用于返回后台管理系统的统计数据
 */
@Data
public class DashboardVO {
    /** 总用户数 */
    private Long totalUsers;

    /** 今日新增用户数 */
    private Long todayNewUsers;

    /** 总歌曲数 */
    private Long totalSongs;

    /** 今日播放量 */
    private Long todayPlayCount;

    /** 今日收入 */
    private BigDecimal todayAmount;

    /** 本月收入 */
    private BigDecimal monthAmount;

    /** 累计收入 */
    private BigDecimal totalRevenue;

    /** 用户增长趋势数据 */
    private List<TrendData> userTrend;

    /** 订单趋势数据 */
    private List<TrendData> orderTrend;

    /** 热门歌曲TOP10 */
    private List<SongVO> hotSongs;

    /**
     * 趋势数据内部类
     */
    @Data
    public static class TrendData {
        /** 日期(MM-dd格式) */
        private String date;

        /** 数值 */
        private Long value;
    }
}
