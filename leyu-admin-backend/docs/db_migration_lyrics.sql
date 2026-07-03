-- 为歌曲表添加歌词字段
ALTER TABLE t_song ADD COLUMN lyrics TEXT COMMENT '歌词内容';

-- 说明：此SQL用于为现有的歌曲表添加歌词存储字段
-- 执行此SQL后，歌词内容将直接存储在数据库中
-- 无需单独的歌词文件
