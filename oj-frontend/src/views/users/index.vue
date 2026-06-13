<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElDialog, ElForm, ElFormItem, ElInput, ElButton, ElUpload } from 'element-plus'
import { getUserInfoById, updateUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const token = ref('')

// 目标用户ID、登录用户ID
const targetUserId = route.params.userId
const loginUserId = computed(() => userStore.userId)
const isSelf = computed(() => Number(targetUserId) === loginUserId.value)

// 用户信息
const userInfo = ref(null)
// 编辑弹窗
const editDialogVisible = ref(false)
// 编辑表单
const editForm = ref({
  nickname: '',
  avatar: ''
})

// ========== 时间格式化工具 ==========
const formatTime = (timeStr) => {
  if (!timeStr) return '未知时间'
  const date = new Date(timeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}年${month}月${day}日 ${hours}:${minutes}`
}

// 加载用户资料
const loadUserInfo = async () => {
  const res = await getUserInfoById(targetUserId)
  if (res.code === 1) {
    userInfo.value = res.data
    editForm.value.nickname = res.data.nickname || ''
    editForm.value.avatar = res.data.avatar || ''
  } else {
    ElMessage.error(res.msg)
  }
}

// 打开编辑弹窗
const openEditDialog = () => {
  editDialogVisible.value = true
}

// 提交修改
const submitEdit = async () => {
  if (!editForm.value.nickname) {
    ElMessage.warning('昵称不能为空')
    return
  }
  const res = await updateUserInfo(editForm.value)
  if (res.code === 1) {
    ElMessage.success('资料修改成功')
    editDialogVisible.value = false
    await loadUserInfo()
    // 同步全局状态
    userStore.setUserInfo(res.data)
  } else {
    ElMessage.error(res.msg)
  }
}

// ========== 头像上传处理 ==========
const handleAvatarSuccess = (res) =>{
    editForm.value.avatar = res.data
    if(userInfo.value){
        userInfo.value.avatar = res.data
    }
}

const beforeAvatarUpload =  (file) => {
  if(file.type !== 'image/jpeg' && file.type !== 'image/png'){
    ElMessage.error('只支持上传图片')
    return false
  }else if(file.size/1024/1024>10){
    ElMessage.error('只能上传10M以内的图片')
    return false
  }

  return true
}

onMounted(() => {
  loadUserInfo()
  token.value = localStorage.getItem('token') || ''
})
</script>

<template>
  <div class="page-wrapper">
    <!-- 主卡片 -->
    <div class="user-card">
      <!-- 头部渐变区域 -->
      <div class="card-header">
        <div class="avatar-box">
          <img
            :src="userInfo?.avatar || ''"
            alt="头像"
            class="big-avatar"
          />
        </div>
        <div class="user-name">
          {{ userInfo?.nickname || userInfo?.username }}
        </div>
        <div class="user-account">
          账号：{{ userInfo?.username }}
        </div>

        <!-- 仅本人显示编辑按钮 -->
        <el-button
          v-if="isSelf"
          type="warning"
          :icon="Edit"
          class="edit-btn"
          @click="openEditDialog"
        >
          编辑资料
        </el-button>
      </div>

      <!-- 统计数据区域 -->
      <div class="stat-row">
        <div class="stat-item">
          <div class="stat-num">{{ userInfo?.finishProblemNum || 0 }}</div>
          <div class="stat-label">已完成题目</div>
        </div>
        <div class="stat-item">
          <div class="stat-num">{{ userInfo?.totalMinute || 0 }}</div>
          <div class="stat-label">总做题时长(分钟)</div>
        </div>
      </div>

      <!-- 底部信息 -->
      <div class="card-footer">
        <p class="create-time">
          入驻时间：{{ formatTime(userInfo?.createTime) }}
        </p>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      title="修改个人资料"
      width="480px"
      destroy-on-close
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
        </el-form-item>

        <!-- 头像上传组件 -->
        <el-form-item label="头像">
          <el-upload
            action="/api/file/upload/avatar"
            list-type="picture-card"
            :show-file-list="false"
            :headers="{'token':token}"
            :before-upload="beforeAvatarUpload"
            :on-success="handleAvatarSuccess"
            class="avatar-upload"
        >
            <img v-if="editForm.avatar" :src="editForm.avatar" class="upload-img">
            <el-icon v-else><Plus /></el-icon>
        </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确认保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 页面居中容器 */
.page-wrapper {
  margin: 0 15vw;
  transition: margin 0.2s;
  padding-top: 40px;
  padding-bottom: 40px;
}

/* 主卡片 */
.user-card {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(255, 158, 89, 0.15);
  overflow: hidden;
  transition: all 0.3s ease;
}
.user-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(255, 158, 89, 0.25);
}

/* 卡片头部渐变背景 */
.card-header {
  position: relative;
  padding: 70px 20px 35px;
  text-align: center;
  background: linear-gradient(135deg, #ff9e59 0%, #ffb978 100%);
  color: #fff;
}

.avatar-box {
  margin-bottom: 20px;
}

.big-avatar {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  border: 5px solid #ffffff;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  object-fit: cover; 
  display: inline-block;
}

.user-name {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
}
.user-account {
  font-size: 15px;
  opacity: 0.92;
}

.edit-btn {
  position: absolute;
  right: 35px;
  top: 35px;
  border-radius: 20px;
}

/* 统计行 */
.stat-row {
  display: flex;
  justify-content: space-around;
  padding: 45px 20px;
  background-color: #fefaf5;
}
.stat-item {
  text-align: center;
}
.stat-num {
  font-size: 40px;
  font-weight: bold;
  color: #ff9e59;
  line-height: 1.2;
}
.stat-label {
  font-size: 15px;
  color: #666;
  margin-top: 8px;
}

/* 底部 */
.card-footer {
  padding: 25px 35px;
  border-top: 1px solid #f0f0f0;
}
.create-time {
  color: #999;
  font-size: 14px;
  margin: 0;
}

/* ========== 上传组件样式 ========== */
/* 头像上传整体容器 */
.avatar-upload {
  --el-upload-card-width: 100px;
  --el-upload-card-height: 100px;
}
/* 上传卡片整体样式 + 圆形 */
.avatar-upload :deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
/* 内部图片 等比例不变形 */
.upload-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
/* 图标大小 */
.avatar-upload :deep(.el-icon) {
  font-size: 28px;
  color: #999;
}
</style>