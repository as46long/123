package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置数据访问层
 * 提供系统配置表的CRUD操作
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {
}
