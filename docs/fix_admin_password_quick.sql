-- ==============================================
-- 快速修复管理员密码
-- 执行方式: mysql -u root -p leyu_music < fix_admin_password_quick.sql
-- ==============================================

USE leyu_music;

-- 更新管理员密码为 admin123
-- 哈希值已验证可用
UPDATE t_admin 
SET password = '$2a$10$UMJqo3WtbiBxgATqrn58Se5.tZdR3d6VV3MtcDgM7xssKPcdcXW8G' 
WHERE username = 'admin';

-- 如果没有更新任何行，则插入新管理员
INSERT INTO t_admin (username, password, role, status, create_time)
SELECT 'admin', '$2a$10$UMJqo3WtbiBxgATqrn58Se5.tZdR3d6VV3MtcDgM7xssKPcdcXW8G', 'ADMIN', 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM t_admin WHERE username = 'admin');

SELECT '修复完成，请使用 admin/admin123 登录' AS message;
