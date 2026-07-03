package com.leyu.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单视图对象
 * 用于返回订单信息给前端
 */
@Data
public class OrderVO {
    /** 订单ID */
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 用户名(关联查询) */
    private String username;

    /** 套餐类型: WEEK/MONTH/QUARTER/YEAR */
    private String packageType;

    /** 订单金额 */
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
