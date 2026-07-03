package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 收藏数据访问层
 * 提供收藏表的CRUD操作及自定义查询
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    /**
     * 查询用户收藏的歌曲ID列表
     * @param userId 用户ID
     * @return 歌曲ID列表
     */
    @Select("SELECT song_id FROM t_favorite WHERE user_id = #{userId}")
    List<Long> findSongIdsByUserId(@Param("userId") Long userId);

    /**
     * 统计用户收藏数量
     * @param userId 用户ID
     * @return 收藏数量
     */
    @Select("SELECT COUNT(*) FROM t_favorite WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
}
