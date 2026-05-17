<template>
  <div class="user-answer-editor">
    <el-card class="filter-card">
      <template #header>
        <div class="card-header">
          <span>筛选条件</span>
        </div>
      </template>

      <el-form inline>
        <el-form-item label="选择练习">
          <el-select
            v-model="selectedPracticeId"
            placeholder="请选择练习"
            style="width: 320px"
            filterable
            clearable
            :loading="practiceLoading"
          >
            <el-option
              v-for="item in practiceOptions"
              :key="item.id"
              :label="`${item.name} (ID: ${item.id})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="answerLoading" @click="handleQueryAnswers">
            查询用户答案
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="hasQueried" class="review-card" v-loading="answerLoading">
      <template #header>
        <div class="card-header stats-row">
          <span>用户答案批改</span>
          <span class="stats-text">
            共 {{ sortedUserAnswers.length }} 条，未批改 {{ ungradedCount }} 条
          </span>
        </div>
      </template>

      <div v-if="userAnswers.length > 0" class="sub-filter-row">
        <el-form inline>
          <el-form-item label="用户ID筛选">
            <el-input
              v-model="filterUserId"
              clearable
              placeholder="支持模糊匹配"
              style="width: 240px"
            />
          </el-form-item>
          <el-form-item label="题型筛选">
            <el-select
              v-model="filterQuestionType"
              clearable
              placeholder="全部题型"
              style="width: 180px"
            >
              <el-option
                v-for="(label, value) in questionTypeMap"
                :key="value"
                :label="label"
                :value="Number(value)"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button text type="primary" @click="resetSubFilters">重置二次筛选</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-empty v-if="sortedUserAnswers.length === 0" description="当前筛选条件下暂无用户答案" />

      <div v-else class="review-body">
        <el-button
          class="nav-btn"
          circle
          :icon="ArrowLeft"
          :disabled="currentIndex === 0"
          @click="goPrev"
        />

        <el-card class="answer-card" shadow="never" v-loading="detailLoading">
          <template #header>
            <div class="answer-header">
              <span>
                第 {{ currentIndex + 1 }} / {{ sortedUserAnswers.length }} 条
                ({{ questionTypeText(currentUserAnswer.questionType) }})
              </span>
              <span class="user-meta">用户: {{ currentUserAnswer.userId || "-" }}</span>
            </div>
          </template>

          <div class="answer-section">
            <div class="section-title">问题内容</div>
            <div class="section-content">{{ currentQuestion?.content || "未查询到问题内容" }}</div>
          </div>

          <div class="answer-section">
            <div class="section-title">参考答案</div>
            <div class="section-content">{{ currentAnswer?.content || "未查询到参考答案" }}</div>
          </div>

          <div class="answer-section">
            <div class="section-title">用户答案</div>
            <div class="section-content">{{ currentUserAnswer.content || "(空)" }}</div>
          </div>

          <el-row :gutter="16" class="grading-row">
            <el-col :span="10">
              <el-form-item label="评分" required>
                <el-input-number
                  v-model="currentDraft.score"
                  :min="0"
                  :max="currentQuestion?.score ?? 0"
                  :step="1"
                  step-strictly
                  :disabled="!currentQuestion"
                  style="width: 100%"
                />
              </el-form-item>
              <div class="score-tip">分值范围: 0 ~ {{ currentQuestion?.score ?? 0 }}</div>
            </el-col>

            <el-col :span="14">
              <el-form-item label="评语">
                <el-input
                  v-model="currentDraft.comment"
                  type="textarea"
                  :rows="4"
                  maxlength="500"
                  show-word-limit
                  placeholder="可选，填写对用户答案的评语"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <div class="action-row">
            <el-button type="primary" :loading="submitting" @click="handleSubmitCurrent">
              完成批改
            </el-button>
          </div>
        </el-card>

        <el-button
          class="nav-btn"
          circle
          :icon="ArrowRight"
          :disabled="currentIndex === sortedUserAnswers.length - 1"
          @click="goNext"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { ElMessage } from "element-plus";
import { ArrowLeft, ArrowRight } from "@element-plus/icons-vue";
import { getPractices } from "@/api/modules/practice/practice";
import { getUserAnswers, judgeAnswerManual } from "@/api/modules/practice/userAnswer";
import { getQuestions } from "@/api/modules/practice/question";
import { getAnswers } from "@/api/modules/practice/answer";

const practiceLoading = ref(false);
const answerLoading = ref(false);
const detailLoading = ref(false);
const submitting = ref(false);

const hasQueried = ref(false);
const selectedPracticeId = ref(null);
const currentIndex = ref(0);

const filterUserId = ref("");
const filterQuestionType = ref(null);

const practiceOptions = ref([]);
const userAnswers = ref([]);

const questionCache = reactive({});
const answerCache = reactive({});
const draftMap = reactive({});

const questionTypeMap = {
  0: "判断题",
  1: "选择题",
  2: "简答题",
  3: "编程题",
};

const filteredUserAnswers = computed(() => {
  const keyword = filterUserId.value.trim();
  return userAnswers.value.filter((item) => {
    const userPass = !keyword || String(item.userId || "").includes(keyword);
    const typePass = filterQuestionType.value === null || item.questionType === filterQuestionType.value;
    return userPass && typePass;
  });
});

const sortedUserAnswers = computed(() => {
  return [...filteredUserAnswers.value].sort((a, b) => {
    const typeA = Number.isFinite(a?.questionType) ? a.questionType : 999;
    const typeB = Number.isFinite(b?.questionType) ? b.questionType : 999;
    if (typeA !== typeB) return typeA - typeB;
    return (a?.id || 0) - (b?.id || 0);
  });
});

const currentUserAnswer = computed(() => {
  return sortedUserAnswers.value[currentIndex.value] || null;
});

const currentQuestion = computed(() => {
  const questionId = currentUserAnswer.value?.questionId;
  if (!questionId) return null;
  return questionCache[questionId] || null;
});

const currentAnswer = computed(() => {
  const questionId = currentUserAnswer.value?.questionId;
  if (!questionId) return null;
  return answerCache[questionId] || null;
});

const currentDraft = computed(() => {
  const userAnswerId = currentUserAnswer.value?.id;
  if (!userAnswerId) {
    return { score: null, comment: "" };
  }
  if (!draftMap[userAnswerId]) {
    const defaultScore =
      currentUserAnswer.value.score === null ||
      currentUserAnswer.value.score === undefined ||
      currentUserAnswer.value.score === -1
        ? null
        : currentUserAnswer.value.score;
    draftMap[userAnswerId] = {
      score: defaultScore,
      comment: currentUserAnswer.value.comment || "",
    };
  }
  return draftMap[userAnswerId];
});

const ungradedCount = computed(() => {
  return sortedUserAnswers.value.filter((item) => {
    return item.score === null || item.score === undefined || item.score === -1;
  }).length;
});

onMounted(() => {
  fetchPracticeOptions();
});

watch(
  currentUserAnswer,
  async (newVal) => {
    if (newVal?.questionId) {
      await fetchQuestionAndAnswer(newVal.questionId);
    }
  },
  { immediate: true }
);

watch([filterUserId, filterQuestionType], () => {
  currentIndex.value = 0;
});

watch(sortedUserAnswers, (list) => {
  if (list.length === 0) {
    currentIndex.value = 0;
    return;
  }
  if (currentIndex.value > list.length - 1) {
    currentIndex.value = list.length - 1;
  }
});

const questionTypeText = (type) => {
  return questionTypeMap[type] || "未知题型";
};

const resetSubFilters = () => {
  filterUserId.value = "";
  filterQuestionType.value = null;
};

const clearCaches = () => {
  Object.keys(questionCache).forEach((key) => delete questionCache[key]);
  Object.keys(answerCache).forEach((key) => delete answerCache[key]);
  Object.keys(draftMap).forEach((key) => delete draftMap[key]);
};

const fetchPracticeOptions = async () => {
  practiceLoading.value = true;
  try {
    const res = await getPractices();
    if (res.code === 200) {
      practiceOptions.value = (res.data || []).map((item) => ({
        id: item.id,
        name: item.name,
      }));
    } else {
      ElMessage.error(res.message || "获取练习列表失败");
    }
  } catch (error) {
    console.error("获取练习列表失败:", error);
    ElMessage.error("获取练习列表失败");
  } finally {
    practiceLoading.value = false;
  }
};

const handleQueryAnswers = async () => {
  if (!selectedPracticeId.value) {
    ElMessage.warning("请先选择一个练习");
    return;
  }

  answerLoading.value = true;
  hasQueried.value = true;
  currentIndex.value = 0;
  userAnswers.value = [];
  resetSubFilters();
  clearCaches();

  try {
    const res = await getUserAnswers({ practiceId: selectedPracticeId.value });
    if (res.code === 200) {
      userAnswers.value = res.data || [];
      const firstQuestionId = sortedUserAnswers.value[0]?.questionId;
      if (firstQuestionId) {
        await fetchQuestionAndAnswer(firstQuestionId);
      }
    } else {
      ElMessage.error(res.message || "获取用户答案失败");
    }
  } catch (error) {
    console.error("获取用户答案失败:", error);
    ElMessage.error("获取用户答案失败");
  } finally {
    answerLoading.value = false;
  }
};

const fetchQuestionAndAnswer = async (questionId) => {
  if (!questionId) return;
  if (questionCache[questionId] && answerCache[questionId]) return;

  detailLoading.value = true;
  try {
    const requests = [];

    if (!questionCache[questionId]) {
      requests.push(
        getQuestions({ id: questionId }).then((res) => {
          if (res.code === 200 && Array.isArray(res.data) && res.data.length > 0) {
            questionCache[questionId] = res.data[0];
          } else {
            questionCache[questionId] = null;
          }
        })
      );
    }

    if (!answerCache[questionId]) {
      requests.push(
        getAnswers({ questionId }).then((res) => {
          if (res.code === 200 && Array.isArray(res.data) && res.data.length > 0) {
            answerCache[questionId] = res.data[0];
          } else {
            answerCache[questionId] = null;
          }
        })
      );
    }

    await Promise.all(requests);
  } catch (error) {
    console.error(`获取题目/答案失败, questionId=${questionId}:`, error);
    ElMessage.error("获取题目或参考答案失败");
  } finally {
    detailLoading.value = false;
  }
};

const goPrev = () => {
  if (currentIndex.value > 0) {
    currentIndex.value -= 1;
  }
};

const goNext = () => {
  if (currentIndex.value < sortedUserAnswers.value.length - 1) {
    currentIndex.value += 1;
  }
};

const isUngraded = (item) => {
  return item.score === null || item.score === undefined || item.score === -1;
};

const findNextUngradedIndex = () => {
  if (sortedUserAnswers.value.length === 0) return -1;

  for (let i = currentIndex.value + 1; i < sortedUserAnswers.value.length; i += 1) {
    if (isUngraded(sortedUserAnswers.value[i])) {
      return i;
    }
  }

  for (let i = 0; i <= currentIndex.value; i += 1) {
    if (isUngraded(sortedUserAnswers.value[i])) {
      return i;
    }
  }

  return -1;
};

const handleSubmitCurrent = async () => {
  if (!currentUserAnswer.value) return;

  const questionScore = currentQuestion.value?.score;
  const score = currentDraft.value.score;
  const trimmedComment = (currentDraft.value.comment || "").trim();

  if (score === null || score === undefined) {
    ElMessage.warning("评分不能为空");
    return;
  }

  if (typeof questionScore !== "number") {
    ElMessage.warning("未查询到当前题目的分值，无法提交");
    return;
  }

  if (score < 0 || score > questionScore) {
    ElMessage.warning(`评分范围必须在 0 ~ ${questionScore} 之间`);
    return;
  }

  submitting.value = true;
  try {
    const payload = {
      id: currentUserAnswer.value.id,
      score,
    };
    if (trimmedComment) {
      payload.comment = trimmedComment;
    }

    const res = await judgeAnswerManual(payload);
    if (res.code === 200) {
      const target = userAnswers.value.find((item) => item.id === currentUserAnswer.value.id);
      const gradedId = currentUserAnswer.value.id;
      if (target) {
        target.score = score;
        target.comment = trimmedComment;
      }

      const currentSortedIndex = sortedUserAnswers.value.findIndex((item) => item.id === gradedId);
      if (currentSortedIndex !== -1) {
        currentIndex.value = currentSortedIndex;
      }

      const nextUngradedIndex = findNextUngradedIndex();
      if (nextUngradedIndex !== -1) {
        currentIndex.value = nextUngradedIndex;
        ElMessage.success("批改已保存，已跳转到下一条未批改答案");
      } else {
        ElMessage.success("批改已保存，当前筛选范围内已全部批改完成");
      }
    } else {
      ElMessage.error(res.message || "提交批改失败");
    }
  } catch (error) {
    console.error("提交批改失败:", error);
    ElMessage.error("提交批改失败");
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.user-answer-editor {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: var(--text-primary);
  font-weight: 600;
}

.stats-row {
  width: 100%;
}

.stats-text {
  color: var(--text-regular);
  font-weight: 500;
  font-size: 14px;
}

.sub-filter-row {
  margin-bottom: 12px;
  padding: 10px 12px 2px;
  border: 1px solid var(--border-light);
  border-radius: 8px;
  background: var(--bg-primary-alpha);
}

.review-body {
  display: grid;
  grid-template-columns: 40px 1fr 40px;
  align-items: center;
  gap: 12px;
}

.nav-btn {
  justify-self: center;
  color: var(--primary);
  border-color: var(--border-primary-lighter);
}

.answer-card {
  width: 100%;
  border-color: var(--border-light);
}

.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-primary);
  font-weight: 500;
}

.user-meta {
  color: var(--text-regular);
  font-size: 13px;
}

.answer-section {
  margin-bottom: 14px;
  border: 1px solid var(--border-light);
  border-radius: 6px;
  overflow: hidden;
}

.section-title {
  padding: 10px 12px;
  color: var(--text-primary);
  background: var(--bg-primary-grey);
  font-weight: 600;
}

.section-content {
  min-height: 54px;
  padding: 12px;
  color: var(--text-primary);
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.grading-row {
  margin-top: 8px;
}

.score-tip {
  margin-top: -6px;
  color: var(--text-placeholder);
  font-size: 12px;
}

.action-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 6px;
}
</style>
