# 乐语匣子音乐平台

一个基于微信小程序的音乐平台，为用户提供便捷的音乐播放、智能推荐、会员服务和互动社区功能。

## 项目结构

```
leyu-music-platform/
├── leyu-admin-backend/     # Spring Boot 后端
├── leyu-admin-web/         # Vue3 管理后台
├── miniprogram/            # 微信小程序
└── docs/                   # 项目文档
```

## 技术栈

### 后端
- Spring Boot 2.7.x
- MyBatis-Plus
- MySQL 8.0
- Spring Security + JWT
- Knife4j (Swagger)

### 管理后台
- Vue 3
- Element Plus
- Vite
- Pinia

### 小程序
- 微信小程序原生开发
- wx.request 封装

## 快速开始

### 1. 数据库配置

执行 docs/database_init.sql 初始化数据库

### 2. 启动后端

后端运行在 http://localhost:8080
API文档: http://localhost:8080/doc.html

### 3. 启动管理后台

管理后台运行在 http://localhost:3000

### 4. 微信小程序

使用微信开发者工具打开 miniprogram 目录

## 默认账号

### 管理后台
- 用户名: admin
- 密码: admin123

## 主要功能

### 用户端（小程序）
- 音乐播放与控制
- 智能推荐（协同过滤）
- 搜索歌曲
- 收藏歌单
- VIP会员
- 乐语互动社区

### 管理端（Web）
- 用户管理
- 歌曲管理
- 订单管理
- 留言审核
- 数据统计

## 推荐算法

采用基于物品的协同过滤算法（Item-Based CF）

## License

MIT
