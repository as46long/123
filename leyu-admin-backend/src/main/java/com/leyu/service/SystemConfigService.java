package com.leyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyu.entity.SystemConfig;

/**
 * 系统配置服务接口
 * 提供系统配置参数的读取和设置功能
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 获取配置值
     * @param key 配置键
     * @return 配置值
     */
    String getConfigValue(String key);

    /**
     * 设置配置值
     * @param key 配置键
     * @param value 配置值
     */
    void setConfigValue(String key, String value);
}
