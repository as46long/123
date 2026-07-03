# 403 Forbidden 错误解决方案

## 问题描述
访问 `http://localhost:8080/api/admin/songComment/list` 时出现 403 Forbidden 错误。

## 根本原因
1. Spring Security 配置中，`/api/admin/**` 路径需要 ADMIN 角色
2. 您当前使用的 token 可能不是 ADMIN 角色
3. 或者根本没有携带有效的 token

## 解决方案

### 方案1：使用管理员账户登录

#### 1.1 获取管理员账户信息
默认管理员账户：
- 用户名：`admin`
- 密码：`admin123`

#### 1.2 登录获取 token
```bash
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

响应示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "user": {...},
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

#### 1.3 使用 token 访问接口
```bash
curl -X GET "http://localhost:8080/api/admin/songComment/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 方案2：检查当前 token 的角色

#### 2.1 解析 token
在线 JWT 解析工具：https://jwt.io/

检查 payload 中的 `role` 字段，应该是 `"ADMIN"`。

#### 2.2 使用管理员工具查看
```bash
# 查看当前登录用户信息（需要携带token）
curl -X GET http://localhost:8080/api/user/info \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 方案3：临时降低安全级别（仅用于开发测试）

#### 3.1 修改 SecurityConfig
在 `SecurityConfig.java` 中临时修改：
```java
// 将这行：
.requestMatchers("/api/admin/**").hasRole("ADMIN")
// 改为：
.requestMatchers("/api/admin/**").permitAll()
```

#### 3.2 重启后端服务
```bash
cd /workspace/leyu-admin-backend
mvn spring-boot:run
```

⚠️ **注意：** 生产环境务必恢复权限配置！

### 方案4：创建新的管理员账户

如果默认账户有问题，可以创建新的管理员账户。

#### 4.1 直接在数据库中插入
```sql
-- 生成密码哈希（密码：admin123）
-- 在 Java 中执行：new BCryptPasswordEncoder().encode("admin123")
-- 得到的哈希值类似于：$2a$10$...

INSERT INTO t_admin (username, password, role, status)
VALUES ('newadmin', '$2a$10$...', 'ADMIN', 1);
```

#### 4.2 或者使用现有的 User 表作为管理员
```sql
-- 将现有用户提升为管理员
UPDATE t_user SET username = 'admin_user' WHERE id = 1;
-- 然后使用 admin_user 登录，后端代码需要临时修改以支持用户作为管理员
```

### 方案5：检查 JWT 配置

#### 5.1 检查 JwtUtil 配置
确保 `JwtUtil` 正确生成和验证 token：
```java
// 生成 token 时包含角色信息
Map<String, Object> claims = new HashMap<>();
claims.put("role", "ADMIN");
return Jwts.builder()
    .setClaims(claims)
    .setSubject(username)
    .signWith(secretKey)
    .compact();
```

#### 5.2 检查 SecurityFilter 确保正确解析角色
在 `JwtAuthenticationFilter.java` 中：
```java
String role = jwtUtil.parseToken(token).get("role", String.class);
// 确保角色被正确设置到 Authentication 中
```

## 调试步骤

### 1. 查看后端日志
```bash
cd /workspace/leyu-admin-backend
tail -f logs/spring.log
```

查找与认证相关的错误信息。

### 2. 启用详细日志
在 `application.yml` 中添加：
```yaml
logging:
  level:
    org.springframework.security: DEBUG
    com.leyu.security: DEBUG
```

### 3. 测试权限配置
```bash
# 测试公开接口（应该成功）
curl http://localhost:8080/api/song/list

# 测试管理员接口（不带token，应该401）
curl http://localhost:8080/api/admin/songComment/list

# 测试管理员接口（带用户token，应该403）
curl http://localhost:8080/api/admin/songComment/list \
  -H "Authorization: Bearer USER_TOKEN"

# 测试管理员接口（带管理员token，应该成功）
curl http://localhost:8080/api/admin/songComment/list \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### 4. 检查数据库管理员账户
```sql
USE leyu_music;

-- 查看管理员账户
SELECT id, username, role, status, last_login_time FROM t_admin;

-- 检查密码哈希
SELECT username, password FROM t_admin WHERE username = 'admin';
```

## 常见错误及解决方案

### 错误1：Invalid token
**原因：** Token 格式错误或已过期

**解决方案：**
- 检查 token 是否包含 "Bearer " 前缀
- 重新登录获取新 token
- 检查 JWT 过期时间配置

### 错误2：Access Denied
**原因：** 用户角色不足

**解决方案：**
- 确保使用管理员账户登录
- 检查 token 中的 role 字段是否为 "ADMIN"
- 如果是用户账户，需要提升权限

### 错误3：User not found
**原因：** Token 中的用户 ID 在数据库中不存在

**解决方案：**
- 检查数据库中的用户数据
- 确保用户状态为正常（status = 1）
- 重新登录获取正确的 token

## 快速测试脚本

创建测试脚本 `/workspace/test_admin_api.sh`：

```bash
#!/bin/bash

echo "=== 管理员API测试 ==="

# 1. 测试登录
echo "1. 测试管理员登录..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

echo "登录响应: $LOGIN_RESPONSE"

# 提取token
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ 获取token失败"
  exit 1
fi

echo "✓ 获取token成功: ${TOKEN:0:20}..."

# 2. 测试权限
echo "2. 测试管理员权限..."
API_RESPONSE=$(curl -s -X GET "http://localhost:8080/api/admin/songComment/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN")

echo "API响应: $API_RESPONSE"

# 检查响应状态
if echo $API_RESPONSE | grep -q '"code":200'; then
  echo "✓ API调用成功"
else
  echo "❌ API调用失败"
  echo "完整响应: $API_RESPONSE"
fi

echo "=== 测试完成 ==="
```

## 推荐的调试流程

1. **确认管理员账户：** 检查数据库中 admin 账户是否存在且状态正常
2. **重新登录：** 使用 admin/admin123 登录获取新 token
3. **验证 token：** 使用 JWT 工具检查 token 中的 role 是否为 ADMIN
4. **测试接口：** 使用正确的 token 访问管理员接口
5. **查看日志：** 如果仍然失败，查看后端详细日志
6. **临时降级：** 如果是开发环境，可以临时降低权限进行测试

## 安全提醒

⚠️ **重要：** 
- 方案3（降低安全级别）仅用于开发测试
- 生产环境必须保持严格的权限控制
- 不要将管理员 token 硬编码在前端代码中
- 定期更换管理员密码
- 监控管理员登录日志，发现异常及时处理
