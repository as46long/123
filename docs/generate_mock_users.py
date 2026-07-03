#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成模拟用户数据SQL脚本
"""

import random
import string
from datetime import datetime, timedelta

# BCrypt哈希值 (123456)
PASSWORD_HASH = '$2a$12$iBPDikjgv2p443LYyux3UuUVd40rFsRlF68frbY3i3jv21ERmmH1O'

# 会员套餐类型和对应金额
VIP_PACKAGES = [
    ('WEEK', 6.00, 7),
    ('MONTH', 18.00, 30),
    ('QUARTER', 48.00, 90),
    ('YEAR', 168.00, 365),
]

# 常用中文姓氏
SURNAMES = ['张', '李', '王', '刘', '陈', '杨', '赵', '黄', '周', '吴',
            '徐', '孙', '胡', '朱', '高', '林', '何', '郭', '马', '罗',
            '梁', '宋', '郑', '谢', '韩', '唐', '冯', '于', '董', '萧',
            '程', '曹', '袁', '邓', '许', '傅', '沈', '曾', '彭', '吕',
            '苏', '卢', '蒋', '蔡', '贾', '丁', '魏', '薛', '叶', '阎']

# 常用名字用字
NAME_CHARS = ['伟', '芳', '娜', '秀英', '敏', '静', '丽', '强', '磊', '洋',
              '勇', '军', '杰', '涛', '明', '超', '秀兰', '霞', '平', '刚',
              '桂英', '华', '飞', '玉兰', '萍', '红', '玉梅', '辉', '建华',
              '玲', '建国', '建军', '英', '华', '文', '志强', '淑珍', '文华',
              '思远', '雨萱', '子涵', '欣怡', '浩然', '宇轩', '梓涵', '一诺',
              '欣妍', '子墨', '可馨', '诗涵', '雨桐', '思源', '星辰', '佳琪']

def random_username():
    """生成随机用户名"""
    surname = random.choice(SURNAMES)
    name = random.choice(NAME_CHARS)
    # 有时加数字后缀避免重复
    if random.random() < 0.3:
        return f"{surname}{name}{random.randint(1, 999):03d}"
    return f"{surname}{name}"

def random_phone():
    """生成随机手机号"""
    prefixes = ['130', '131', '132', '133', '135', '136', '137', '138', '139',
                '150', '151', '152', '153', '155', '156', '157', '158', '159',
                '170', '176', '177', '178', '180', '181', '182', '183', '185',
                '186', '187', '188', '189']
    return random.choice(prefixes) + ''.join([str(random.randint(0, 9)) for _ in range(8)])

def random_email(username):
    """生成随机邮箱"""
    domains = ['qq.com', '163.com', '126.com', 'gmail.com', 'outlook.com', 'sina.com']
    return f"{username.lower()}@{random.choice(domains)}"

def random_create_time(date):
    """生成指定日期的随机时间"""
    hour = random.randint(0, 23)
    minute = random.randint(0, 59)
    second = random.randint(0, 59)
    return f"{date} {hour:02d}:{minute:02d}:{second:02d}"

def generate_order_no(user_id, seq):
    """生成订单号"""
    timestamp = datetime.now().strftime('%Y%m%d%H%M%S')
    return f"ORD{timestamp}{user_id:06d}{seq:04d}"

def main():
    users = []
    orders = []
    user_id = 1
    order_id = 1
    used_usernames = set()
    
    # 日期范围: 2026-05-15 到 2026-05-21
    start_date = datetime(2026, 5, 15)
    end_date = datetime(2026, 5, 21)
    
    current_date = start_date
    while current_date <= end_date:
        date_str = current_date.strftime('%Y-%m-%d')
        # 每天10-15个用户
        num_users = random.randint(10, 15)
        
        for i in range(num_users):
            # 生成唯一用户名
            while True:
                username = random_username()
                if username not in used_usernames:
                    used_usernames.add(username)
                    break
            
            nickname = username
            phone = random_phone()
            email = random_email(username)
            create_time = random_create_time(date_str)
            
            # 随机决定是否开通会员 (60%概率)
            is_vip = 0
            vip_expire_time = 'NULL'
            
            if random.random() < 0.6:
                is_vip = 1
                # 随机选择套餐
                package = random.choice(VIP_PACKAGES)
                package_type = package[0]
                amount = package[1]
                vip_days = package[2]
                
                # 计算过期时间
                expire_date = current_date + timedelta(days=vip_days)
                vip_expire_time = f"'{expire_date.strftime('%Y-%m-%d')} 23:59:59'"
                
                # 生成订单
                order_no = generate_order_no(user_id, order_id)
                pay_time = f"'{create_time}'"
                
                orders.append(f"""
INSERT INTO t_order (order_no, user_id, package_type, amount, pay_status, pay_time, expire_time, create_time)
VALUES ('{order_no}', {user_id}, '{package_type}', {amount}, 1, {pay_time}, {vip_expire_time}, '{create_time}');""")
                
                order_id += 1
            
            # 生成用户INSERT语句
            users.append(f"""
INSERT INTO t_user (username, password, nickname, phone, email, is_vip, vip_expire_time, status, create_time)
VALUES ('{username}', '{PASSWORD_HASH}', '{nickname}', '{phone}', '{email}', {is_vip}, {vip_expire_time}, 1, '{create_time}');""")
            
            user_id += 1
        
        current_date += timedelta(days=1)
    
    # 生成SQL文件
    sql_content = f"""-- ==============================================
-- 乐语匣子音乐平台 - 模拟用户数据
-- 生成日期范围: 2026-05-15 至 2026-05-21
-- 总用户数: {user_id - 1}
-- 总订单数: {order_id - 1}
-- 生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
-- ==============================================

USE leyu_music;

-- ==============================================
-- 插入用户数据
-- 密码统一为: 123456 (BCrypt加密)
-- ==============================================
"""
    
    for user in users:
        sql_content += user
    
    sql_content += """
-- ==============================================
-- 插入会员订单数据
-- ==============================================
"""
    
    for order in orders:
        sql_content += order
    
    sql_content += f"""
-- ==============================================
-- 数据生成完成
-- ==============================================

SELECT '用户数据导入完成' AS message;
SELECT CONCAT('共插入 ', COUNT(*), ' 条用户记录') AS user_count FROM t_user WHERE create_time >= '2026-05-15' AND create_time < '2026-05-22';
SELECT CONCAT('共插入 ', COUNT(*), ' 条订单记录') AS order_count FROM t_order WHERE create_time >= '2026-05-15' AND create_time < '2026-05-22';
"""
    
    # 写入文件
    with open('/workspace/docs/mock_users_202605.sql', 'w', encoding='utf-8') as f:
        f.write(sql_content)
    
    print(f"SQL文件已生成: /workspace/docs/mock_users_202605.sql")
    print(f"总用户数: {user_id - 1}")
    print(f"总订单数: {order_id - 1}")

if __name__ == '__main__':
    main()
