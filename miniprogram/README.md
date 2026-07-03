# 乐语匣子 - 微信小程序

## 项目说明

乐语匣子音乐平台微信小程序端。

## 开发指南

### 1. 导入项目

1. 下载并安装 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
2. 打开微信开发者工具
3. 选择「导入项目」
4. 选择 `miniprogram` 目录
5. 填写 AppID（测试可使用测试号）

### 2. 配置后端地址

编辑 `utils/request.js`，修改后端 API 地址:

```javascript
const BASE_URL = 'http://localhost:8080/api';
```

> 注意: 在微信开发者工具中，需要勾选「不校验合法域名」选项

### 3. 运行项目

点击微信开发者工具的「编译」按钮即可预览小程序。

## 项目结构

```
miniprogram/
├── pages/           # 页面
│   ├── index/       # 首页
│   ├── player/      # 播放器
│   ├── search/      # 搜索
│   ├── playlist/    # 歌单
│   ├── vip/         # VIP
│   ├── order/       # 订单
│   ├── community/   # 社区
│   ├── profile/     # 我的
│   └── login/       # 登录
├── components/      # 组件
├── static/          # 静态资源
│   └── images/      # 图片资源
├── utils/           # 工具函数
├── app.js           # 小程序入口
├── app.json         # 小程序配置
└── app.wxss         # 全局样式
```

## 已修复的问题

- TabBar 图标缺失问题已修复
