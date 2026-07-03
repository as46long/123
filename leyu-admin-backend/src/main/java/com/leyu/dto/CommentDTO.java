package com.leyu.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 留言数据传输对象
 * 用于发布留言时的参数传递
 */
@Data
public class CommentDTO {
    /** 留言内容，必填 */
    @NotBlank(message = "留言内容不能为空")
    private String content;
}
