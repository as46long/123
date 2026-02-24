package com.leyu.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class CommentDTO {
    @NotBlank(message = "留言内容不能为空")
    private String content;
}
