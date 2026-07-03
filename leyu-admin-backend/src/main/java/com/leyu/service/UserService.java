package com.leyu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.LoginDTO;
import com.leyu.dto.RegisterDTO;
import com.leyu.entity.User;
import com.leyu.vo.UserVO;

/**
 * 用户服务接口
 * 提供用户登录、注册、信息管理等功能
 */
public interface UserService {
    /**
     * 用户名密码登录
     * @param dto 登录参数
     * @return 用户视图对象
     */
    UserVO login(LoginDTO dto);

    /**
     * 微信登录
     * @param code 微信登录code
     * @return 用户视图对象
     */
    UserVO wxLogin(String code);

    /**
     * 微信登录并更新用户信息
     * @param code 微信登录code
     * @param nickname 昵称
     * @param avatar 头像URL
     * @return 用户视图对象
     */
    UserVO wxLoginWithInfo(String code, String nickname, String avatar);

    /**
     * 用户注册
     * @param dto 注册参数
     * @return 注册成功的用户视图对象
     */
    UserVO register(RegisterDTO dto);

    /**
     * 根据用户名获取用户实体
     * @param username 用户名
     * @return 用户实体
     */
    User getByUsername(String username);

    /**
     * 根据ID获取用户实体
     * @param id 用户ID
     * @return 用户实体
     */
    User getEntityById(Long id);

    /**
     * 根据ID获取用户视图对象
     * @param id 用户ID
     * @return 用户视图对象
     */
    UserVO getById(Long id);

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词
     * @return 用户分页数据
     */
    Page<UserVO> getPage(int pageNum, int pageSize, String keyword);

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param vo 更新的用户信息
     */
    void update(Long id, UserVO vo);

    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 目标状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 删除用户
     * @param id 用户ID
     */
    void delete(Long id);
}
