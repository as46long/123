-- ==============================================
-- 乐语匣子音乐平台 - 数据库初始化脚本
-- ==============================================

CREATE DATABASE IF NOT EXISTS leyu_music DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE leyu_music;

-- 用户表
CREATE TABLE t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    wx_openid VARCHAR(100) COMMENT '微信OpenID',
    is_vip TINYINT DEFAULT 0 COMMENT '是否VIP 0-否 1-是',
    vip_expire_time DATETIME COMMENT 'VIP过期时间',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 歌曲表
CREATE TABLE t_song (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '歌曲ID',
    title VARCHAR(100) NOT NULL COMMENT '歌曲名',
    artist VARCHAR(100) COMMENT '歌手',
    album VARCHAR(100) COMMENT '专辑',
    cover_url VARCHAR(255) COMMENT '封面URL',
    audio_url VARCHAR(255) NOT NULL COMMENT '音频URL',
    lyrics_url VARCHAR(255) COMMENT '歌词URL',
    duration INT COMMENT '时长(秒)',
    category VARCHAR(50) COMMENT '分类',
    is_vip TINYINT DEFAULT 0 COMMENT '是否VIP歌曲 0-否 1-是',
    play_count INT DEFAULT 0 COMMENT '播放量',
    status TINYINT DEFAULT 1 COMMENT '状态 0-下架 1-上架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌曲表';

-- 收藏表
CREATE TABLE t_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_song (user_id, song_id),
    INDEX idx_user_id (user_id),
    INDEX idx_song_id (song_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 订单表
CREATE TABLE t_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(64) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    package_type VARCHAR(20) COMMENT '套餐类型 MONTH/QUARTER/YEAR',
    amount DECIMAL(10,2) COMMENT '金额',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态 0-未支付 1-已支付',
    pay_time DATETIME COMMENT '支付时间',
    expire_time DATETIME COMMENT '会员到期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 留言表
CREATE TABLE t_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '留言ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content TEXT NOT NULL COMMENT '留言内容',
    likes INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 1 COMMENT '状态 0-待审核 1-已通过 2-已拒绝',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言表';

-- 管理员表
CREATE TABLE t_admin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role VARCHAR(20) DEFAULT 'ADMIN' COMMENT '角色',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 用户行为表（用于推荐算法）
CREATE TABLE t_user_behavior (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    behavior_type VARCHAR(20) COMMENT '行为类型 PLAY/COLLECT/SHARE',
    score INT DEFAULT 1 COMMENT '评分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_song_id (song_id),
    UNIQUE KEY uk_user_song_behavior (user_id, song_id, behavior_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表';

-- 插入默认管理员（密码：admin123）
 -- 插入默认管理员（密码：admin123）
 -- BCrypt哈希值通过 BCryptPasswordEncoder.encode("admin123") 生成
 INSERT INTO t_admin (username, password) VALUES ('admin', '$2a$10$UMJqo3WtbiBxgATqrn58Se5.tZdR3d6VV3MtcDgM7xssKPcdcXW8G');

-- 插入示例歌曲数据
INSERT INTO t_song (title, artist, album, category, is_vip, play_count, status) VALUES
('夜曲', '周杰伦', '十一月的萧邦', '流行', 1, 10000, 1),
('青花瓷', '周杰伦', '我很忙', '流行', 0, 8500, 1),
('晴天', '周杰伦', '叶惠美', '流行', 0, 9200, 1),
('海阔天空', 'Beyond', '乐与怒', '摇滚', 0, 7800, 1),
('光辉岁月', 'Beyond', '命运派对', '摇滚', 1, 8200, 1),
('稻香', '周杰伦', '魔杰座', '流行', 0, 6800, 1),
('七里香', '周杰伦', '七里香', '流行', 1, 7500, 1),
('告白气球', '周杰伦', '周杰伦的床边故事', '流行', 0, 9000, 1),
('爱情转移', '陈奕迅', '认了吧', '流行', 1, 7200, 1),
('十年', '陈奕迅', '黑•白•灰', '流行', 0, 8500, 1);
