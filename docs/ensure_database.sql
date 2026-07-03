-- 确保数据库表和数据正确
USE leyu_music;

-- 1. 检查并创建 t_song_comment 表
CREATE TABLE IF NOT EXISTS t_song_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content TEXT NOT NULL COMMENT '评论内容',
    likes INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态 0-已封禁 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_song_id (song_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌曲评论表';

-- 2. 检查并添加 t_comment 表的 comment_type 字段
SET @column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = 'leyu_music'
    AND table_name = 't_comment'
    AND column_name = 'comment_type'
);

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE t_comment ADD COLUMN comment_type TINYINT DEFAULT 1 COMMENT ''评论类型1-乐语留言 2-歌曲留言''',
    'SELECT ''字段已存在，无需添加'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 确保管理员账户存在（密码：admin123）
INSERT INTO t_admin (username, password, role, status)
VALUES ('admin', '$2a$10$UMJqo3WtbiBxgATqrn58Se5.tZdR3d6VV3MtcDgM7xssKPcdcXW8G', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE password = '$2a$10$UMJqo3WtbiBxgATqrn58Se5.tZdR3d6VV3MtcDgM7xssKPcdcXW8G', status = 1;

-- 4. 插入一些示例歌曲数据（如果不存在）
INSERT IGNORE INTO t_song (title, artist, album, category, is_vip, play_count, status) VALUES
('夜曲', '周杰伦', '十一月的萧邦', '流行', 1, 10000, 1),
('青花瓷', '周杰伦', '我很忙', '流行', 0, 8500, 1),
('晴天', '周杰伦', '叶惠美', '流行', 0, 9200, 1),
('海阔天空', 'Beyond', '乐与怒', '摇滚', 0, 7800, 1),
('光辉岁月', 'Beyond', '命运派对', '摇滚', 1, 8200, 1);

-- 5. 插入一些示例评论数据（用于测试）
INSERT IGNORE INTO t_comment (user_id, content, likes, status) VALUES
(1, '这个音乐平台很好用！', 10, 1),
(1, '歌曲质量很高，推荐给大家。', 8, 1);

-- 6. 插入一些示例歌曲评论数据（用于测试）
INSERT IGNORE INTO t_song_comment (song_id, user_id, content, likes, status) VALUES
(1, 1, '周杰伦的经典作品，每次听都有不同的感动。', 15, 1),
(1, 1, '这首歌陪伴我度过了很多艰难时刻。', 12, 1),
(2, 1, '中国风歌曲的代表作之一。', 8, 1),
(3, 1, '晴天的旋律很治愈。', 6, 1);

-- 7. 显示创建结果
SELECT '数据库设置完成' AS result;
SELECT 't_song_comment 表行数: ' || COUNT(*) AS song_comments FROM t_song_comment;
SELECT 't_comment 表行数: ' || COUNT(*) AS comments FROM t_comment;
SELECT 't_song 表行数: ' || COUNT(*) AS songs FROM t_song;
SELECT 't_admin 表行数: ' || COUNT(*) AS admins FROM t_admin;
