package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.Song;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface SongMapper extends BaseMapper<Song> {

    @Select("SELECT * FROM t_song WHERE status = 1 ORDER BY play_count DESC LIMIT #{limit}")
    List<Song> findHotSongs(@Param("limit") int limit);

    @Select("SELECT * FROM t_song WHERE status = 1 AND category = #{category}")
    List<Song> findByCategory(@Param("category") String category);
}
