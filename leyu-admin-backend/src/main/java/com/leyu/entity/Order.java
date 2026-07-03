package com.leyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 对应数据库表: t_order
 */
@Data
@TableName("t_order")
public class Order {
    /** 订单ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单号，唯一标识 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 套餐类型: WEEK-周卡 MONTH-月卡 QUARTER-季卡 YEAR-年卡 */
    private String packageType;

    /** 订单金额(元) */
    private BigDecimal amount;

    /** 支付状态: 0-未支付 1-已支付 */
    private Integer payStatus;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 会员到期时间 */
    private LocalDateTime expireTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}
