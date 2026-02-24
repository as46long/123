package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {

    @Select("SELECT * FROM t_user_behavior WHERE user_id = #{userId}")
    List<UserBehavior> findByUserId(@Param("userId") Long userId);

    @Select("SELECT DISTINCT song_id FROM t_user_behavior WHERE user_id = #{userId}")
    List<Long> findSongIdsByUserId(@Param("userId") Long userId);

    @Select("SELECT DISTINCT user_id FROM t_user_behavior WHERE song_id = #{songId}")
    List<Long> findUserIdsBySongId(@Param("songId") Long songId);

    @Select("SELECT * FROM t_user_behavior WHERE song_id = #{songId}")
    List<UserBehavior> findBySongId(@Param("songId") Long songId);

    @Select("SELECT DISTINCT song_id FROM t_user_behavior")
    List<Long> findAllSongIds();
}
