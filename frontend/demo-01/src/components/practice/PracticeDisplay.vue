<template>
	<div class="practice-index-container">
		<div class="practice-panel" :class="{ 'is-scrolling': isScrolling }" @scroll="handleScroll">
			<div class="scrollable-content">
				<div class="panel-header">
					<div class="header-left">
						<h3>练习任务</h3>
						<div class="stats">
							<span>共 {{ filteredPractices.length }} 个练习</span>
						</div>
					</div>
					<div class="header-right">
						<el-pagination
							v-model:current-page="currentPage"
							v-model:page-size="pageSize"
							:page-sizes="[6, 12, 18]"
							layout="prev, pager, next"
							:total="filteredPractices.length"
							@size-change="handleSizeChange"
							@current-change="handleCurrentChange"
							small
							background
						/>
					</div>
				</div>

				<div v-if="loading" class="loading-state">
					<el-icon class="loading-icon"><Cpu /></el-icon>
					<p>正在加载练习数据...</p>
				</div>

				<div v-else-if="error" class="error-state">
					<el-icon class="error-icon"><Warning /></el-icon>
					<p>加载失败，请稍后重试</p>
					<el-button type="primary" size="small" @click="fetchPractices">
						重新加载
					</el-button>
				</div>

				<div v-else-if="filteredPractices.length === 0" class="empty-state">
					<el-icon class="empty-icon"><Edit /></el-icon>
					<p>暂无练习任务</p>
				</div>

				<div v-else class="practice-items">
					<div
						v-for="practice in paginatedPractices"
						:key="practice.id"
						class="practice-item"
						:class="{ active: selectedPracticeId === practice.id }"
						@click="selectPractice(practice)"
					>
						<div class="practice-icon">
							<el-icon><Cpu /></el-icon>
						</div>
						<div class="practice-info">
							<h4>{{ practice.title }}</h4>
							<p>{{ practice.description }}</p>
							<div class="practice-meta">
								<span class="deadline" v-if="practice.deadline">
									<el-icon><Clock /></el-icon>
									截止: {{ formatDeadline(practice.deadline) }}
								</span>
								<span class="deadline" v-else>
									<el-icon><Clock /></el-icon>
									无截止时间
								</span>
								<div class="practice-status">
									<el-tag
										type="info"
										size="small"
										v-if="selectedPracticeId === practice.id && currentPracticeQuestionCount > 0"
									>
										{{ currentPracticeQuestionCount }} 题
									</el-tag>
									<el-tag type="info" size="small" v-else>未开始</el-tag>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="preview-panel">
			<div v-if="loadingQuestions" class="loading-state">
				<el-icon class="loading-icon"><Cpu /></el-icon>
				<p>正在加载练习详情...</p>
			</div>

			<div v-else-if="selectedPractice" class="practice-preview">
				<el-card class="preview-card">
					<template #header>
						<div class="preview-header">
							<h4>{{ selectedPractice.title }}</h4>
							<el-button type="primary" @click="goPractice">开始练习</el-button>
						</div>
					</template>

					<div class="preview-content">
						<div class="preview-section">
							<h5>练习信息</h5>
							<p>
								本练习包含 {{ selectedPractice.questionNum || 0 }} 个算法题目，
								旨在帮助您掌握基础算法思想和编程技巧。
							</p>
							<div v-if="selectedPractice.deadline" class="deadline-info">
								<el-icon><Clock /></el-icon>
								截止时间: {{ new Date(selectedPractice.deadline).toLocaleString() }}
								<el-tag
									:type="new Date(selectedPractice.deadline) < new Date() ? 'danger' : 'info'"
									size="small"
								>
									{{ formatDeadline(selectedPractice.deadline) }}
								</el-tag>
							</div>
							<div v-else class="deadline-info">
								<el-icon><Clock /></el-icon>
								截止时间: 长期有效
								<el-tag type="success" size="small">长期有效</el-tag>
							</div>
						</div>

						<div class="preview-section">
							<h5>练习目标</h5>
							<ul>
								<li>掌握基础算法设计思路</li>
								<li>提高代码实现能力</li>
								<li>理解时间复杂度分析</li>
								<li>培养编程思维</li>
							</ul>
						</div>

						<div class="preview-section">
							<h5>题目信息</h5>
							<p>本练习共包含 {{ questionsStore.questions.length }} 道题目。</p>
							<div v-if="questionsStore.questions.length > 0">
								<el-tag type="info" size="small">{{ questionsStore.questions.length }} 道题</el-tag>
							</div>
						</div>

						<div class="preview-section">
							<h5>难度分布</h5>
							<p>题目难度循序渐进，从简单到复杂，适合不同水平的学员。</p>
						</div>
					</div>
				</el-card>
			</div>

			<div v-else class="empty-preview">
				<el-icon size="60"><Cpu /></el-icon>
				<p>请选择练习查看详情</p>
			</div>
		</div>
	</div>
</template>

<script setup>
import { Cpu, Clock, Warning, Edit } from "@element-plus/icons-vue";
import { ElCard, ElIcon, ElPagination, ElButton, ElTag, ElMessage } from "element-plus";
import { computed, ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { getPractices } from "@/api/modules/practice/practice";
import { getPracticeIndexes } from "@/api/modules/practice/practiceIndex";
import { getQuestionByIndex } from "@/api/modules/practice/question";
import { useQuestionsStore } from "@/store";

const props = defineProps({
	courseSectionId: {
		type: [Number, String],
		default: null,
	},
});

const router = useRouter();
const questionsStore = useQuestionsStore();

const loading = ref(false);
const error = ref(null);
const loadingQuestions = ref(false);

const isScrolling = ref(false);
let scrollTimeout = null;

const practices = ref([]);
const selectedPracticeId = ref(null);

const currentPage = ref(1);
const pageSize = ref(6);

const normalizedCourseSectionId = computed(() => {
	if (props.courseSectionId === null || props.courseSectionId === undefined || props.courseSectionId === "") {
		return null;
	}

	const parsed = Number(props.courseSectionId);
	return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
});

const normalizeList = (value) => {
	if (Array.isArray(value)) {
		return value;
	}
	if (value && typeof value === "object") {
		return [value];
	}
	return [];
};

const clearCurrentSelection = () => {
	selectedPracticeId.value = null;
	questionsStore.setQuestions([]);
};

const filteredPractices = computed(() => {
	if (normalizedCourseSectionId.value === null) {
		return practices.value;
	}

	return practices.value.filter((practice) => Number(practice.courseSectionId) === normalizedCourseSectionId.value);
});

const paginatedPractices = computed(() => {
	const startIndex = (currentPage.value - 1) * pageSize.value;
	return filteredPractices.value.slice(startIndex, startIndex + pageSize.value);
});

const selectedPractice = computed(() => {
	if (!selectedPracticeId.value) {
		return null;
	}
	return filteredPractices.value.find((practice) => practice.id === selectedPracticeId.value) || null;
});

const currentPracticeQuestionCount = computed(() => questionsStore.questions.length);

const handleSizeChange = (value) => {
	pageSize.value = value;
	currentPage.value = 1;
};

const handleCurrentChange = (value) => {
	currentPage.value = value;
};

const handleScroll = () => {
	isScrolling.value = true;
	if (scrollTimeout) {
		clearTimeout(scrollTimeout);
	}
	scrollTimeout = setTimeout(() => {
		isScrolling.value = false;
	}, 1000);
};

const selectPractice = async (practice) => {
	if (!practice || selectedPracticeId.value === practice.id) {
		return;
	}

	selectedPracticeId.value = practice.id;
	loadingQuestions.value = true;

	try {
		const response = await getQuestionByIndex(practice.id);
		if (response.code === 200 && response.data) {
			questionsStore.setQuestions(response.data);
		} else {
			questionsStore.setQuestions([]);
			ElMessage.warning("获取题目失败: " + (response.message || "未知错误"));
		}
	} catch (requestError) {
		console.error("获取题目失败:", requestError);
		questionsStore.setQuestions([]);
		ElMessage.error("获取题目失败，请稍后重试");
	} finally {
		loadingQuestions.value = false;
	}
};

const fetchPractices = async () => {
	loading.value = true;
	error.value = null;

	try {
		const indexParams = {};
		if (normalizedCourseSectionId.value !== null) {
			indexParams.courseSectionId = normalizedCourseSectionId.value;
		}

		const practiceIndexesResponse = await getPracticeIndexes(indexParams);
		const practiceIndexList = normalizeList(practiceIndexesResponse?.data);
		if (practiceIndexesResponse?.code !== 200) {
			throw new Error("获取练习索引信息失败");
		}

		const practiceIndexMap = new Map();
		practiceIndexList.forEach((indexItem) => {
			const normalizedPracticeId = Number(indexItem.practiceId);
			if (Number.isFinite(normalizedPracticeId) && normalizedPracticeId > 0) {
				if (!practiceIndexMap.has(normalizedPracticeId)) {
					practiceIndexMap.set(normalizedPracticeId, {
						courseSectionId: Number(indexItem.courseSectionId),
						courseId: Number(indexItem.courseId || 0),
					});
				}
			}
		});

		if (practiceIndexMap.size === 0) {
			practices.value = [];
			clearCurrentSelection();
			return;
		}

		const practiceIds = [...practiceIndexMap.keys()];
		const practiceResponses = await Promise.all(practiceIds.map((practiceId) => getPractices(practiceId)));
		const practiceList = practiceResponses
			.map((res, index) => {
				if (res?.code !== 200) {
					return null;
				}
				const targetId = practiceIds[index];
				const candidates = normalizeList(res?.data);
				return candidates.find((item) => Number(item?.id) === Number(targetId)) || null;
			})
			.filter(Boolean);

		const nowTime = Date.now();

		const fetchedPractices = practiceList
			.filter((practice) => {
				const mapped = practiceIndexMap.get(Number(practice.id));
				if (!mapped) {
					return false;
				}

				if (normalizedCourseSectionId.value !== null && Number(mapped.courseSectionId) !== normalizedCourseSectionId.value) {
					return false;
				}

				if (!practice.createdAt) {
					return true;
				}

				const createdAtTime = new Date(practice.createdAt).getTime();
				if (Number.isNaN(createdAtTime)) {
					return true;
				}

				return createdAtTime <= nowTime;
			})
			.sort((a, b) => {
				if (!a.expiredAt && !b.expiredAt) {
					return 0;
				}
				if (!a.expiredAt) {
					return -1;
				}
				if (!b.expiredAt) {
					return 1;
				}
				return new Date(b.expiredAt) - new Date(a.expiredAt);
			})
			.map((practice) => {
				const practiceInfo = practiceIndexMap.get(Number(practice.id));
				return {
					id: Number(practice.id),
					courseSectionId: practiceInfo.courseSectionId,
					courseId: practiceInfo.courseId,
					title: practice.name,
					description: `包含 ${practice.questionNum || 0} 个问题的练习`,
					requirement: "",
					deadline: practice.expiredAt ? new Date(practice.expiredAt).toISOString() : null,
					status: "未开始",
					score: null,
					totalScore: 100,
					difficulty: 1,
					questionNum: practice.questionNum || 0,
					createTime: practice.createdAt || new Date().toISOString(),
				};
			});

		practices.value = fetchedPractices;
		currentPage.value = 1;

		if (filteredPractices.value.length > 0) {
			await selectPractice(filteredPractices.value[0]);
		} else {
			clearCurrentSelection();
		}
	} catch (requestError) {
		error.value = requestError;
		practices.value = [];
		clearCurrentSelection();
		ElMessage.error("获取练习数据失败，请稍后重试");
		console.error("获取练习数据失败:", requestError);
	} finally {
		loading.value = false;
	}
};

const formatDeadline = (deadline) => {
	if (!deadline) {
		return "长期有效";
	}

	const date = new Date(deadline);
	const now = new Date();
	const diff = date - now;

	if (diff < 0) {
		return "已过期";
	}

	const days = Math.floor(diff / (1000 * 60 * 60 * 24));
	const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));

	if (days > 0) {
		return `${days}天后`;
	}
	if (hours > 0) {
		return `${hours}小时后`;
	}
	return "即将截止";
};

const goPractice = () => {
	if (!selectedPractice.value) {
		ElMessage.warning("请先选择一个练习");
		return;
	}

	if (!selectedPractice.value.courseSectionId) {
		ElMessage.error("练习数据不完整，无法进入练习");
		return;
	}

	router.push({
		name: "Practice",
		params: {
			courseSectionId: selectedPractice.value.courseSectionId,
			practiceId: selectedPractice.value.id,
		},
	});
};

watch(
	() => props.courseSectionId,
	() => {
		fetchPractices();
	}
);

onMounted(() => {
	fetchPractices();
});

defineExpose({
	fetchPractices,
});
</script>

<style scoped>
.practice-index-container {
	display: flex;
	gap: 20px;
	height: 100%;
	padding: 20px;
	background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.practice-panel {
	flex: 0 0 350px;
	background: rgba(255, 255, 255, 0.8);
	border-radius: 12px;
	padding: 0;
	overflow-y: auto;
	backdrop-filter: blur(10px);
	scrollbar-width: thin;
	scrollbar-color: transparent transparent;
	transition: scrollbar-color 0.3s ease;
	overflow-y: overlay;
	scrollbar-gutter: stable;
	position: relative;
}

.practice-panel.is-scrolling {
	scrollbar-color: rgba(144, 147, 153, 0.5) transparent;
}

.practice-panel::-webkit-scrollbar {
	width: 6px;
}

.practice-panel::-webkit-scrollbar-track {
	background: transparent;
}

.practice-panel::-webkit-scrollbar-thumb {
	background-color: transparent;
	border-radius: 3px;
	transition: background-color 0.3s ease;
}

.practice-panel.is-scrolling::-webkit-scrollbar-thumb {
	background-color: rgba(144, 147, 153, 0.5);
}

.practice-panel::-webkit-scrollbar-button {
	display: none;
}

.practice-panel::-webkit-scrollbar-corner {
	background: transparent;
}

.practice-panel > * {
	width: calc(100% - 6px);
}

.practice-panel .scrollable-content {
	width: 100%;
	box-sizing: border-box;
	padding: 20px;
	padding-right: 14px;
}

.panel-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20px;
	padding-bottom: 15px;
	border-bottom: 1px solid #e5e7eb;
}

.header-left {
	display: flex;
	flex-direction: column;
	gap: 4px;
}

.header-left h3 {
	margin: 0;
	color: #409eff;
	font-size: 18px;
	font-weight: 700;
}

.stats {
	color: #909399;
	font-size: 12px;
}

.header-right {
	display: flex;
	align-items: center;
}

:deep(.el-pagination) {
	padding: 0;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pagination .el-pager li) {
	background: rgba(255, 255, 255, 0.6);
	border: 1px solid var(--border-light);
	margin: 0 2px;
	border-radius: 6px;
	transition: all 0.3s ease;
}

:deep(.el-pagination .btn-prev:hover),
:deep(.el-pagination .btn-next:hover),
:deep(.el-pagination .el-pager li:hover) {
	background: #f5f7fa;
	border-color: #409eff;
	color: #409eff;
}

:deep(.el-pagination .el-pager li.active) {
	background: var(--gradient-brand);
	border-color: var(--primary);
	color: white;
}

.practice-items {
	display: flex;
	flex-direction: column;
	gap: 12px;
	margin-bottom: 20px;
}

.practice-item {
	display: flex;
	align-items: flex-start;
	gap: 12px;
	padding: 16px;
	background: white;
	border-radius: 10px;
	cursor: pointer;
	transition: all 0.3s ease;
	border: 1px solid #e5e7eb;
}

.practice-item:hover {
	transform: translateY(-2px);
	box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.practice-item.active {
	background: linear-gradient(135deg, #e6f2ff 0%, #f0f9ff 100%);
	border-color: #409eff;
}

.practice-icon {
	width: 40px;
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 10px;
	font-size: 20px;
	background: linear-gradient(135deg, #e6f2ff, #c6e2ff);
	color: #409eff;
	flex-shrink: 0;
}

.practice-info {
	flex: 1;
}

.practice-info h4 {
	margin: 0 0 8px 0;
	color: #303133;
	font-size: 15px;
	font-weight: 600;
}

.practice-info p {
	margin: 0 0 10px 0;
	color: var(--text-placeholder);
	font-size: 13px;
	line-height: 1.4;
}

.practice-meta {
	display: flex;
	align-items: center;
	justify-content: space-between;
	font-size: 12px;
}

.practice-status {
	display: flex;
	gap: 4px;
}

.preview-panel {
	flex: 1;
	background: rgba(255, 255, 255, 0.8);
	border-radius: 12px;
	padding: 20px;
	overflow-y: auto;
	backdrop-filter: blur(10px);
}

.practice-preview {
	margin-bottom: 20px;
}

.preview-card {
	border-radius: 12px;
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.preview-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.preview-header h4 {
	margin: 0;
	color: var(--primary);
	font-size: 18px;
	font-weight: 700;
}

.preview-content {
	color: #606266;
}

.preview-section {
	margin-bottom: 20px;
}

.preview-section h5 {
	margin: 0 0 10px 0;
	color: #303133;
	font-size: 16px;
	font-weight: 600;
}

.preview-section p,
.preview-section ul {
	margin: 0;
	font-size: 14px;
	line-height: 1.6;
}

.preview-section ul {
	padding-left: 20px;
}

.preview-section li {
	margin-bottom: 5px;
}

.deadline-info {
	display: flex;
	align-items: center;
	gap: 8px;
	margin-top: 10px;
	padding: 8px 12px;
	background: var(--bg-primary-alpha);
	border-radius: 6px;
	font-size: 13px;
	color: var(--text-regular);
}

.empty-preview {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	height: 200px;
	color: var(--text-placeholder);
}

.empty-preview .el-icon {
	margin-bottom: 20px;
	opacity: 0.5;
}

.loading-state,
.error-state,
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 40px 20px;
	color: var(--text-placeholder);
	text-align: center;
}

.preview-panel .loading-state {
	height: 200px;
}

.loading-state .loading-icon {
	font-size: 32px;
	margin-bottom: 12px;
	animation: rotate 1s linear infinite;
}

.error-state .error-icon {
	font-size: 32px;
	margin-bottom: 12px;
	color: var(--danger);
}

.empty-state .empty-icon {
	font-size: 32px;
	margin-bottom: 12px;
}

@keyframes rotate {
	from {
		transform: rotate(0deg);
	}
	to {
		transform: rotate(360deg);
	}
}

@media (max-width: 1200px) {
	.practice-panel {
		flex: 0 0 300px;
	}
}

@media (max-width: 768px) {
	.practice-index-container {
		flex-direction: column;
		padding: 10px;
		gap: 15px;
	}

	.practice-panel {
		flex: none;
		width: 100%;
		padding: 15px;
	}

	.panel-header {
		flex-direction: column;
		gap: 15px;
		align-items: stretch;
	}

	.header-left {
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
	}

	.header-right {
		justify-content: center;
	}

	:deep(.el-pagination) {
		justify-content: center;
	}
}
</style>
