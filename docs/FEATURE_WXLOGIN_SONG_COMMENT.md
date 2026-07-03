# 微信一键登录与歌曲评论功能实现

## 功能概述

本次实现包含以下主要功能：
1. 微信一键登录功能
2. 歌曲评论功能（发布、查看、点赞）
3. 管理后台留言管理拆分为乐语留言和歌曲留言管理
4. 小程序端我的留言页面支持两种类型留言切换

## 数据库变更

### 1. 创建歌曲评论表
```sql
CREATE TABLE t_song_comment (
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
```

### 2. 更新现有留言表
```sql
ALTER TABLE t_comment ADD COLUMN comment_type TINYINT DEFAULT 1 COMMENT '评论类型 1-乐语留言 2-歌曲留言';
```

## 后端实现

### 1. 新增实体类
- `SongComment.java` - 歌曲评论实体
- `SongCommentVO.java` - 歌曲评论视图对象
- `SongCommentDTO.java` - 歌曲评论数据传输对象

### 2. 新增 Mapper
- `SongCommentMapper.java` - 歌曲评论数据访问接口

### 3. 新增 Service
- `SongCommentService.java` - 歌曲评论服务接口
- `SongCommentServiceImpl.java` - 歌曲评论服务实现

### 4. 新增 Controller
- `SongCommentController.java` - 用户端歌曲评论接口
- `AdminSongCommentController.java` - 管理员歌曲评论接口

### 5. 优化微信登录
- 新增 `WechatUtil.java` 工具类，实现真正的微信登录
- 优化 `UserServiceImpl.java` 中的 `wxLogin` 方法
- 在 `application.yml` 中添加微信配置项

### 6. 扩展歌曲服务
- 在 `SongService` 中添加 `getAllCategories()` 方法
- 在 `SongServiceImpl` 中实现获取所有歌曲分类

## 小程序端实现

### 1. 播放器页面
- 在收藏按钮旁添加评论按钮
- 实现评论弹窗，显示评论列表
- 支持发布评论和点赞评论
- 新增相关样式和交互逻辑

### 2. 登录页面
- 添加微信一键登录按钮
- 实现微信登录逻辑

### 3. 我的留言页面
- 添加 Tab 切换，支持查看乐语留言和歌曲留言
- 分别加载两种类型的评论数据
- 优化页面样式和交互

### 4. API 接口
- 在 `api.js` 中添加歌曲评论相关接口
- 添加微信登录接口

## 管理后台实现

### 1. 新增页面
- `songComment/index.vue` - 歌曲留言管理页面
- `leyuComment/index.vue` - 乐语留言管理页面

### 2. 新增 API
- `api/songComment.js` - 歌曲评论相关接口
- `api/comment.js` - 乐语留言相关接口

### 3. 更新路由
- 在 `router/index.js` 中添加新页面路由
- 在 `MainLayout.vue` 中更新侧边栏菜单

## 功能特性

### 微信登录
- 支持微信一键登录
- 自动创建新用户
- 返回 JWT token

### 歌曲评论
- 用户可以发布歌曲评论
- 查看某首歌曲的所有评论
- 点赞评论功能
- 分页加载评论

### 管理后台
#### 乐语留言管理
- 查看所有乐语留言
- 审核留言（通过/拒绝）
- 删除留言
- 按状态筛选

#### 歌曲留言管理
- 查看所有歌曲评论
- 封禁/解封评论
- 删除评论
- 按歌曲类型筛选
- 按状态筛选（正常/已封禁）

### 小程序端我的留言
- Tab 切换查看两种类型留言
- 显示歌曲信息的歌曲留言
- 显示留言状态（正常/已封禁/已通过/已拒绝）
- 分页加载

## 配置说明

### 微信登录配置
在 `leyu-admin-backend/src/main/resources/application.yml` 中配置：
```yaml
wechat:
  appid: your_appid_here
  secret: your_secret_here
```

## 使用说明

### 1. 执行数据库脚本
```bash
mysql -u root -p leyu_music < docs/song_comment_tables.sql
```

### 2. 配置微信登录
编辑后端配置文件，填入实际的微信小程序 AppID 和 AppSecret

### 3. 启动后端服务
```bash
cd leyu-admin-backend
mvn spring-boot:run
```

### 4. 启动管理后台
```bash
cd leyu-admin-web
npm install
npm run dev
```

### 5. 运行小程序
使用微信开发者工具打开 `miniprogram` 目录

## 注意事项

1. 微信登录需要在微信公众平台配置服务器域名白名单
2. 管理后台需要后端服务正常运行
3. 小程序端需要配置正确的后端 API 地址
4. 数据库表创建后需要重启后端服务

## 文件清单

### 后端新增文件
- `leyu-admin-backend/src/main/java/com/leyu/entity/SongComment.java`
- `leyu-admin-backend/src/main/java/com/leyu/vo/SongCommentVO.java`
- `leyu-admin-backend/src/main/java/com/leyu/dto/SongCommentDTO.java`
- `leyu-admin-backend/src/main/java/com/leyu/mapper/SongCommentMapper.java`
- `leyu-admin-backend/src/main/java/com/leyu/service/SongCommentService.java`
- `leyu-admin-backend/src/main/java/com/leyu/service/impl/SongCommentServiceImpl.java`
- `leyu-admin-backend/src/main/java/com/leyu/controller/SongCommentController.java`
- `leyu-admin-backend/src/main/java/com/leyu/controller/AdminSongCommentController.java`
- `leyu-admin-backend/src/main/java/com/leyu/utils/WechatUtil.java`
- `docs/song_comment_tables.sql`

### 前端新增文件
- `leyu-admin-web/src/views/songComment/index.vue`
- `leyu-admin-web/src/views/leyuComment/index.vue`
- `leyu-admin-web/src/api/songComment.js`
- `leyu-admin-web/src/api/comment.js`

### 小程序修改文件
- `miniprogram/pages/player/player.wxml`
- `miniprogram/pages/player/player.wxss`
- `miniprogram/pages/player/player.js`
- `miniprogram/pages/login/login.js`
- `miniprogram/pages/my-comments/my-comments.wxml`
- `miniprogram/pages/my-comments/my-comments.wxss`
- `miniprogram/pages/my-comments/my-comments.js`
- `miniprogram/utils/api.js`

### 管理后台修改文件
- `leyu-admin-web/src/router/index.js`
- `leyu-admin-web/src/layouts/MainLayout.vue`

### 后端修改文件
- `leyu-admin-backend/src/main/java/com/leyu/service/SongService.java`
- `leyu-admin-backend/src/main/java/com/leyu/service/impl/SongServiceImpl.java`
- `leyu-admin-backend/src/main/java/com/leyu/service/impl/UserServiceImpl.java`
- `leyu-admin-backend/src/main/resources/application.yml`
