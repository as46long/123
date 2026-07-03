package com.leyu.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单数据传输对象
 * 用于创建订单时的参数传递
 */
@Data
public class OrderDTO {
    /** 套餐类型: WEEK-周卡 MONTH-月卡 QUARTER-季卡 YEAR-年卡 */
    private String packageType;

    /** 订单金额 */
    private BigDecimal amount;
}
