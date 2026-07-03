# API 问题修复汇总

## 已修复的问题

### 1. 管理后台 - 用户管理 API 路径不匹配

**问题：** 前端请求 `/user/list`，后端实际路径 `/user/page`

**修复文件：** `/workspace/leyu-admin-web/src/api/user.js`

```javascript
// 修复前
export const getUserList = (params) => request.get('/user/list', { params })

// 修复后
export const getUserList = (params) => request.get('/user/page', { params })
```

### 2. 小程序 - 收藏接口参数格式不匹配

**问题：** 小程序发送 JSON body，后端期望 URL 参数

**修复文件：** 
- `/workspace/leyu-admin-backend/src/main/java/com/leyu/dto/FavoriteDTO.java` (新增)
- `/workspace/leyu-admin-backend/src/main/java/com/leyu/controller/FavoriteController.java` (修改)

```java
// 修复后
@PostMapping("/add")
public Result<Void> add(@RequestHeader("Authorization") String token, @RequestBody FavoriteDTO dto) {
    Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
    favoriteService.add(userId, dto.getSongId());
    return Result.success();
}
```

### 3. 数据库 - 管理员密码哈希值错误

**问题：** 初始化脚本中的 BCrypt 哈希值无效

**修复文件：** `/workspace/docs/database_init.sql`

## 需要手动执行的步骤

### 步骤 1: 更新数据库

```bash
# 重新初始化数据库（会清空数据）
mysql -u root -p < /workspace/docs/database_init.sql

# 或者只更新管理员密码
mysql -u root -p leyu_music -e "UPDATE t_admin SET password = '\$2a\$10\$UMJqo3WtbiBxgATqrn58Se5.tZdR3d6VV3MtcDgM7xssKPcdcXW8G' WHERE username = 'admin';"
```

### 步骤 2: 重新编译后端

```bash
cd /workspace/leyu-admin-backend
mvn clean compile
mvn spring-boot:run
```

### 步骤 3: 重启前端

```bash
cd /workspace/leyu-admin-web
npm run dev
```

### 步骤 4: 小程序设置

在微信开发者工具中：
1. 点击右上角「详情」
2. 勾选「不校验合法域名」

## API 对照表

### 管理后台 API

| 功能 | 前端路径 | 后端路径 | 状态 |
|------|----------|----------|------|
| 登录 | `/admin/login` | `/admin/login` | ✅ |
| 用户列表 | `/user/page` | `/user/page` | ✅ 已修复 |
| 用户详情 | `/user/detail/{id}` | `/user/detail/{id}` | ✅ |
| 更新状态 | `/user/status/{id}` | `/user/status/{id}` | ✅ |
| 删除用户 | `/user/delete/{id}` | `/user/delete/{id}` | ✅ |

### 小程序 API

| 功能 | 小程序路径 | 后端路径 | 状态 |
|------|------------|----------|------|
| 用户登录 | `/user/login` | `/user/login` | ✅ |
| 微信登录 | `/user/wxLogin` | `/user/wxLogin` | ✅ |
| 用户信息 | `/user/info` | `/user/info` | ✅ |
| 推荐歌曲 | `/song/recommend/{userId}` | `/song/recommend/{userId}` | ✅ |
| 搜索歌曲 | `/song/search` | `/song/search` | ✅ |
| 歌曲详情 | `/song/detail/{id}` | `/song/detail/{id}` | ✅ |
| 添加收藏 | `/favorite/add` | `/favorite/add` | ✅ 已修复 |
| 取消收藏 | `/favorite/delete/{songId}` | `/favorite/delete/{songId}` | ✅ |
| 收藏列表 | `/favorite/list` | `/favorite/list` | ✅ |
| 订单列表 | `/order/my` | `/order/my` | ✅ |
| 留言列表 | `/comment/list` | `/comment/list` | ✅ |
