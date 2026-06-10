<script setup>
import {ref,shallowRef,onMounted,onUnmounted} from 'vue'
import * as monaco from 'monaco-editor'


//===============description-panel================//
import DescriptionContent from '@/components/DescriptionContent.vue'
import SolutionContent from '@/components/SolutionContent.vue'
import SubmissionContent from '@/components/SubmissionContent.vue'

// 当前激活的菜单项
const activeTab = ref('description')

// 组件映射
const componentMap = {
  description: DescriptionContent,
  solution: SolutionContent,
  submission: SubmissionContent
}

// 当前显示的组件
const currentComponent = shallowRef(componentMap[activeTab.value])

// 菜单切换事件
const handleTabSelect = (index) => {
  activeTab.value = index
  currentComponent.value = componentMap[index]
}


//===============edit-panel================//
const editorContainer = ref(null);
let editor = null;

const lang = ref('cpp')

const codeTemplates = {
  cpp: '#include <iostream>\nusing namespace std;\n\nint main() {\n\n    return 0;\n}',
  c: '#include <stdio.h>\nint main() {\n    return 0;\n}',
  java: 'public class Main {\n    public static void main(String[] args) {\n    }\n}',
  python: '# Write your code here\nif __name__ == "__main__":\n    pass',
  javascript: '// Write your code here\nconsole.log("Hello")',
  go: 'package main\nimport "fmt"\nfunc main() {\n\tfmt.Println("")\n}'
}

onMounted(() => {
  editor = monaco.editor.create(editorContainer.value, {
    value: codeTemplates.cpp,
    language: 'cpp', // 支持：python/java/javascript/go 等
    theme: 'vs', // vs/vs-dark/hc-black
    automaticLayout: true,
    minimap: { enabled: false }, // OJ 通常关闭小地图
    fontSize: 14,
    lineNumbers: 'on',
    roundedSelection: true,
    scrollBeyondLastLine: false,
  });
});

const changeLang = (val) => {
  const model = editor.getModel()
  monaco.editor.setModelLanguage(model, val)
  editor.setValue(codeTemplates[val])
}


// 运行代码
const runCode = () => {
  const code = editor.getValue()
  console.log('运行代码：', code)
  console.log('语言：', lang.value)
  // 在这里调用后端运行接口
}

// 提交代码
const submitCode = () => {
  const code = editor.getValue()
  console.log('提交代码：', code)
  console.log('语言：', lang.value)
  // 在这里调用后端提交接口
}


onUnmounted(() => {
  editor?.dispose();
});


</script>

<template>
    <div class="page-wrapper">
        <el-splitter>
            <el-splitter-panel size="35%" :collapsible="true">
                <div class="description-panel">
                    <el-container>
                        <el-header class="description-head">
                            <span>
                                <img src="@/assets/logo.png" alt="Logo" class="logo-img" width="30px">
                            </span>
                            <span class="title">1.两数之和</span>
                        </el-header>
                        <el-container>
                            <el-aside class="description-aside" width="100px">
                                <el-menu
                                    :default-active="activeTab"
                                    class="aside-menu"
                                    background-color="transparent"
                                    text-color="#3a3a3a"
                                    active-text-color="#ff9e59"
                                    @select="handleTabSelect"
                                >
                                    <el-menu-item index="description">
                                    <div class="menu-item-content">
                                        <el-icon><Notebook /></el-icon>
                                        <span class="menu-text">题目描述</span>
                                    </div>
                                    </el-menu-item>
                                    <el-menu-item index="solution">
                                    <div class="menu-item-content">
                                        <el-icon><Guide /></el-icon>
                                        <span class="menu-text">题解</span>
                                    </div>
                                    </el-menu-item>
                                    <el-menu-item index="submission">
                                    <div class="menu-item-content">
                                        <el-icon><DataLine /></el-icon>
                                        <span class="menu-text">提交记录</span>
                                    </div>
                                    </el-menu-item>
                                </el-menu>
                            </el-aside>
                            <el-main class="description-main">
                                <component :is="currentComponent" />
                            </el-main>
                        </el-container>
                    </el-container>
                </div>
            </el-splitter-panel>
            <el-splitter-panel :min="200">
                <el-splitter layout="vertical">
                    <el-splitter-panel size="80%">
                        <div class="edit-panel" style="height: 100%;">
                            <el-container style="height: 100%;display:flex;flex-direction:column;">
                            <!-- 顶部：语言切换 + 运行 + 提交 -->
                            <el-header class="edit-header">
                                <div class="left">
                                    <span class="header-title">代码</span>
                                    <el-select v-model="lang" size="small" @change="changeLang" style="width:100px;">
                                    <el-option label="C++" value="cpp"></el-option>
                                    <el-option label="C" value="c"></el-option>
                                    <el-option label="Java" value="java"></el-option>
                                    <el-option label="Python" value="python"></el-option>
                                    <el-option label="JavaScript" value="javascript"></el-option>
                                    <el-option label="Go" value="go"></el-option>
                                    </el-select>
                                </div>

                                <div class="center">
                                    <el-button color="#40c35d" :dark="isDark" plain @click="runCode"><el-icon><VideoPlay /></el-icon>运行</el-button>
                                    <el-button color="#ff9e59" :dark="isDark" plain @click="submitCode"><el-icon><UploadFilled /></el-icon>提交</el-button>
                                </div>

                                <div class="right">
                                    <el-button color="#ff9e59" :dark="isDark" text><el-icon size="larger"><Setting /></el-icon></el-button>
                                    <el-button color="#ff9e59" :dark="isDark" text><el-icon size="larger"><Refresh /></el-icon></el-button>
                                </div>
                                
                                
                            </el-header>

                            <!-- 编辑器区域：自动填满 -->
                            <el-main class="edit-main">
                                <div ref="editorContainer" style="width:100%;height:100%;"></div>
                            </el-main>
                            </el-container>
                        </div>
                    </el-splitter-panel>
                    <el-splitter-panel :min="32">
                        <div class="console-panel">
                            <el-container>
                                <el-header class="console-header">控制台</el-header>
                                <el-main class="console-main">内容显示</el-main>
                            </el-container>
                        </div>
                    </el-splitter-panel>
                </el-splitter>
            </el-splitter-panel>
    </el-splitter>
    </div>
</template>

<style scoped>
.page-wrapper {
 width: 100%;
 height: 100vh;
}



.description-panel{
    height: 100%;
}

.description-head{
    display: flex;
    white-space: nowrap;
    align-items: center;
    gap: 20px;
    background-color: #fafafa;
    border-bottom: 1px solid #eee;
}

.el-container{
    height: 100%;
}
.description-aside {
  width: 60px;              
  border-right: 1px solid #eee;
}

.aside-menu {
  height: 100%;
  border-right: none;
}

.aside-menu .el-menu-item {
  height: 70px;
  line-height: 1.4;
  padding: 0 !important;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-item-content {
  display: flex;
  flex-direction: column;    /* 垂直排列 */
  align-items: center;
  gap: 8px;
}

.menu-item-content .el-icon {
  font-size: 20px;
  margin-right: 0;       
}

.menu-text {
  font-size: 10px;
  color: inherit;
}

/* 编辑器样式部分 */
.edit-header{
    display: flex;
    align-items: center;
    justify-content: space-between;
    background:#fafafa;
}
.left{
    display: flex;
    gap: 20px;
}

.right{
    display: flex;
    .el-button{
        width: 20px;
        margin: 0 !important;
    }
}


.center{
    display: flex;
    left: 50%;
    transform: translate(-60%);
}

.edit-main{
    flex: 1;
    padding: 0;
    position: relative;
    overflow: hidden;
}

.edit-header .el-select{
    margin-right: 150px;
}

.header-title{
    white-space: nowrap;
}


/* 控制台样式部分 */

.console-panel{
    height: 100%;
}

.console-header{
    height: 32px;
    background-color: #fafafa;
}

.console-main{
    height: 100%;
    overflow: hidden;
}
</style>
