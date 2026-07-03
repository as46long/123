package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

/**
 * 用户数据访问层
 * 提供用户表的CRUD操作及自定义查询
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 获取最近7天的用户注册趋势
     * @return 日期和数量的映射列表
     */
    @Select("SELECT DATE(create_time) as date, COUNT(*) as value FROM t_user WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> getUserTrendLast7Days();
}
