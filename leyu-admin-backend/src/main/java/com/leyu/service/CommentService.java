package com.leyu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.CommentDTO;
import com.leyu.vo.CommentVO;

import java.util.List;

/**
 * 留言服务接口
 * 提供留言的发布、审核、点赞、查询等功能
 */
public interface CommentService {
    /**
     * 分页查询留言列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 留言状态
     * @return 留言分页数据
     */
    Page<CommentVO> getPage(int pageNum, int pageSize, Integer status);

    /**
     * 获取推荐留言
     * @param userId 用户ID
     * @param num 推荐数量
     * @return 推荐留言列表
     */
    List<CommentVO> getRecommend(Long userId, int num);

    /**
     * 发布留言
     * @param userId 用户ID
     * @param dto 留言内容DTO
     */
    void post(Long userId, CommentDTO dto);

    /**
     * 审核留言
     * @param id 留言ID
     * @param status 目标状态
     */
    void audit(Long id, Integer status);

    /**
     * 删除留言
     * @param id 留言ID
     */
    void delete(Long id);

    /**
     * 点赞留言
     * @param id 留言ID
     * @param userId 用户ID
     */
    void like(Long id, Long userId);

    /**
     * 统计待审核留言数量
     * @return 待审核数量
     */
    Long countPending();

    /**
     * 获取用户的留言列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户留言分页数据
     */
    Page<CommentVO> getMyComments(Long userId, int pageNum, int pageSize);
}
