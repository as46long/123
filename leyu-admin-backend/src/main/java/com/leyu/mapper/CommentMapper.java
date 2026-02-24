package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
