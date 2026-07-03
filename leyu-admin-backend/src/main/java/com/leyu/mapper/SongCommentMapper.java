package com.leyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyu.entity.SongComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 歌曲评论数据访问层
 * 提供歌曲评论表的CRUD操作及自定义查询
 */
@Mapper
public interface SongCommentMapper extends BaseMapper<SongComment> {

    /**
     * 统计用户的歌曲评论数量
     * @param userId 用户ID
     * @return 评论数量
     */
    @Select("SELECT COUNT(*) FROM t_song_comment WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
}
