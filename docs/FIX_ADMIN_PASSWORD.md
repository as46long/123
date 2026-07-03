# 修复管理员登录密码问题

## 问题诊断

数据库初始化脚本 `database_init.sql` 中的管理员密码哈希值无效，导致无法登录。

**受影响的账户：**
- 用户名: admin
- 预期密码: admin123
- 问题: BCrypt 哈希值格式错误

## 解决方案

### 方法一：直接执行 SQL 更新（推荐）

连接到 MySQL 数据库，执行以下 SQL：

```sql
USE leyu_music;

-- 更新管理员密码为 admin123
UPDATE t_admin 
SET password = '$2a$10$4KRKpSXnElUm.gCH3/htOOIG6k6yKLVZWD4fuxdSBv1l/epsb3CnC' 
WHERE username = 'admin';

-- 如果 admin 用户不存在，则插入
INSERT INTO t_admin (username, password, role, status) 
SELECT 'admin', '$2a$10$4KRKpSXnElUm.gCH3/htOOIG6k6yKLVZWD4fuxdSBv1l/epsb3CnC', 'ADMIN', 1
WHERE NOT EXISTS (SELECT 1 FROM t_admin WHERE username = 'admin');
```

### 方法二：使用命令行工具

```bash
# 假设 MySQL 用户名为 root，密码为 123456
mysql -u root -p123456 leyu_music -e "UPDATE t_admin SET password = '\$2a\$10\$4KRKpSXnElUm.gCH3/htOOIG6k6yKLVZWD4fuxdSBv1l/epsb3CnC' WHERE username = 'admin';"
```

### 方法三：重新初始化数据库

如果是新环境，可以重新执行已修复的初始化脚本：

```bash
mysql -u root -p < /workspace/docs/database_init.sql
```

## 验证

执行上述修复后，使用以下凭据登录：
- 用户名: admin
- 密码: admin123

## 技术说明

BCrypt 哈希值由 Spring Security 的 `BCryptPasswordEncoder` 生成，
确保与后端代码中的密码验证逻辑兼容。
