package com.leyu.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDTO {
    private String packageType;
    private BigDecimal amount;
}
