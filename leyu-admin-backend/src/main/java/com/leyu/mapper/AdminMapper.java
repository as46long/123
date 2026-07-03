package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员数据访问层
 * 提供管理员表的CRUD操作
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
