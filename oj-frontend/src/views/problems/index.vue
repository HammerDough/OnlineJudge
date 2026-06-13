<script setup>
import { ref ,onMounted} from 'vue'
import HeatCalendar from '@/components/HeatCalendar.vue'
import DataCalendar from '@/components/DataCalendar.vue'
import { getProblemList } from '@/api/problem'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

// 筛选相关
const keyword = ref('')
const searchField = ref('')
const difficulty = ref('')
const options = [
  {value: '1',label: '简单',},
  {value: '2',label: '中等',},
  {value: '3',label: '困难',}
]

// 表格数据示例
const total = ref(0)
const tableData = ref([])


// 分页相关
const currentPage = ref(1)
const pageSize = ref(15)


const loadProblemList = async()=>{
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value
  }

  // 搜索字段 + 关键词：必须两个都不为空才传递
  if (searchField.value && keyword.value.trim()) {
    params.searchField = searchField.value
    params.keyword = keyword.value.trim()
  }

  // 难度：不为空才传递
  if (difficulty.value) {
    params.difficulty = difficulty.value
  }

  try {
    const { data } = await getProblemList(params)
    tableData.value = data.list
    total.value = data.total
  } catch (error) {
    ElMessage.error('加载题目列表失败')
    console.error(error)
  }
}


// 搜索按钮
const handleSearch = ()=>{
  currentPage.value = 1
  loadProblemList()
}

// 清空筛选
const clearFilters = ()=>{
  keyword.value = ''
  searchField.value = ''
  difficulty.value = ''
  currentPage.value = 1
  loadProblemList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadProblemList()
}

onMounted(()=>{
  loadProblemList()
})


const goToDetail = (row)=>{
  router.push(`/description/${row.id}`)
}

</script>

<template>
  <!-- 外层容器处理左右留白 -->
  <div class="page-wrapper">
    <div class="problems-page">
      <div class="problems-main">

        <div class="card">
          <HeatCalendar />
        </div>

        <div class="card" id="problemlist-area">
          <div class="filter-area">
            <el-form :inline="true" class="filter-form">
                <el-form-item>
                  <el-input v-model="keyword" placeholder="请输入关键词" style="width: 280px">
                    <template #prepend>
                      <el-select v-model="searchField" style="width: 75px" placeholder="字段">
                        <el-option label="编号" value="id" />
                        <el-option label="名称" value="title" />
                      </el-select>
                    </template>
                    <template #append>
                      <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
                    </template>
                  </el-input>
                </el-form-item>
                <el-form-item>
                  <el-select
                    v-model="difficulty"
                    clearable
                    placeholder="难度"
                    style="width: 80px"
                    @change="loadProblemList"
                  >
                    <el-option
                      v-for="item in options"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    />
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="clearFilters">清空筛选</el-button>
                </el-form-item>
              </el-form>
          </div>

          <div class="show-area">
            <el-table :data="tableData" 
              stripe 
              style="width: 100%;cursor:pointer"
              @row-click="goToDetail"
              row-class-name="click-row"
              >
              <el-table-column prop="id" label="编号" width="60" />
              <el-table-column prop="title" label="名称" />
              <el-table-column prop="difficulty" label="难度" width="80" />
              <el-table-column prop="passRate" label="通过率" width="80" />
            </el-table>
          </div>


          <div class="pagination-area">
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :size="'large'"
              :disabled="false"
              :background="false"
              layout="prev, pager, next"
              :total="total"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>

      </div>


      <div class="problems-aside">
        <div class="card" id="card-area">
          <DataCalendar />
        </div>
        <div class="card" id="rank-area">排行榜区域</div>
      </div>

    </div>
  </div>
</template>



<style scoped>

.page-wrapper {
  margin: 0 15vw;           
  transition: margin 0.2s;
}

.problems-page {
  display: flex;
  flex-wrap: wrap;          /* 允许换行，窄屏时侧边栏下移 */
  gap: 20px;                /* 主内容区与侧边栏之间的间距，以及换行后的行间距 */
  padding: 20px 0;
  align-items: stretch;     /* 让两侧卡片高度自然对齐 */
}

/* 主内容区域：占剩余宽度的 3/4，同时限制最大宽度 */
.problems-main {
  flex: 3;                  /* 3/4 */
  min-width: 240px;         /* 保证基本可用宽度，同时影响换行阈值 */
  max-width: 1280px;        /* 限制最大宽度 */
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 侧边栏区域：占剩余宽度的 1/4，限制最大宽度 */
.problems-aside {
  flex: 1;                  /* 1/4 */
  min-width: 220px;
  max-width: 420px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 卡片通用样式 */
.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: box-shadow 0.2s;
}


#problemlist-area {
  min-height: 400px;
}

.filter-area {
  display: flex;
  margin-top: 20px;
  justify-content: center;
}

.filter-form{
  display: inline-flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
}

.pagination-area {
  display: flex;
  margin-top: 20px;
  justify-content: center;
}

.pagination-area .el-pagination {
  display: inline-flex;
}


#card-area {
  background-color: lightyellow;
}

#rank-area {
  min-height: 300px;
  background-color: lightgray;
}

/* 响应式断点：当屏幕宽度较窄时，侧边栏换行到主内容区下方 */
@media (max-width: 900px) {
  .page-wrapper {
    margin: 0 5vw;          /* 窄屏时缩小左右留白，节省空间 */
  }
  .problems-main,
  .problems-aside {
    flex: 1 1 100%;         /* 两者都占满整行，侧边栏自然换行到底部 */
    max-width: none;        /* 解除最大宽度限制，充分利用空间 */
  }
}

/* 针对超宽屏，保持最大宽度限制，但居中效果不变 */
@media (min-width: 1600px) {
  .problems-main {
    max-width: 1280px;      /* 保持不超过 1280px */
  }
  .problems-aside {
    max-width: 420px;
  }
}
</style>