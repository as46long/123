package com.leyu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyu.dto.SongDTO;
import com.leyu.entity.Song;
import com.leyu.vo.SongVO;

import java.util.List;

/**
 * 歌曲服务接口
 * 提供歌曲的增删改查、搜索、推荐、分类管理等功能
 */
public interface SongService {
    /**
     * 根据ID获取歌曲实体
     * @param id 歌曲ID
     * @return 歌曲实体
     */
    Song getById(Long id);

    /**
     * 根据ID获取歌曲视图对象
     * @param id 歌曲ID
     * @param userId 用户ID(可选，用于判断是否已收藏)
     * @return 歌曲视图对象
     */
    SongVO getVOById(Long id, Long userId);

    /**
     * 分页查询歌曲列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键词
     * @param category 歌曲分类
     * @return 歌曲分页数据
     */
    Page<SongVO> getPage(int pageNum, int pageSize, String keyword, String category);

    /**
     * 搜索歌曲
     * @param keyword 搜索关键词
     * @param userId 用户ID(可选)
     * @return 匹配的歌曲列表
     */
    List<SongVO> search(String keyword, Long userId);

    /**
     * 获取推荐歌曲
     * @param userId 用户ID
     * @param num 推荐数量
     * @return 推荐歌曲列表
     */
    List<SongVO> getRecommend(Long userId, int num);

    /**
     * 获取热门歌曲
     * @param limit 返回数量
     * @return 热门歌曲列表
     */
    List<SongVO> getHot(int limit);

    /**
     * 按分类获取歌曲
     * @param category 分类名称
     * @param userId 用户ID(可选)
     * @return 该分类下的歌曲列表
     */
    List<SongVO> getByCategory(String category, Long userId);

    /**
     * 添加歌曲
     * @param dto 歌曲信息DTO
     */
    void add(SongDTO dto);

    /**
     * 更新歌曲
     * @param dto 歌曲更新DTO
     */
    void update(SongDTO dto);

    /**
     * 删除歌曲
     * @param id 歌曲ID
     */
    void delete(Long id);

    /**
     * 增加歌曲播放次数
     * @param id 歌曲ID
     * @param userId 用户ID(可选)
     */
    void incrementPlayCount(Long id, Long userId);

    /**
     * 获取歌曲歌词
     * @param id 歌曲ID
     * @return 歌词内容
     */
    String getLyrics(Long id);

    /**
     * 更新歌曲歌词
     * @param id 歌曲ID
     * @param lyrics 新歌词内容
     */
    void updateLyrics(Long id, String lyrics);

    /**
     * 获取所有歌曲分类
     * @return 分类名称列表
     */
    List<String> getAllCategories();
}
