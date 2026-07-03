# 歌词搜索API文档

## 功能说明
歌词搜索功能已升级为在线查询模式，可以从网易云音乐API搜索歌词并保存到本地数据库。

## API接口

### 1. 搜索歌词
**接口地址**: `GET /api/song/lyrics/search`

**参数**:
- `keyword` (必填): 搜索关键词（歌曲名或歌手名）

**返回示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "songId": "123456",
      "title": "歌曲名称",
      "artist": "歌手名",
      "album": "专辑名",
      "lyrics": null
    }
  ]
}
```

### 2. 获取并保存歌词
**接口地址**: `GET /api/song/lyrics/fetch/{songId}`

**参数**:
- `songId` (路径参数): 系统中的歌曲ID
- `apiSongId` (查询参数，可选): API中的歌曲ID，如果提供则会从在线API获取歌词并保存

**使用流程**:
1. 先调用搜索歌词接口，获取`apiSongId`
2. 调用此接口并传入`apiSongId`参数，系统会自动获取歌词并保存到数据库

**返回示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "[00:00.00]歌词内容..."
}
```

### 3. 获取本地歌词
**接口地址**: `GET /api/song/lyrics/{id}`

**参数**:
- `id` (路径参数): 歌曲ID

**说明**: 获取已保存在数据库中的歌词

### 4. 更新歌词
**接口地址**: `PUT /api/song/lyrics/{id}`

**参数**:
- `id` (路径参数): 歌曲ID
- 请求体: 歌词文本

**说明**: 手动更新歌词内容

## 使用示例

### 前端集成示例
```javascript
// 1. 搜索歌词
const searchResults = await fetch('/api/song/lyrics/search?keyword=稻香');

// 2. 选择一条结果，获取并保存歌词
const lyrics = await fetch('/api/song/lyrics/fetch/123?apiSongId=456789');

// 3. 显示歌词
console.log(lyrics.data);
```

## 技术实现
- 使用网易云音乐公开API进行歌词搜索
- 歌词以LRC格式存储在数据库的`lyrics`字段中
- 支持自动保存到数据库，避免重复查询

## 注意事项
1. 由于使用第三方API，请遵守相关的使用条款
2. 建议在生产环境中添加缓存机制，减少API调用频率
3. 歌词内容版权归原作者所有
