package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问层
 * 提供订单表的CRUD操作及自定义查询
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    /**
     * 计算已支付订单的累计收入
     * @return 累计收入金额
     */
    @Select("SELECT IFNULL(SUM(amount), 0) FROM t_order WHERE pay_status = 1")
    java.math.BigDecimal getTotalRevenue();

    /**
     * 获取最近7天的订单金额趋势(已支付)
     * @return 日期和金额的映射列表
     */
    @Select("SELECT DATE(pay_time) as date, SUM(amount) as value FROM t_order WHERE pay_status = 1 AND pay_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) GROUP BY DATE(pay_time) ORDER BY date")
    List<Map<String, Object>> getOrderTrendLast7Days();

    /**
     * 获取最近7天的播放量趋势
     * @return 日期和播放次数的映射列表
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as value FROM t_user_behavior WHERE behavior_type = 'PLAY' AND create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> getPlayTrendLast7Days();
}
