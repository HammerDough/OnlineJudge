<!-- components/SubmissionContent.vue -->
<template>
  <div class="submission-container">
    <el-table
      :data="recordList"
      border
      stripe
      v-loading="loading"
      @row-click="handleRowClick"
      style="width: 100%;"
    >
      <el-table-column label="提交时间" prop="submitTime" width="180" />
      <el-table-column label="编程语言" prop="language" width="100" />
      <el-table-column label="运行时间(ms)" prop="runTime" width="120" align="center" />
      <el-table-column label="运行内存(KB)" prop="runMemory" width="120" align="center" />
      <el-table-column label="通过率" prop="passRate" width="100" align="center" />
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button type="primary" link @click.stop="handleRowClick(row)">
            查看详情
          </el-button>
        </template>
      </el-table-column>
      <template #empty>
        <div class="empty-text">暂无提交记录</div>
      </template>
    </el-table>

    <el-dialog
      v-model="detailDialogVisible"
      title="提交记录详情"
      width="70%"
      destroy-on-close
    >
      <div class="detail-info">
        <el-row :gutter="20">
          <el-col :span="8">
            <p><strong>编程语言：</strong>{{ detailInfo.language }}</p>
          </el-col>
          <el-col :span="8">
            <p><strong>运行时间：</strong>{{ detailInfo.runTime }} ms</p>
          </el-col>
          <el-col :span="8">
            <p><strong>运行内存：</strong>{{ detailInfo.runMemory }} KB</p>
          </el-col>
        </el-row>
        <el-row style="margin-top: 8px;">
          <el-col :span="12">
            <p><strong>提交时间：</strong>{{ detailInfo.submitTime }}</p>
          </el-col>
          <el-col :span="12">
            <p><strong>通过率：</strong>{{ detailInfo.passRate }}</p>
          </el-col>
        </el-row>
      </div>
      <div class="code-box">
        <p style="margin-bottom: 8px;"><strong>提交代码：</strong></p>
        <el-input
          v-model="detailInfo.submitCode"
          type="textarea"
          :rows="18"
          readonly
          resize="none"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSubmitRecordList, getSubmitRecordDetail } from '@/api/submit'

// 从路由获取参数 /description/1 → route.params.id
const route = useRoute()
const problemId = ref(null)

const loading = ref(false)
const recordList = ref([])
const detailDialogVisible = ref(false)
const detailInfo = ref({})

// 加载提交记录
const getRecordList = async () => {
  problemId.value = route.params.id
  if (!problemId.value) {
    ElMessage.warning('未获取到题目ID')
    return
  }

  loading.value = true
  try {
    const res = await getSubmitRecordList({ problemId: problemId.value })
    if (res.code === 1) {
      recordList.value = res.data || []
    } else {
      ElMessage.warning(res.msg || '获取提交记录失败')
    }
  } catch (err) {
    ElMessage.error('网络异常，获取记录失败')
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 查看单条详情
const handleRowClick = async (row) => {
  if (!problemId.value) {
    ElMessage.warning('题目ID异常')
    return
  }
  try {
    const res = await getSubmitRecordDetail({
      id: row.id,
      problemId: problemId.value
    })
    if (res.code === 1) {
      detailInfo.value = res.data
      detailDialogVisible.value = true
    } else {
      ElMessage.warning(res.msg || '获取详情失败')
    }
  } catch (err) {
    ElMessage.error('网络异常')
    console.error(err)
  }
}

// 组件挂载直接请求
onMounted(() => {
  getRecordList()
})
</script>

<style scoped>
.submission-container {
  padding: 16px;
  height: 100%;
  box-sizing: border-box;
}
.empty-text {
  color: #999;
  line-height: 60px;
}
.detail-info {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 16px;
}
.code-box {
  width: 100%;
}
</style>