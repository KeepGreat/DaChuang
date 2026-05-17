<template>
	<el-dialog
		v-model="dialogVisible"
		title="新建练习"
		width="860px"
		destroy-on-close
	>
		<div class="create-practice-dialog">
			<div class="dialog-section">
				<h4 class="dialog-section-title">练习字段编辑</h4>
				<el-form :model="newPracticeForm" label-width="110px">
					<el-row v-if="!presetCourseSectionId" :gutter="16">
						<el-col :span="24">
							<el-form-item label="关联课程" required>
								<el-select
									v-model="selectedCourseSectionId"
									placeholder="请选择课程"
									style="width: 100%"
									filterable
								>
									<el-option
										v-for="section in courseSectionOptions"
										:key="section.id"
										:label="section.name"
										:value="section.id"
									/>
								</el-select>
							</el-form-item>
						</el-col>
					</el-row>

					<el-row :gutter="16">
						<el-col :span="12">
							<el-form-item label="练习名称" required>
								<el-input v-model="newPracticeForm.name" placeholder="请输入练习名称" />
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="练习类型" required>
								<el-select
									v-model="newPracticeForm.practiceTypeId"
									placeholder="请选择练习类型"
									style="width: 100%"
								>
									<el-option
										v-for="type in practiceTypes"
										:key="type.id"
										:label="type.name"
										:value="type.id"
									/>
								</el-select>
							</el-form-item>
						</el-col>
					</el-row>

					<el-row :gutter="16">
						<el-col :span="12">
							<el-form-item label="问题数量">
								<el-input :model-value="String(selectedQuestionRows.length)" disabled />
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="创建时间">
								<el-input :model-value="newPracticeForm.createdAt" disabled />
							</el-form-item>
						</el-col>
					</el-row>

					<el-row :gutter="16">
						<el-col :span="12">
							<el-form-item label="过期时间">
								<el-date-picker
									v-model="newPracticeForm.expiredAt"
									type="datetime"
									placeholder="可选"
									style="width: 100%"
								/>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="关联课时">
								<el-select
									v-model="newPracticeForm.courseId"
									clearable
									placeholder="不关联课时"
									style="width: 100%"
									:disabled="!activeCourseSectionId"
								>
									<el-option
										v-for="course in courses"
										:key="course.id"
										:label="course.name"
										:value="course.id"
									/>
								</el-select>
							</el-form-item>
						</el-col>
					</el-row>
				</el-form>
			</div>

			<div class="dialog-section">
				<h4 class="dialog-section-title">问题选择</h4>
				<div class="question-filter">
					<el-form :inline="true" :model="questionBankSearchForm" size="small">
						<el-form-item label="题目名称">
							<el-input
								v-model="questionBankSearchForm.name"
								placeholder="搜索名称"
								clearable
								@keyup.enter="handleSearchQuestionBank"
							/>
						</el-form-item>
						<el-form-item label="类型">
							<el-select
								v-model="questionBankSearchForm.type"
								placeholder="全部"
								clearable
								style="width: 120px"
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
							<el-button type="primary" @click="handleSearchQuestionBank">查询</el-button>
						</el-form-item>
					</el-form>
				</div>

				<el-table
					:data="questionBankData"
					v-loading="questionBankLoading"
					border
					stripe
					max-height="320"
					@selection-change="handleQuestionSelectionChange"
				>
					<el-table-column type="selection" width="55" />
					<el-table-column prop="id" label="ID" width="70" align="center" />
					<el-table-column prop="name" label="题目名称" min-width="180" show-overflow-tooltip />
					<el-table-column prop="type" label="类型" width="100" align="center">
						<template #default="{ row }">
							{{ questionTypeMap[row.type] || '未知' }}
						</template>
					</el-table-column>
					<el-table-column prop="score" label="分值" width="80" align="center" />
					<el-table-column prop="difficulty" label="难度" width="100" align="center">
						<template #default="{ row }">
							<el-tag :type="difficultyTagMap[row.difficulty]" size="small">
								{{ difficultyMap[row.difficulty] || '未知' }}
							</el-tag>
						</template>
					</el-table-column>
				</el-table>

				<div class="question-pagination">
					<el-pagination
						v-model:current-page="questionBankPageParams.pageNo"
						v-model:page-size="questionBankPageParams.pageSize"
						:total="questionBankTotal"
						layout="total, prev, pager, next"
						small
						@current-change="fetchQuestionBank"
					/>
				</div>
			</div>
		</div>

		<template #footer>
			<el-button @click="dialogVisible = false">取消</el-button>
			<el-button
				type="primary"
				:loading="creatingPractice"
				@click="handleCreatePractice"
				style="background: var(--primary); border-color: var(--primary)"
			>
				创建
			</el-button>
		</template>
	</el-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getCourseSections } from '@/api/modules/teaching/CourseSectionAPI'
import { getCourses } from '@/api/modules/teaching/CourseAPI'
import { addPractice, getPracticeTypes, getPracticesByIndex } from '@/api/modules/practice/practice'
import { getQuestionsPage } from '@/api/modules/practice/question'
import { createQuestionIndexes } from '@/api/modules/practice/questionIndex'

const props = defineProps({
	visible: {
		type: Boolean,
		default: false
	},
	courseSectionId: {
		type: [Number, String],
		default: null
	}
})

const emit = defineEmits(['update:visible', 'success'])

const dialogVisible = computed({
	get: () => props.visible,
	set: (val) => emit('update:visible', val)
})

const normalizeId = (value) => {
	const id = Number(value)
	return Number.isFinite(id) && id > 0 ? id : null
}

const presetCourseSectionId = computed(() => normalizeId(props.courseSectionId))
const selectedCourseSectionId = ref(null)
const activeCourseSectionId = computed(() => presetCourseSectionId.value || selectedCourseSectionId.value)

const creatingPractice = ref(false)
const practiceTypes = ref([])
const courseSectionOptions = ref([])
const courses = ref([])

const newPracticeForm = ref({
	name: '',
	questionNum: 0,
	practiceTypeId: null,
	createdAt: '',
	expiredAt: null,
	courseId: null
})

const questionTypeMap = {
	0: '判断题',
	1: '选择题',
	2: '简答题',
	3: '编程题'
}

const difficultyMap = {
	0: '简单',
	1: '中等',
	2: '困难'
}

const difficultyTagMap = {
	0: 'success',
	1: 'warning',
	2: 'danger'
}

const questionBankLoading = ref(false)
const questionBankData = ref([])
const questionBankTotal = ref(0)
const questionBankPageParams = reactive({
	pageNo: 1,
	pageSize: 10
})
const questionBankSearchForm = reactive({
	name: '',
	type: null
})
const selectedQuestionRows = ref([])

const getNowIsoString = () => {
	return new Date().toISOString().slice(0, 19)
}

const resetForm = () => {
	selectedCourseSectionId.value = presetCourseSectionId.value
	newPracticeForm.value = {
		name: '',
		questionNum: 0,
		practiceTypeId: practiceTypes.value[0]?.id ?? null,
		createdAt: getNowIsoString(),
		expiredAt: null,
		courseId: null
	}
	selectedQuestionRows.value = []
	questionBankPageParams.pageNo = 1
	questionBankSearchForm.name = ''
	questionBankSearchForm.type = null
}

const loadPracticeTypes = async () => {
	const res = await getPracticeTypes()
	practiceTypes.value = res.data || []
}

const loadCourseSectionOptions = async () => {
	const res = await getCourseSections()
	courseSectionOptions.value = res.data || []
}

const loadCoursesBySection = async (sectionId) => {
	const res = await getCourses(null, null, sectionId)
	courses.value = res.data || []
}

const fetchQuestionBank = async () => {
	questionBankLoading.value = true
	try {
		const response = await getQuestionsPage(questionBankPageParams, questionBankSearchForm)
		if (response.code === 200) {
			questionBankData.value = response.data?.records || []
			questionBankTotal.value = response.data?.total || 0
		} else {
			questionBankData.value = []
			questionBankTotal.value = 0
			ElMessage.error('获取题库失败')
		}
	} catch (error) {
		ElMessage.error('获取题库失败')
	} finally {
		questionBankLoading.value = false
	}
}

const handleSearchQuestionBank = () => {
	questionBankPageParams.pageNo = 1
	fetchQuestionBank()
}

const handleQuestionSelectionChange = (rows) => {
	selectedQuestionRows.value = rows
	newPracticeForm.value.questionNum = rows.length
}

const initializeDialog = async () => {
	try {
		await loadPracticeTypes()
		if (!presetCourseSectionId.value) {
			await loadCourseSectionOptions()
		}
		resetForm()
		if (activeCourseSectionId.value) {
			await loadCoursesBySection(activeCourseSectionId.value)
		} else {
			courses.value = []
		}
		await fetchQuestionBank()
	} catch (error) {
		ElMessage.error('初始化新建练习弹窗失败')
	}
}

watch(
	() => props.visible,
	(val) => {
		if (val) {
			initializeDialog()
		}
	}
)

watch(activeCourseSectionId, async (newSectionId) => {
	newPracticeForm.value.courseId = null
	if (!newSectionId) {
		courses.value = []
		return
	}
	try {
		await loadCoursesBySection(newSectionId)
	} catch (error) {
		courses.value = []
		ElMessage.error('加载课时列表失败')
	}
})

const handleCreatePractice = async () => {
	if (!activeCourseSectionId.value) {
		ElMessage.warning('请先选择课程章节')
		return
	}
	if (!newPracticeForm.value.name.trim()) {
		ElMessage.warning('请输入练习名称')
		return
	}
	if (newPracticeForm.value.practiceTypeId === null || newPracticeForm.value.practiceTypeId === undefined) {
		ElMessage.warning('请选择练习类型')
		return
	}

	creatingPractice.value = true
	try {
		const selectedCount = selectedQuestionRows.value.length
		const practicePayload = {
			name: newPracticeForm.value.name.trim(),
			questionNum: selectedCount,
			practiceTypeId: newPracticeForm.value.practiceTypeId,
			createdAt: newPracticeForm.value.createdAt,
			expiredAt: newPracticeForm.value.expiredAt
		}

		const practiceDTO = {
			practice: practicePayload,
			practiceIndex: {
				courseSectionId: activeCourseSectionId.value,
				courseId: newPracticeForm.value.courseId || 0
			}
		}

		console.log('创建练习请求参数:', practiceDTO)
		const practiceRes = await addPractice(practiceDTO)
		if (practiceRes?.code && practiceRes.code !== 200) {
			throw new Error(practiceRes.message || '创建练习失败')
		}

		let practiceId =
			practiceRes?.data?.id ??
			practiceRes?.data?.practice?.id ??
			practiceRes?.data

		if (!practiceId) {
			const fallbackRes = await getPracticesByIndex(
				activeCourseSectionId.value,
				newPracticeForm.value.courseId || 0
			)
			const fallbackList = Array.isArray(fallbackRes?.data) ? fallbackRes.data : []
			const matchedPractice = fallbackList
				.filter(item => item?.name === practicePayload.name)
				.filter(item => Number(item?.questionNum ?? -1) === selectedCount)
				.sort((a, b) => Number(b?.id || 0) - Number(a?.id || 0))[0]
			practiceId = matchedPractice?.id || null
		}

		if (!practiceId) {
			if (selectedCount > 0) {
				throw new Error('练习已创建，但未获取到练习ID，题目关联失败，请刷新后重试')
			}
			ElMessage.success('新建练习成功')
			dialogVisible.value = false
			emit('success')
			return
		}

		if (selectedCount > 0) {
			const questionIndexRes = await createQuestionIndexes(
				selectedQuestionRows.value.map(question => ({
					practiceId,
					questionId: question.id
				}))
			)

			if (questionIndexRes?.code && questionIndexRes.code !== 200) {
				throw new Error(questionIndexRes.message || '关联题目失败')
			}
		}

		ElMessage.success('新建练习成功')
		dialogVisible.value = false
		emit('success')
	} catch (error) {
		console.error('新建练习失败:', error)
		ElMessage.error(error?.message || '新建练习失败')
	} finally {
		creatingPractice.value = false
	}
}
</script>

<style scoped lang="scss">
.create-practice-dialog {
	display: flex;
	flex-direction: column;
	gap: 18px;
}

.dialog-section {
	border: 1px solid var(--border-light);
	border-radius: 8px;
	padding: 16px;
	background: var(--bg-white);
}

.dialog-section-title {
	margin: 0 0 14px 0;
	color: var(--text-primary);
	font-size: 15px;
	font-weight: 600;
	border-left: 4px solid var(--primary);
	padding-left: 8px;
}

.question-filter {
	background: var(--bg-primary-light);
	border: 1px solid var(--border-light);
	border-radius: 6px;
	padding: 10px 12px;
	margin-bottom: 12px;
}

.question-pagination {
	margin-top: 12px;
	display: flex;
	justify-content: flex-end;
}

:deep(.el-dialog__header) {
	background: var(--bg-primary-lighter);
	padding: 20px;
}

:deep(.el-dialog__title) {
	color: var(--text-primary);
	font-weight: 600;
}
</style>
