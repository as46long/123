package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 歌曲数据访问层
 * 提供歌曲表的CRUD操作及自定义查询
 */
@Mapper
public interface SongMapper extends BaseMapper<Song> {

    /**
     * 查询热门歌曲
     * @param limit 返回数量
     * @return 热门歌曲列表(按播放量降序)
     */
    @Select("SELECT * FROM t_song WHERE status = 1 ORDER BY play_count DESC LIMIT #{limit}")
    List<Song> findHotSongs(@Param("limit") int limit);

    /**
     * 按分类查询歌曲
     * @param category 歌曲分类
     * @return 该分类下的歌曲列表
     */
    @Select("SELECT * FROM t_song WHERE status = 1 AND category = #{category}")
    List<Song> findByCategory(@Param("category") String category);

    /**
     * 统计所有歌曲播放量总和
     * @return 播放量总和
     */
    @Select("SELECT IFNULL(SUM(play_count), 0) FROM t_song WHERE status = 1")
    Long getTotalPlayCount();
}
