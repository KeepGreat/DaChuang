<template>
	<div class="question-display-new">
		<div v-if="!isPracticeIdValid" class="error-view">
			<el-result
				icon="error"
				title="练习加载失败"
				sub-title="未获取到有效的 practiceId，请返回上一页重试"
			/>
		</div>

		<div v-else-if="pageLoading" class="loading-view">
			<el-skeleton :rows="8" animated />
		</div>

		<div v-else class="layout">
			<aside class="left-panel">
				<div class="panel-title">题目导航</div>
				<div v-for="group in groupedQuestions" :key="group.type" class="type-group">
					<div class="group-title">{{ group.label }}</div>
					<div class="question-cards">
						<div
							v-for="(q, idx) in group.questions"
							:key="q.id"
							class="question-card"
							:class="{
								active: currentQuestion && currentQuestion.id === q.id,
								completed: isQuestionCompleted(q.id)
							}"
							@click="selectQuestion(q.id)"
						>
							<div class="card-top">
								<span class="card-index">{{ idx + 1 }}</span>
								<el-tag
									size="small"
									:type="difficultyTagType(q.difficulty)"
									effect="plain"
								>
									{{ difficultyLabel(q.difficulty) }}
								</el-tag>
							</div>
							<div class="card-content">{{ q.name || q.content || '未命名题目' }}</div>
							<div class="card-status">
								<el-tag
									size="small"
									:type="isQuestionCompleted(q.id) ? 'success' : 'info'"
									effect="light"
								>
									{{ isQuestionCompleted(q.id) ? '已完成' : '未完成' }}
								</el-tag>
							</div>
						</div>
					</div>
				</div>
			</aside>

			<section class="right-panel">
				<div v-if="!currentQuestion" class="empty-view">
					<el-empty description="当前练习暂无题目" />
				</div>

				<template v-else>
					<div v-if="currentQuestion.type !== 3" class="question-view">
						<div class="question-headline">{{ currentQuestion.content }}</div>

						<div v-if="currentDescriptionResources.length > 0" class="resource-section">
							<div class="resource-title">题目资源</div>
							<div class="resource-list">
								<div
									v-for="resource in currentDescriptionResources"
									:key="resource.id"
									class="resource-item"
								>
									<div class="resource-meta">
										<div class="resource-name">{{ resource.name }}</div>
										<div class="resource-desc">{{ resource.description || '描述资料' }}</div>
									</div>
									<el-button size="small" type="primary" plain @click="downloadResource(resource)">
										下载
									</el-button>
								</div>
							</div>
						</div>

						<div class="answer-section">
							<template v-if="isChoiceQuestion(currentQuestion)">
								<div class="option-list">
									<div
										v-for="option in normalizeOptions(currentQuestion)"
										:key="option.value"
										class="option-item"
										:class="{ selected: isOptionSelected(currentQuestion, option.value) }"
										@click="toggleOption(currentQuestion, option.value)"
									>
										<div class="option-label">{{ option.label }}</div>
										<div class="option-text">{{ option.text }}</div>
									</div>
								</div>
							</template>

							<template v-else-if="currentQuestion.type === 2">
								<el-input
									v-model="shortAnswerDraft"
									type="textarea"
									:rows="8"
									placeholder="请输入你的答案..."
								/>
							</template>
						</div>

						<div class="action-row">
							<el-button
								type="primary"
								:loading="submitLoading"
								@click="submitCurrentAnswer"
							>
								提交并下一题
							</el-button>
						</div>
					</div>

					<div v-else class="programming-view">
						<div class="programming-left">
							<div class="section-card">
								<h3 class="section-title">问题题目</h3>
								<div class="problem-title">{{ currentQuestion.name || '编程题' }}</div>
							</div>

							<div class="section-card">
								<h3 class="section-title">问题要求</h3>
								<div class="problem-content">{{ currentQuestion.content }}</div>
							</div>
						</div>

						<div class="programming-right">
							<div class="code-card">
								<div class="code-header">
									<span class="code-title">代码编写</span>
								</div>
								<div class="code-content">
									<CodeSandbox
										ref="codeSandboxRef"
										:key="currentQuestion.id"
										:initial-language="programmingDraft.language"
										:initial-code="programmingDraft.code"
										:initial-input="programmingDraft.input"
										@code-change="handleCodeSandboxChange"
									/>
								</div>
								<div class="action-row programming-action">
									<el-button
										type="primary"
										:loading="submitLoading"
										@click="submitCurrentAnswer"
									>
										提交并下一题
									</el-button>
								</div>
							</div>
						</div>
					</div>
				</template>
			</section>
		</div>
	</div>
</template>

<script setup>
import {
	createUserAnswer,
	downloadQuestionResource,
	getQuestionByIndex,
	getQuestionResources,
	getUserAnswers,
	getUserId,
	updateUserAnswerById,
} from "@/api";
import { useUserStore } from "@/store";
import { ElMessage } from "element-plus";
import { computed, onMounted, ref, watch } from "vue";
import CodeSandbox from "../teaching/CodeSandbox.vue";

const props = defineProps({
	practiceId: {
		type: [String, Number, null],
		default: null,
	},
});

const userStore = useUserStore();

const pageLoading = ref(false);
const submitLoading = ref(false);
const currentUserId = ref("");
const questions = ref([]);
const selectedQuestionId = ref(null);
const answerDraftMap = ref({});
const existingAnswerMap = ref({});
const resourceCache = ref({});
const programmingStateMap = ref({});
const codeSandboxRef = ref(null);

const typeLabelMap = {
	0: "判断题",
	1: "选择题",
	2: "简答题",
	3: "编程题",
};

const isPracticeIdValid = computed(() => {
	return Number(props.practiceId) > 0;
});

const normalizedPracticeId = computed(() => Number(props.practiceId));

const currentQuestion = computed(() => {
	return questions.value.find((q) => q.id === selectedQuestionId.value) || null;
});

const groupedQuestions = computed(() => {
	const groups = [0, 1, 2, 3].map((type) => ({
		type,
		label: typeLabelMap[type],
		questions: questions.value.filter((q) => Number(q.type) === type),
	}));
	return groups.filter((g) => g.questions.length > 0);
});

const currentDescriptionResources = computed(() => {
	if (!currentQuestion.value) return [];
	return resourceCache.value[currentQuestion.value.id] || [];
});

const shortAnswerDraft = computed({
	get() {
		if (!currentQuestion.value) return "";
		return answerDraftMap.value[currentQuestion.value.id] || "";
	},
	set(val) {
		if (!currentQuestion.value) return;
		answerDraftMap.value[currentQuestion.value.id] = val;
	},
});

const programmingDraft = computed(() => {
	if (!currentQuestion.value) {
		return { language: "cpp", code: "", input: "" };
	}
	const qid = currentQuestion.value.id;
	if (!programmingStateMap.value[qid]) {
		programmingStateMap.value[qid] = {
			language: "cpp",
			code: typeof answerDraftMap.value[qid] === "string" ? answerDraftMap.value[qid] : "",
			input: currentQuestion.value.input || currentQuestion.value.codeInput || "",
		};
	}
	return programmingStateMap.value[qid];
});

function difficultyLabel(level) {
	const map = {
		0: "简单",
		1: "中等",
		2: "困难",
	};
	return map[level] || "未知";
}

function difficultyTagType(level) {
	if (level === 0) return "success";
	if (level === 1) return "warning";
	if (level === 2) return "danger";
	return "info";
}

function isChoiceQuestion(question) {
	return Number(question.type) === 0 || Number(question.type) === 1;
}

function isQuestionCompleted(questionId) {
	return Boolean(existingAnswerMap.value[questionId]?.id);
}

function normalizeOptions(question) {
	if (!question) return [];

	if (Number(question.type) === 0) {
		if (!question.options || question.options.length === 0) {
			return [
				{ label: "A", value: "true", text: "正确" },
				{ label: "B", value: "false", text: "错误" },
			];
		}
	}

	let rawOptions = question.options;
	if (typeof rawOptions === "string") {
		try {
			rawOptions = JSON.parse(rawOptions);
		} catch (e) {
			rawOptions = rawOptions
				.split("|")
				.map((item) => item.trim())
				.filter(Boolean);
		}
	}

	if (!Array.isArray(rawOptions)) return [];

	return rawOptions.map((item, idx) => {
		if (typeof item === "object") {
			return {
				label: item.label || String.fromCharCode(65 + idx),
				value: item.value ?? item.label ?? String(idx),
				text: item.text ?? item.content ?? String(item.value ?? item.label ?? ""),
			};
		}
		return {
			label: String.fromCharCode(65 + idx),
			value: String.fromCharCode(65 + idx),
			text: String(item),
		};
	});
}

function isOptionSelected(question, optionValue) {
	const answer = answerDraftMap.value[question.id];
	if (Array.isArray(answer)) {
		return answer.includes(optionValue);
	}
	return answer === optionValue;
}

function toggleOption(question, optionValue) {
	const questionId = question.id;
	const current = answerDraftMap.value[questionId];

	if (Number(question.type) === 0) {
		answerDraftMap.value[questionId] = optionValue;
		return;
	}

	const list = Array.isArray(current) ? [...current] : [];
	const index = list.indexOf(optionValue);
	if (index >= 0) {
		list.splice(index, 1);
	} else {
		list.push(optionValue);
	}
	answerDraftMap.value[questionId] = list;
}

function selectQuestion(questionId) {
	selectedQuestionId.value = questionId;
	ensureDescriptionResources(questionId);
}

function moveToNextQuestion() {
	if (!currentQuestion.value) return;
	const flat = questions.value;
	const currentIndex = flat.findIndex((q) => q.id === currentQuestion.value.id);
	if (currentIndex >= 0 && currentIndex < flat.length - 1) {
		selectedQuestionId.value = flat[currentIndex + 1].id;
		ensureDescriptionResources(selectedQuestionId.value);
	}
}

async function resolveUserId() {
	if (userStore.userId) {
		currentUserId.value = userStore.userId;
		return;
	}

	if (!userStore.token) {
		throw new Error("用户未登录，无法提交答案");
	}

	const response = await getUserId({ token: userStore.token });
	if (!response?.data) {
		throw new Error("获取用户信息失败");
	}

	currentUserId.value = response.data;
	userStore.setUserId(response.data);
}

function hydrateDraftFromAnswer(question, answerContent) {
	const qid = question.id;
	const type = Number(question.type);

	if (type === 0) {
		answerDraftMap.value[qid] = answerContent;
		return;
	}

	if (type === 1) {
		answerDraftMap.value[qid] = String(answerContent)
			.split(",")
			.map((item) => item.trim())
			.filter(Boolean);
		return;
	}

	answerDraftMap.value[qid] = answerContent;
}

async function loadExistingAnswers() {
	const response = await getUserAnswers({
		userId: currentUserId.value,
		practiceId: normalizedPracticeId.value,
	});
	const records = Array.isArray(response?.data) ? response.data : [];

	records.forEach((record) => {
		existingAnswerMap.value[record.questionId] = record;
		const question = questions.value.find((q) => q.id === record.questionId);
		if (question) {
			hydrateDraftFromAnswer(question, record.content || "");
		}
	});
}

async function loadQuestions() {
	const response = await getQuestionByIndex(normalizedPracticeId.value);
	questions.value = Array.isArray(response?.data) ? response.data : [];
	selectedQuestionId.value = questions.value.length > 0 ? questions.value[0].id : null;
}

async function ensureDescriptionResources(questionId) {
	if (!questionId || resourceCache.value[questionId]) return;

	try {
		const response = await getQuestionResources(undefined, undefined, 2, undefined, questionId);
		resourceCache.value[questionId] = Array.isArray(response?.data) ? response.data : [];
	} catch (error) {
		resourceCache.value[questionId] = [];
	}
}

function buildAnswerContent(question) {
	const qid = question.id;
	if (Number(question.type) === 3) {
		const code = programmingStateMap.value[qid]?.code || "";
		return code;
	}

	const draft = answerDraftMap.value[qid];
	if (Array.isArray(draft)) {
		return draft.join(",");
	}
	return String(draft || "").trim();
}

function validateBeforeSubmit(question, content) {
	if (Number(question.type) === 3) {
		return content.trim().length > 0;
	}

	if (Number(question.type) === 2) {
		return content.trim().length > 0;
	}

	return Boolean(content);
}

async function submitCurrentAnswer() {
	if (!currentQuestion.value) return;

	const question = currentQuestion.value;
	const content = buildAnswerContent(question);
	if (!validateBeforeSubmit(question, content)) {
		ElMessage.warning("请先作答后再提交");
		return;
	}

	submitLoading.value = true;
	try {
		await resolveUserId();

		const payload = {
			content,
			userId: currentUserId.value,
			questionId: question.id,
			practiceId: normalizedPracticeId.value,
			questionType: Number(question.type),
		};

		const existing = existingAnswerMap.value[question.id];
		if (existing?.id) {
			await updateUserAnswerById({
				id: existing.id,
				...payload,
			});
		} else {
			await createUserAnswer(payload);
			const refreshed = await getUserAnswers({
				userId: currentUserId.value,
				questionId: question.id,
				practiceId: normalizedPracticeId.value,
			});
			if (Array.isArray(refreshed?.data) && refreshed.data.length > 0) {
				existingAnswerMap.value[question.id] = refreshed.data[0];
			}
		}

		ElMessage.success("提交成功");
		moveToNextQuestion();
	} catch (error) {
		console.error("提交答案失败:", error);
		ElMessage.error(error?.message || "提交失败，请稍后重试");
	} finally {
		submitLoading.value = false;
	}
}

function handleCodeSandboxChange(payload) {
	if (!currentQuestion.value) return;
	const qid = currentQuestion.value.id;
	programmingStateMap.value[qid] = {
		language: payload?.language || "cpp",
		code: payload?.code || "",
		input: payload?.input || "",
	};
	answerDraftMap.value[qid] = payload?.code || "";
}

async function downloadResource(resource) {
	try {
		const response = await downloadQuestionResource(resource.id);
		const blob = new Blob([response.data]);
		const url = window.URL.createObjectURL(blob);
		const link = document.createElement("a");
		link.href = url;
		link.setAttribute("download", resource.name || "resource");
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
		window.URL.revokeObjectURL(url);
	} catch (error) {
		ElMessage.error("资源下载失败");
	}
}

async function initPage() {
	if (!isPracticeIdValid.value) return;

	pageLoading.value = true;
	questions.value = [];
	selectedQuestionId.value = null;
	answerDraftMap.value = {};
	existingAnswerMap.value = {};
	resourceCache.value = {};
	programmingStateMap.value = {};

	try {
		await resolveUserId();
		await loadQuestions();
		await loadExistingAnswers();
		if (selectedQuestionId.value) {
			await ensureDescriptionResources(selectedQuestionId.value);
		}
	} catch (error) {
		console.error("初始化题目页面失败:", error);
		ElMessage.error(error?.message || "题目加载失败，请刷新后重试");
	} finally {
		pageLoading.value = false;
	}
}

watch(
	() => props.practiceId,
	() => {
		initPage();
	}
);

watch(
	() => currentQuestion.value?.id,
	(qid) => {
		if (qid) {
			ensureDescriptionResources(qid);
		}
	}
);

onMounted(() => {
	initPage();
});
</script>

<style scoped>
.question-display-new {
	width: 100%;
	min-height: 620px;
	background: var(--bg-white);
	border: 1px solid var(--border-light);
	border-radius: 10px;
}

.error-view,
.loading-view,
.empty-view {
	padding: 32px;
}

.layout {
	display: grid;
	grid-template-columns: 300px 1fr;
	min-height: 620px;
}

.left-panel {
	border-right: 1px solid var(--border-light);
	background: var(--bg-primary-light);
	padding: 16px;
	overflow-y: auto;
}

.panel-title {
	color: var(--primary-dark);
	font-size: 16px;
	font-weight: 600;
	margin-bottom: 14px;
}

.type-group {
	margin-bottom: 16px;
}

.group-title {
	color: var(--text-primary);
	font-size: 14px;
	font-weight: 600;
	margin-bottom: 10px;
}

.question-cards {
	display: flex;
	flex-direction: column;
	gap: 10px;
}

.question-card {
	background: var(--bg-white);
	border: 1px solid var(--border-light);
	border-radius: 8px;
	padding: 10px;
	cursor: pointer;
	transition: all 0.2s ease;
}

.question-card:hover {
	border-color: var(--primary-light);
	box-shadow: 0 2px 8px var(--primary-alpha-10);
}

.question-card.active {
	border-color: var(--primary);
	box-shadow: 0 0 0 2px var(--primary-alpha-10);
}

.question-card.completed {
	background: var(--success-alpha-10);
}

.card-top {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 6px;
}

.card-index {
	color: var(--primary);
	font-size: 12px;
	font-weight: 700;
}

.card-content {
	color: var(--text-primary);
	font-size: 13px;
	line-height: 1.4;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	margin-bottom: 8px;
}

.card-status {
	display: flex;
	justify-content: flex-end;
}

.right-panel {
	padding: 20px;
	background: var(--bg-white);
}

.question-view {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

.question-headline {
	color: var(--text-primary);
	font-size: 18px;
	font-weight: 600;
	padding: 14px;
	background: var(--bg-light);
	border: 1px solid var(--border-primary-lighter);
	border-radius: 8px;
}

.resource-section {
	border: 1px solid var(--border-light);
	border-radius: 8px;
	padding: 12px;
	background: var(--bg-primary-light);
}

.resource-title {
	font-size: 14px;
	color: var(--primary-dark);
	font-weight: 600;
	margin-bottom: 10px;
}

.resource-list {
	display: flex;
	flex-direction: column;
	gap: 8px;
}

.resource-item {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 10px;
	padding: 10px;
	border: 1px solid var(--border-light);
	border-radius: 6px;
	background: var(--bg-white);
}

.resource-meta {
	min-width: 0;
}

.resource-name {
	color: var(--text-primary);
	font-size: 13px;
	font-weight: 500;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.resource-desc {
	color: var(--text-regular);
	font-size: 12px;
	margin-top: 2px;
}

.answer-section {
	border: 1px solid var(--border-light);
	border-radius: 8px;
	padding: 16px;
}

.option-list {
	display: flex;
	flex-direction: column;
	gap: 10px;
}

.option-item {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 10px;
	border: 1px solid var(--border-light);
	border-radius: 6px;
	cursor: pointer;
	transition: all 0.2s ease;
}

.option-item:hover {
	border-color: var(--primary-light);
}

.option-item.selected {
	border-color: var(--primary);
	background: var(--primary-alpha-10);
}

.option-label {
	width: 26px;
	height: 26px;
	border-radius: 50%;
	background: var(--bg-primary-lighter);
	color: var(--primary-dark);
	display: flex;
	align-items: center;
	justify-content: center;
	font-weight: 600;
	font-size: 12px;
	flex-shrink: 0;
}

.option-text {
	color: var(--text-primary);
	font-size: 14px;
}

.action-row {
	display: flex;
	justify-content: flex-end;
}

.programming-view {
	display: grid;
	grid-template-columns: 1fr 1.1fr;
	gap: 16px;
}

.programming-left {
	display: flex;
	flex-direction: column;
	gap: 14px;
}

.section-card {
	background: var(--bg-white);
	border: 1px solid var(--border-light);
	border-radius: 8px;
	padding: 14px;
}

.section-title {
	color: var(--primary-dark);
	margin: 0 0 10px 0;
	font-size: 14px;
	font-weight: 600;
}

.problem-title {
	color: var(--text-primary);
	font-size: 16px;
	font-weight: 600;
}

.problem-content {
	color: var(--text-regular);
	font-size: 14px;
	line-height: 1.6;
	white-space: pre-wrap;
}

.programming-right,
.code-card {
	min-height: 520px;
}

.code-card {
	border: 1px solid var(--border-light);
	border-radius: 8px;
	background: var(--bg-white);
	display: flex;
	flex-direction: column;
}

.code-header {
	padding: 12px 14px;
	border-bottom: 1px solid var(--border-light);
	background: var(--bg-primary-light);
}

.code-title {
	color: var(--primary-dark);
	font-size: 14px;
	font-weight: 600;
}

.code-content {
	flex: 1;
}

.programming-action {
	padding: 12px 14px;
	border-top: 1px solid var(--border-light);
}

@media (max-width: 1100px) {
	.layout {
		grid-template-columns: 1fr;
	}

	.left-panel {
		border-right: none;
		border-bottom: 1px solid var(--border-light);
		max-height: 280px;
	}

	.programming-view {
		grid-template-columns: 1fr;
	}
}
</style>
