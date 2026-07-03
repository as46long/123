package com.leyu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户行为实体类
 * 用于记录用户对歌曲的操作行为，支持推荐算法
 * 对应数据库表: t_user_behavior
 */
@Data
@TableName("t_user_behavior")
public class UserBehavior {
    /** 行为ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 歌曲ID */
    private Long songId;

    /** 行为类型: PLAY-播放 COLLECT-收藏 SHARE-分享 */
    private String behaviorType;

    /** 行为评分，用于推荐算法权重计算 */
    private Integer score;

    /** 创建时间 */
    private LocalDateTime createTime;
}
