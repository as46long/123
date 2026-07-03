package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留言数据访问层
 * 提供留言表的CRUD操作
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
