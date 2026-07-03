# 故障排查指南

## 问题现象

1. 后端管理页面提示"服务器内部错误"
2. 小程序端操作提示"请求失败"

## 可能原因

### 1. 数据库连接问题

检查 MySQL 服务是否运行：

```bash
# Linux/Mac
systemctl status mysql
# 或
service mysql status

# 测试连接
mysql -u root -p123456 -e "SELECT 1"
```

### 2. 数据库表不存在

确认数据库已初始化：

```bash
mysql -u root -p123456 -e "USE leyu_music; SHOW TABLES;"
```

如果表不存在，执行初始化：

```bash
mysql -u root -p123456 < /workspace/docs/database_init.sql
```

### 3. 后端服务未启动

检查后端服务状态：

```bash
# 检查端口 8080 是否被占用
netstat -tlnp | grep 8080
# 或
lsof -i :8080

# 检查 Java 进程
ps aux | grep java
```

启动后端服务：

```bash
cd /workspace/leyu-admin-backend
mvn spring-boot:run
```

### 4. 前端代理配置问题

管理后台前端通过 Vite 代理转发请求到后端。
确保前端已启动：

```bash
cd /workspace/leyu-admin-web
npm install
npm run dev
```

### 5. 小程序网络请求配置

小程序 API 配置文件：`/workspace/miniprogram/utils/api.js`

当前配置：
```javascript
const BASE_URL = 'http://localhost:8080/api'
```

**重要：** 微信开发者工具中需要：
1. 点击右上角「详情」
2. 勾选「不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书」

如果是真机调试，需要将 `localhost` 改为实际服务器 IP。

## 完整启动流程

### 步骤 1: 启动 MySQL

```bash
# 确认 MySQL 运行
systemctl start mysql
```

### 步骤 2: 初始化数据库

```bash
mysql -u root -p123456 < /workspace/docs/database_init.sql
```

### 步骤 3: 修复管理员密码

```bash
mysql -u root -p123456 leyu_music -e "UPDATE t_admin SET password = '\$2a\$10\$UMJqo3WtbiBxgATqrn58Se5.tZdR3d6VV3MtcDgM7xssKPcdcXW8G' WHERE username = 'admin';"
```

### 步骤 4: 启动后端服务

```bash
cd /workspace/leyu-admin-backend
mvn spring-boot:run
```

等待看到类似输出：
```
Started LeyuApplication in X.XXX seconds
```

### 步骤 5: 启动管理后台前端

```bash
cd /workspace/leyu-admin-web
npm install
npm run dev
```

访问：http://localhost:3000

### 步骤 6: 运行小程序

1. 打开微信开发者工具
2. 导入 `/workspace/miniprogram` 目录
3. 勾选「不校验合法域名」
4. 点击编译

## 验证各服务状态

```bash
# 后端 API 文档
curl http://localhost:8080/doc.html

# 测试登录接口
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 前端管理后台
curl http://localhost:3000
```

## 常见错误及解决

### 错误 1: Communications link failure

**原因：** MySQL 未启动或连接配置错误

**解决：**
1. 启动 MySQL 服务
2. 检查 `application.yml` 中的数据库配置

### 错误 2: 401 Unauthorized

**原因：** Token 无效或未登录

**解决：** 重新登录获取新 Token

### 错误 3: 403 Forbidden

**原因：** 权限不足

**解决：** 确认用户角色正确（管理员需要 ADMIN 角色）

### 错误 4: 500 Internal Server Error

**原因：** 后端代码执行异常

**解决：** 查看后端日志定位具体错误
