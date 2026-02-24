package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    @Select("SELECT song_id FROM t_favorite WHERE user_id = #{userId}")
    List<Long> findSongIdsByUserId(@Param("userId") Long userId);
}
