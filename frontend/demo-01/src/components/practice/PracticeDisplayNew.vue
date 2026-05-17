<template>
	<div class="practice-index-container">
		<div class="practice-panel">
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
							layout="prev, pager, next"
							:total="filteredPractices.length"
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
					<el-button type="primary" size="small" @click="fetchPractices">重新加载</el-button>
				</div>

				<div v-else-if="filteredPractices.length === 0" class="empty-state">
					<el-icon class="empty-icon"><Edit /></el-icon>
					<p>当前章节暂无练习任务</p>
				</div>

				<div v-else class="practice-items">
					<div
						v-for="practice in paginatedPractices"
						:key="practice.id"
						class="practice-item"
						@click="openPractice(practice)"
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
								<el-tag type="info" size="small">点击进入</el-tag>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="preview-panel">
			<div class="empty-preview">
				<el-icon size="60"><Cpu /></el-icon>
				<p>点击左侧练习即可进入答题页面</p>
			</div>
		</div>
	</div>
</template>

<script setup>
import { Cpu, Clock, Warning, Edit } from "@element-plus/icons-vue";
import { ElIcon, ElPagination, ElButton, ElTag, ElMessage } from "element-plus";
import { computed, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { getPracticesByIndex } from "@/api/modules/practice/practice";
import { getPracticeIndexes } from "@/api/modules/practice/practiceIndex";

const props = defineProps({
	courseSectionId: {
		type: [Number, String],
		default: null,
	},
});

const router = useRouter();

const loading = ref(false);
const error = ref(null);
const practices = ref([]);

const currentPage = ref(1);
const pageSize = ref(6);

const normalizedCourseSectionId = computed(() => {
	if (props.courseSectionId === null || props.courseSectionId === undefined || props.courseSectionId === "") {
		return null;
	}

	const parsed = Number(props.courseSectionId);
	return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
});

const filteredPractices = computed(() => {
	if (normalizedCourseSectionId.value === null) {
		return [];
	}
	return practices.value.filter((item) => Number(item.courseSectionId) === normalizedCourseSectionId.value);
});

const paginatedPractices = computed(() => {
	const startIndex = (currentPage.value - 1) * pageSize.value;
	return filteredPractices.value.slice(startIndex, startIndex + pageSize.value);
});

const fetchPractices = async () => {
	loading.value = true;
	error.value = null;

	try {
		if (normalizedCourseSectionId.value === null) {
			practices.value = [];
			return;
		}

		const indexRes = await getPracticeIndexes({ courseSectionId: normalizedCourseSectionId.value });
		if (indexRes?.code !== 200 || !Array.isArray(indexRes?.data)) {
			throw new Error(indexRes?.message || "获取练习索引信息失败");
		}

		const practiceIndexMap = new Map();
		indexRes.data.forEach((indexItem) => {
			const normalizedPracticeId = Number(indexItem.practiceId);
			if (Number.isFinite(normalizedPracticeId) && normalizedPracticeId > 0) {
				practiceIndexMap.set(normalizedPracticeId, {
					courseSectionId: Number(indexItem.courseSectionId),
					courseId: Number(indexItem.courseId || 0),
				});
			}
		});

		if (practiceIndexMap.size === 0) {
			practices.value = [];
			currentPage.value = 1;
			return;
		}

		const practiceRes = await getPracticesByIndex(normalizedCourseSectionId.value);
		if (practiceRes?.code !== 200 || !Array.isArray(practiceRes?.data)) {
			throw new Error(practiceRes?.message || "获取练习信息失败");
		}

		const nowTime = Date.now();
		const fetchedPractices = practiceRes.data
			.filter((practice) => practiceIndexMap.has(Number(practice?.id)))
			.filter((practice) => {
				if (!practice?.createdAt) {
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
				return new Date(b.expiredAt).getTime() - new Date(a.expiredAt).getTime();
			})
			.map((practice) => {
				const practiceInfo = practiceIndexMap.get(Number(practice.id));
				return {
					id: Number(practice.id),
					courseSectionId: Number(practiceInfo?.courseSectionId || normalizedCourseSectionId.value),
					courseId: Number(practiceInfo?.courseId || 0),
					title: practice.name,
					description: `包含 ${practice.questionNum || 0} 个问题的练习`,
					deadline: practice.expiredAt ? new Date(practice.expiredAt).toISOString() : null,
					questionNum: Number(practice.questionNum || 0),
				};
			});

		practices.value = fetchedPractices;
		currentPage.value = 1;
	} catch (requestError) {
		error.value = requestError;
		practices.value = [];
		ElMessage.error("获取练习数据失败，请稍后重试");
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
	const diff = date.getTime() - now.getTime();

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

const openPractice = (practice) => {
	if (!practice?.id || !practice?.courseSectionId) {
		ElMessage.error("练习数据不完整，无法进入练习");
		return;
	}

	router.push({
		name: "Practice",
		params: {
			courseSectionId: practice.courseSectionId,
			practiceId: practice.id,
		},
	});
};

watch(
	() => normalizedCourseSectionId.value,
	() => {
		fetchPractices();
	},
	{ immediate: true }
);

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
	flex: 0 0 380px;
	background: rgba(255, 255, 255, 0.88);
	border-radius: 14px;
	backdrop-filter: blur(10px);
	overflow: hidden;
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}

.scrollable-content {
	padding: 18px;
	height: 100%;
	overflow-y: auto;
}

.panel-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 16px;
	padding-bottom: 12px;
	border-bottom: 1px solid #e5e7eb;
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
	margin-top: 4px;
}

.practice-items {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.practice-item {
	display: flex;
	align-items: flex-start;
	gap: 12px;
	padding: 14px;
	background: #ffffff;
	border-radius: 10px;
	border: 1px solid #e5e7eb;
	cursor: pointer;
	transition: all 0.25s ease;
}

.practice-item:hover {
	transform: translateY(-2px);
	border-color: #409eff;
	box-shadow: 0 6px 16px rgba(64, 158, 255, 0.12);
}

.practice-icon {
	width: 38px;
	height: 38px;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 10px;
	font-size: 18px;
	background: linear-gradient(135deg, #e6f2ff, #c6e2ff);
	color: #409eff;
	flex-shrink: 0;
}

.practice-info {
	flex: 1;
}

.practice-info h4 {
	margin: 0 0 6px 0;
	color: #303133;
	font-size: 15px;
	font-weight: 600;
}

.practice-info p {
	margin: 0 0 8px 0;
	color: #909399;
	font-size: 13px;
}

.practice-meta {
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-size: 12px;
	color: #606266;
}

.deadline {
	display: inline-flex;
	align-items: center;
	gap: 4px;
}

.preview-panel {
	flex: 1;
	background: rgba(255, 255, 255, 0.88);
	border-radius: 14px;
	box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
	display: flex;
	align-items: center;
	justify-content: center;
}

.empty-preview,
.loading-state,
.error-state,
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	text-align: center;
	color: var(--text-placeholder);
	padding: 30px 20px;
}

.loading-icon {
	font-size: 30px;
	margin-bottom: 10px;
	animation: rotate 1s linear infinite;
}

.error-icon,
.empty-icon {
	font-size: 30px;
	margin-bottom: 10px;
}

.error-icon {
	color: var(--danger);
}

@keyframes rotate {
	from {
		transform: rotate(0deg);
	}
	to {
		transform: rotate(360deg);
	}
}

@media (max-width: 900px) {
	.practice-index-container {
		flex-direction: column;
		padding: 12px;
	}

	.practice-panel {
		flex: none;
		width: 100%;
	}

	.preview-panel {
		min-height: 160px;
	}
}
</style>
