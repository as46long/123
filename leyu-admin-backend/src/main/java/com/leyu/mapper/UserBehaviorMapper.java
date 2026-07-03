package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 用户行为数据访问层
 * 提供用户行为表的CRUD操作及自定义查询
 * 用于推荐算法的数据支持
 */
@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {

    /**
     * 查询用户的所有行为记录
     * @param userId 用户ID
     * @return 行为记录列表
     */
    @Select("SELECT * FROM t_user_behavior WHERE user_id = #{userId}")
    List<UserBehavior> findByUserId(@Param("userId") Long userId);

    /**
     * 查询用户互动过的歌曲ID列表
     * @param userId 用户ID
     * @return 歌曲ID列表
     */
    @Select("SELECT DISTINCT song_id FROM t_user_behavior WHERE user_id = #{userId}")
    List<Long> findSongIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询互动过某歌曲的用户ID列表
     * @param songId 歌曲ID
     * @return 用户ID列表
     */
    @Select("SELECT DISTINCT user_id FROM t_user_behavior WHERE song_id = #{songId}")
    List<Long> findUserIdsBySongId(@Param("songId") Long songId);

    /**
     * 查询歌曲的所有行为记录
     * @param songId 歌曲ID
     * @return 行为记录列表
     */
    @Select("SELECT * FROM t_user_behavior WHERE song_id = #{songId}")
    List<UserBehavior> findBySongId(@Param("songId") Long songId);

    /**
     * 查询所有有行为记录的歌曲ID
     * @return 歌曲ID列表
     */
    @Select("SELECT DISTINCT song_id FROM t_user_behavior")
    List<Long> findAllSongIds();

    /**
     * 统计用户听过的歌曲数量(去重)
     * @param userId 用户ID
     * @return 歌曲数量
     */
    @Select("SELECT COUNT(DISTINCT song_id) FROM t_user_behavior WHERE user_id = #{userId} AND behavior_type = 'play'")
    int countUserPlaySongs(@Param("userId") Long userId);

    /**
     * 统计用户的播放记录总数
     * @param userId 用户ID
     * @return 播放记录数
     */
    @Select("SELECT COUNT(*) FROM t_user_behavior WHERE user_id = #{userId} AND behavior_type = 'play'")
    int countUserPlayRecords(@Param("userId") Long userId);
}
