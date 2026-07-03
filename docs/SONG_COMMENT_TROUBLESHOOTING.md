# 歌曲留言功能故障排除指南

## 问题1：后端歌曲留言管理点击无反应

### 可能原因
1. 数据库表 `t_song_comment` 未创建
2. 后端服务未启动
3. 前端 API 配置错误
4. 浏览器控制台有错误

### 解决步骤

#### 1. 检查数据库表
在 MySQL 客户端中执行：
```sql
USE leyu_music;
SHOW TABLES LIKE 't_song_comment';
```

如果表不存在，执行：
```sql
-- 直接复制 /workspace/manual_db_setup.sql 的内容执行
```

#### 2. 检查后端服务
```bash
# 查看后端是否运行
ps aux | grep java

# 查看后端日志
cd leyu-admin-backend
tail -f logs/spring.log
```

#### 3. 测试 API 接口
在浏览器中访问：
```
http://localhost:8080/api/admin/songComment/list?pageNum=1&pageSize=10
```

#### 4. 检查前端配置
检查 `/workspace/leyu-admin-web/src/api/songComment.js` 中的 API 路径是否正确：
```javascript
url: '/api/admin/songComment/list'
```

#### 5. 查看浏览器控制台
打开浏览器开发者工具 (F12)，查看 Console 和 Network 标签页的错误信息。

## 问题2：小程序端无法发送歌曲留言

### 可能原因
1. 用户未登录
2. 后端接口配置错误
3. 数据库表未创建
4. 网络请求失败

### 解决步骤

#### 1. 检查登录状态
在小程序中检查：
- 是否已登录（查看 `app.globalData.token` 是否存在）
- Token 是否有效

#### 2. 检查 API 配置
检查 `/workspace/miniprogram/utils/api.js`：
```javascript
postSongComment: (data) => request({ url: '/songComment/post', method: 'POST', data }),
```

#### 3. 查看请求日志
在小程序开发者工具中：
1. 打开 Console 标签页
2. 尝试发送评论
3. 查看是否有请求日志输出（已添加 console.log）
4. 查看是否有错误信息

#### 4. 检查后端日志
```bash
cd leyu-admin-backend
tail -f logs/spring.log
```

查看是否有相关错误信息。

#### 5. 手动测试后端接口
使用 curl 或 Postman 测试：
```bash
curl -X POST http://localhost:8080/api/songComment/post \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"songId":1,"content":"测试评论"}'
```

## 常见错误及解决方案

### 错误1：Table 'leyu_music.t_song_comment' doesn't exist
**原因：** 数据库表未创建

**解决方案：**
```sql
-- 执行创建表语句
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

### 错误2：401 Unauthorized 或 403 Forbidden
**原因：** 用户未登录或 Token 无效

**解决方案：**
1. 检查用户是否已登录
2. 清除无效 token 重新登录
3. 检查后端 JWT 配置

### 错误3：网络请求失败
**原因：** 网络连接问题或后端服务未启动

**解决方案：**
1. 检查后端服务是否运行
2. 检查网络连接
3. 检查防火墙设置

### 错误4：发布评论失败
**原因：** 歌曲不存在或数据库操作失败

**解决方案：**
1. 检查歌曲 ID 是否存在
2. 查看后端日志了解具体错误
3. 检查数据库连接是否正常

## 调试技巧

### 1. 启用详细日志
在 `application.yml` 中设置：
```yaml
logging:
  level:
    com.leyu: DEBUG
```

### 2. 查看小程序网络请求
在微信开发者工具中：
1. 打开 Network 标签页
2. 查看所有网络请求的详细信息
3. 查看请求和响应的详细内容

### 3. 使用数据库查询验证
```sql
-- 查看歌曲评论数据
SELECT * FROM t_song_comment ORDER BY create_time DESC LIMIT 10;

-- 查看相关用户信息
SELECT u.*, c.content, c.create_time
FROM t_user u
JOIN t_song_comment c ON u.id = c.user_id
ORDER BY c.create_time DESC LIMIT 10;
```

### 4. 测试分页功能
```sql
-- 测试分页查询
SELECT * FROM t_song_comment LIMIT 10 OFFSET 0;
SELECT * FROM t_song_comment LIMIT 10 OFFSET 10;
```

## 预防措施

### 1. 定期备份数据库
```bash
mysqldump -u root -p leyu_music > backup_$(date +%Y%m%d).sql
```

### 2. 监控后端日志
定期检查后端日志，及时发现和处理错误。

### 3. 测试环境验证
在生产环境部署前，在测试环境中充分测试所有功能。

### 4. 数据库连接池配置
确保 MyBatis-Plus 配置了合理的连接池参数：
```yaml
mybatis-plus:
  global-config:
    db-config:
      table-prefix: t_
      id-type: auto
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 联系支持

如果以上解决方案都无法解决问题，请提供以下信息：
1. 错误截图
2. 浏览器控制台错误信息
3. 后端日志相关内容
4. 小程序开发者工具中的网络请求信息
5. 数据库查询结果
