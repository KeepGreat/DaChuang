<template>
  <div class="course-content">
    <!-- 中间提纲栏 -->
    <div class="outline-panel">
      <div class="outline-header">
        <h3>课程大纲</h3>
        <el-progress
          :percentage="courseProgress"
          :color="'#d63384'"
          :stroke-width="8"
        />
        <span class="progress-text">学习进度: {{ courseProgress }}%</span>
      </div>

      <div class="chapter-list">
        <div
          v-for="(chapter, index) in courseChapters"
          :key="index"
          class="chapter-item"
          :class="{ active: selectedChapter === index, completed: chapter.completed }"
          @click="selectChapter(index)"
        >
          <div class="chapter-icon">
            <el-icon v-if="chapter.completed"><Check /></el-icon>
            <el-icon v-else><VideoPlay /></el-icon>
          </div>
          <div class="chapter-info">
            <h4>第{{ index + 1 }}章 {{ chapter.title }}</h4>
            <p>{{ chapter.description }}</p>
            <div class="chapter-meta">
              <span>{{ chapter.videos.length }} 个视频</span>
              <span>{{ chapter.duration }}</span>
              <el-tag v-if="chapter.completed" size="small" type="success">已完成</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧视频播放区 -->
    <div class="video-panel">
      <div v-if="selectedChapter !== null" class="video-container">
        <div class="video-wrapper">
          <video
            ref="videoPlayer"
            :src="currentVideo?.url || ''"
            controls
            class="video-player"
            @ended="handleVideoEnd"
          ></video>
          <div class="video-overlay" v-if="!currentVideo">
            <el-icon size="60"><VideoPlay /></el-icon>
            <p>请选择视频开始学习</p>
          </div>
        </div>

        <div class="video-info">
          <h4>{{ courseChapters[selectedChapter].title }}</h4>
          <p>{{ courseChapters[selectedChapter].description }}</p>

          <div class="video-list">
            <h5>视频列表</h5>
            <div
              v-for="(video, vIndex) in courseChapters[selectedChapter].videos"
              :key="vIndex"
              class="video-item"
              :class="{ active: currentVideoIndex === vIndex, watched: video.watched }"
              @click="playVideo(vIndex)"
            >
              <div class="video-item-icon">
                <el-icon v-if="video.watched"><Check /></el-icon>
                <el-icon v-else><VideoPlay /></el-icon>
              </div>
              <div class="video-item-info">
                <span class="video-title">{{ video.title }}</span>
                <span class="video-duration">{{ video.duration }}</span>
              </div>
              <div class="video-item-progress" v-if="video.progress">
                <el-progress
                  :percentage="video.progress"
                  :show-text="false"
                  :stroke-width="2"
                  color="#d63384"
                />
              </div>
            </div>
          </div>

          <!-- 课程笔记 -->
          <div class="notes-section">
            <h5>课程笔记</h5>
            <el-input
              v-model="notes"
              type="textarea"
              :rows="4"
              placeholder="记录学习笔记..."
              @blur="saveNotes"
            />
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-icon size="80"><Document /></el-icon>
        <p>请选择章节开始学习</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import {
  VideoPlay, Check, Document
} from '@element-plus/icons-vue';
import { ElProgress, ElTag, ElInput, ElMessage } from 'element-plus';

const route = useRoute();

// 课程数据
const selectedChapter = ref(null);
const currentVideoIndex = ref(0);
const currentVideo = ref(null);
const videoPlayer = ref(null);
const notes = ref('');

// 模拟课程数据
const courseChapters = ref([
  {
    title: 'Python基础语法',
    description: '学习Python的基础语法、变量和数据类型',
    duration: '2小时30分',
    completed: false,
    videos: [
      {
        title: '1.1 Python环境搭建',
        duration: '15:20',
        url: 'https://example.com/video1.mp4',
        watched: false,
        progress: 0
      },
      {
        title: '1.2 变量与数据类型',
        duration: '25:15',
        url: 'https://example.com/video2.mp4',
        watched: false,
        progress: 0
      },
      {
        title: '1.3 运算符与表达式',
        duration: '20:10',
        url: 'https://example.com/video3.mp4',
        watched: false,
        progress: 0
      }
    ]
  },
  {
    title: '控制流程',
    description: '掌握条件语句和循环语句的使用',
    duration: '3小时',
    completed: false,
    videos: [
      {
        title: '2.1 条件语句',
        duration: '30:20',
        url: 'https://example.com/video4.mp4',
        watched: false,
        progress: 0
      },
      {
        title: '2.2 循环语句',
        duration: '35:15',
        url: 'https://example.com/video5.mp4',
        watched: false,
        progress: 0
      }
    ]
  },
  {
    title: '函数与模块',
    description: '学习函数的定义、调用和模块的使用',
    duration: '2小时45分',
    completed: false,
    videos: [
      {
        title: '3.1 函数定义',
        duration: '25:30',
        url: 'https://example.com/video6.mp4',
        watched: false,
        progress: 0
      },
      {
        title: '3.2 模块导入',
        duration: '20:15',
        url: 'https://example.com/video7.mp4',
        watched: false,
        progress: 0
      }
    ]
  }
]);

// 计算课程进度
const courseProgress = computed(() => {
  let totalVideos = 0;
  let watchedVideos = 0;

  courseChapters.value.forEach(chapter => {
    totalVideos += chapter.videos.length;
    watchedVideos += chapter.videos.filter(v => v.watched).length;
  });

  return totalVideos > 0 ? Math.round((watchedVideos / totalVideos) * 100) : 0;
});

// 选择章节
const selectChapter = (index) => {
  selectedChapter.value = index;
  currentVideoIndex.value = 0;
  playVideo(0);

  // 加载该章节的笔记
  loadNotes();
};

// 播放视频
const playVideo = (index) => {
  if (index >= 0 && index < courseChapters.value[selectedChapter.value].videos.length) {
    currentVideoIndex.value = index;
    currentVideo.value = courseChapters.value[selectedChapter.value].videos[index];

    if (videoPlayer.value && currentVideo.value.url) {
      videoPlayer.value.src = currentVideo.value.url;
      // 记录视频观看开始
      recordVideoProgress();
    }
  }
};

// 视频结束处理
const handleVideoEnd = () => {
  if (currentVideo.value) {
    currentVideo.value.watched = true;
    currentVideo.value.progress = 100;
    ElMessage.success('视频播放完成！');

    // 自动播放下一个视频
    const nextIndex = currentVideoIndex.value + 1;
    if (nextIndex < courseChapters.value[selectedChapter.value].videos.length) {
      setTimeout(() => {
        playVideo(nextIndex);
      }, 1000);
    }
  }
};

// 记录视频进度
const recordVideoProgress = () => {
  if (!videoPlayer.value) return;

  const updateProgress = () => {
    if (videoPlayer.value && currentVideo.value) {
      const progress = Math.round((videoPlayer.value.currentTime / videoPlayer.value.duration) * 100);
      currentVideo.value.progress = progress;
    }
  };

  videoPlayer.value.addEventListener('timeupdate', updateProgress);
};

// 保存笔记
const saveNotes = () => {
  const courseId = route.params.id;
  const chapterIndex = selectedChapter.value;
  const key = `course_notes_${courseId}_${chapterIndex}`;
  localStorage.setItem(key, notes.value);
  ElMessage.success('笔记已保存');
};

// 加载笔记
const loadNotes = () => {
  const courseId = route.params.id;
  const chapterIndex = selectedChapter.value;
  const key = `course_notes_${courseId}_${chapterIndex}`;
  notes.value = localStorage.getItem(key) || '';
};

onMounted(() => {
  // 默认选择第一章
  if (courseChapters.value.length > 0) {
    selectChapter(0);
  }
});
</script>

<style scoped>
.course-content {
  display: flex;
  gap: 20px;
  height: 100%;
  padding: 20px;
}

/* 中间提纲栏 */
.outline-panel {
  flex: 0 0 350px;
  background: rgba(255,255,255,0.8);
  border-radius: 12px;
  padding: 20px;
  overflow-y: auto;
}

.outline-header {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f5dbe7;
}

.outline-header h3 {
  margin: 0 0 15px 0;
  color: #d63384;
  font-size: 18px;
  font-weight: 700;
}

.progress-text {
  display: block;
  margin-top: 8px;
  color: #9b7a88;
  font-size: 13px;
  text-align: center;
}

.chapter-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chapter-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #ffd6e7;
}

.chapter-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(214, 51, 132, 0.1);
  border-color: #ffb6d9;
}

.chapter-item.active {
  background: linear-gradient(135deg, #ffd6e8 0%, #fff0f4 100%);
  border-color: #d63384;
}

.chapter-item.completed {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f7fa 100%);
  border-color: #67c23a;
}

.chapter-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  font-size: 20px;
  background: linear-gradient(135deg, #fff0f6, #ffd6e7);
  color: #d63384;
  flex-shrink: 0;
}

.chapter-item.completed .chapter-icon {
  background: linear-gradient(135deg, #f0f9ff, #e0f7fa);
  color: #67c23a;
}

.chapter-info h4 {
  margin: 0 0 8px 0;
  color: #7b2a53;
  font-size: 15px;
  font-weight: 600;
}

.chapter-info p {
  margin: 0 0 10px 0;
  color: #9b7a88;
  font-size: 13px;
  line-height: 1.4;
}

.chapter-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #9b7a88;
}

/* 右侧视频播放区 */
.video-panel {
  flex: 1;
  background: rgba(255,255,255,0.8);
  border-radius: 12px;
  padding: 20px;
  overflow-y: auto;
}

.video-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  background: #000;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 20px;
}

.video-player {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0.7);
  color: white;
  font-size: 18px;
}

.video-overlay .el-icon {
  margin-bottom: 20px;
  opacity: 0.8;
}

.video-info h4 {
  margin: 0 0 10px 0;
  color: #d63384;
  font-size: 20px;
  font-weight: 700;
}

.video-info p {
  margin: 0 0 20px 0;
  color: #6b3b52;
  font-size: 14px;
  line-height: 1.6;
}

.video-list {
  margin-bottom: 30px;
}

.video-list h5 {
  margin: 0 0 15px 0;
  color: #7b2a53;
  font-size: 16px;
  font-weight: 600;
}

.video-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  margin-bottom: 8px;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #ffd6e7;
}

.video-item:hover {
  background: #fff0f6;
  transform: translateX(4px);
}

.video-item.active {
  background: linear-gradient(135deg, #ffd6e8 0%, #fff0f4 100%);
  border-color: #d63384;
}

.video-item.watched {
  opacity: 0.8;
}

.video-item-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  font-size: 16px;
  color: #d63384;
  flex-shrink: 0;
}

.video-item.watched .video-item-icon {
  color: #67c23a;
}

.video-item-info {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.video-title {
  color: #7b2a53;
  font-size: 14px;
}

.video-duration {
  color: #9b7a88;
  font-size: 12px;
}

.video-item-progress {
  width: 60px;
}

/* 笔记部分 */
.notes-section {
  margin-top: 20px;
}

.notes-section h5 {
  margin: 0 0 10px 0;
  color: #7b2a53;
  font-size: 16px;
  font-weight: 600;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #9b7a88;
}

.empty-state .el-icon {
  margin-bottom: 20px;
  opacity: 0.5;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .outline-panel {
    flex: 0 0 300px;
  }
}

@media (max-width: 768px) {
  .course-content {
    flex-direction: column;
  }

  .outline-panel {
    flex: none;
    width: 100%;
  }
}
</style>