package com.leyu.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardVO {
    private Long totalUsers;
    private Long todayNewUsers;
    private Long totalSongs;
    private Long todayPlayCount;
    private BigDecimal todayAmount;
    private BigDecimal monthAmount;
    private Long pendingComments;
    private List<TrendData> userTrend;
    private List<TrendData> orderTrend;
    private List<SongVO> hotSongs;

    @Data
    public static class TrendData {
        private String date;
        private Long value;
    }
}
