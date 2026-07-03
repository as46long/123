# 微信一键登录功能使用说明

## 功能说明

用户在小程序登录页面可以点击"微信一键登录"按钮，使用微信开发者工具中的微信账号进行登录。

## 使用步骤

### 1. 配置微信小程序信息

在 `leyu-admin-backend/src/main/resources/application.yml` 中配置：

```yaml
wechat:
  appid: your_wechat_appid_here
  secret: your_wechat_secret_here
```

**重要：** 
- 在开发测试阶段，可以使用测试号或不填（会使用模拟登录）
- 生产环境必须填写真实的 AppID 和 AppSecret

### 2. 开发测试阶段配置

由于开发环境可能没有真实的微信 AppID，我们提供了两种测试模式：

#### 方式一：使用测试 AppID（推荐）
在微信公众平台申请测试号，获取测试用的 AppID 和 AppSecret

#### 方式二：开发模式（仅用于测试）
如果不配置真实的 AppID，系统会自动使用模拟登录：

1. 用户的 `wx_openid` 将被设置为微信 `code`
2. 新用户会自动创建，用户名格式：`wx_时间戳`
3. 默认昵称：`微信用户`

### 3. 微信开发者工具设置

1. 打开微信开发者工具
2. 在右侧"详情"中勾选：
   - ☑️ 不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书
3. 使用测试号或真实的 AppID

### 4. 测试流程

1. 打开小程序
2. 进入登录页面
3. 点击"微信一键登录"按钮
4. 系统会自动：
   - 调用 `wx.login()` 获取临时登录凭证 `code`
   - 将 `code` 发送到后端
   - 后端调用微信 API 换取 `openid`
   - 根据 `openid` 查找或创建用户
   - 返回 JWT token
   - 自动保存登录状态并跳转

## 登录流程

### 前端流程
```javascript
// 1. 获取微信登录码
const { code } = await wx.login()

// 2. 调用后端登录接口
const res = await api.wxLogin({ code })

// 3. 保存 token 和用户信息
wx.setStorageSync('token', res.data.token)
app.globalData.token = res.data.token
app.globalData.userInfo = res.data.user

// 4. 跳转回上一页
wx.navigateBack()
```

### 后端流程
```java
// 1. 接收前端传来的 code
String code = request.getParameter("code");

// 2. 调用微信 API 获取 openid
WechatLoginResult result = wechatUtil.code2Session(appid, secret, code);
String openid = result.getOpenid();

// 3. 根据 openid 查找用户
User user = userMapper.selectOne(wrapper.eq(User::getWxOpenid, openid));

// 4. 如果用户不存在，创建新用户
if (user == null) {
    user = new User();
    user.setWxOpenid(openid);
    user.setUsername("wx_" + System.currentTimeMillis());
    // ... 设置其他信息
    userMapper.insert(user);
}

// 5. 生成 JWT token 并返回
String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
```

## 注意事项

1. **开发测试：**
   - 使用微信开发者工具时，需要勾选"不校验合法域名"
   - 可以使用测试号进行开发测试

2. **生产环境：**
   - 必须配置真实的微信 AppID 和 AppSecret
   - 需要在微信公众平台配置服务器域名白名单
   - 需要在小程序后台设置业务域名

3. **用户体验：**
   - 微信登录失败时会显示具体错误信息
   - 加载过程中有加载提示
   - 登录成功后会有成功提示并自动跳转

4. **安全性：**
   - 微信 code 只能使用一次，有效期5分钟
   - 后端会对每个用户创建唯一账号
   - 使用 JWT token 进行身份验证

## 常见问题

### Q: 点击微信登录没有反应？
A: 检查微信开发者工具是否勾选了"不校验合法域名"选项

### Q: 显示"登录失败，请重试"？
A: 可能是网络问题或后端服务未启动，检查后端日志

### Q: 显示"获取微信登录码失败"？
A: 确认是否在微信开发者工具中运行，且使用的是有效的 AppID

### Q: 如何查看登录后的用户信息？
A: 登录成功后可以在"我的"页面查看用户信息

## 文件清单

### 小程序端
- `pages/login/login.wxml` - 添加微信登录按钮
- `pages/login/login.wxss` - 添加微信登录按钮样式
- `pages/login/login.js` - 实现微信登录逻辑
- `utils/api.js` - 添加微信登录 API 接口

### 后端
- `controller/UserController.java` - 微信登录接口
- `service/UserService.java` - 微信登录服务接口
- `service/impl/UserServiceImpl.java` - 微信登录服务实现
- `utils/WechatUtil.java` - 微信 API 调用工具类
- `resources/application.yml` - 微信配置
