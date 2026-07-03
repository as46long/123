package com.leyu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.SongCommentDTO;
import com.leyu.vo.SongCommentVO;

import java.util.List;

/**
 * 歌曲评论服务接口
 * 提供歌曲评论的发布、点赞、审核、查询等功能
 */
public interface SongCommentService {
    /**
     * 获取歌曲评论列表
     * @param songId 歌曲ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param userId 用户ID(可选)
     * @return 评论分页数据
     */
    Page<SongCommentVO> getSongComments(Long songId, int pageNum, int pageSize, Long userId);

    /**
     * 发布歌曲评论
     * @param userId 用户ID
     * @param dto 评论内容DTO
     */
    void postSongComment(Long userId, SongCommentDTO dto);

    /**
     * 点赞歌曲评论
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    void likeSongComment(Long commentId, Long userId);

    /**
     * 封禁歌曲评论
     * @param commentId 评论ID
     */
    void banSongComment(Long commentId);

    /**
     * 解封歌曲评论
     * @param commentId 评论ID
     */
    void unbanSongComment(Long commentId);

    /**
     * 管理员查询歌曲评论
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param category 歌曲分类
     * @param status 评论状态
     * @return 评论分页数据
     */
    Page<SongCommentVO> getAdminSongComments(int pageNum, int pageSize, String category, Integer status);

    /**
     * 删除歌曲评论
     * @param commentId 评论ID
     */
    void deleteSongComment(Long commentId);

    /**
     * 获取用户的歌曲评论列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 用户评论分页数据
     */
    Page<SongCommentVO> getMySongComments(Long userId, int pageNum, int pageSize);
}
