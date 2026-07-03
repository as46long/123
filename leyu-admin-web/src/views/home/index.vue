<template>
  <div class="home-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>首页管理</span>
        </div>
      </template>
      
      <div class="splash-section">
        <h3>启动画面设置</h3>
        <p class="description">设置小程序启动时显示的全屏图片，用户点击图片后进入首页</p>
        
        <div class="splash-preview">
          <div class="preview-label">当前启动画面：</div>
          <div class="preview-container">
            <el-image 
              v-if="splashImage" 
              :src="splashImage" 
              fit="cover"
              class="splash-image"
            />
            <div v-else class="no-image">暂无设置</div>
          </div>
        </div>
        
        <div class="upload-action">
          <el-upload
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
            accept=".jpg,.jpeg,.png,.gif,.webp"
          >
            <el-button type="primary" :loading="uploading">
              {{ splashImage ? '更换图片' : '上传图片' }}
            </el-button>
          </el-upload>
          <span class="upload-tip">支持 jpg、png、gif、webp 格式，建议尺寸 750 x 1500</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSplashImage, uploadSplashImage } from '@/api/system'
import { ElMessage } from 'element-plus'

const splashImage = ref('')
const uploading = ref(false)

const loadData = async () => {
  try {
    const res = await getSplashImage()
    splashImage.value = res.data || ''
  } catch (e) {
    console.error('加载启动画面失败', e)
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB')
    return false
  }
  return true
}

const handleUpload = async (options) => {
  uploading.value = true
  try {
    const res = await uploadSplashImage(options.file)
    splashImage.value = res.data
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.home-management {
  padding: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.splash-section h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
}

.description {
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}

.splash-preview {
  margin-bottom: 20px;
}

.preview-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.preview-container {
  width: 200px;
  height: 400px;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.splash-image {
  width: 100%;
  height: 100%;
}

.no-image {
  color: #c0c4cc;
  font-size: 14px;
}

.upload-action {
  display: flex;
  align-items: center;
  gap: 15px;
}

.upload-tip {
  color: #909399;
  font-size: 12px;
}
</style>
