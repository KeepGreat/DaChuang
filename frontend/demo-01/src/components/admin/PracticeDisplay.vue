<template>
	<div class="practice-display">
		<el-card class="filter-card" shadow="never">
			<template #header>
				<div class="card-header">
					<span class="card-title">练习筛选</span>
				</div>
			</template>

			<el-form :model="filterForm" label-width="120px">
				<el-row :gutter="16">
					<el-col :xs="24" :sm="12" :md="8">
						<el-form-item label="课程">
							<el-select
								v-model="filterForm.courseSectionId"
								placeholder="请选择课程"
								clearable
								filterable
								style="width: 100%"
							>
								<el-option
									v-for="section in courseSections"
									:key="section.id"
									:label="section.name"
									:value="section.id"
								/>
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :xs="24" :sm="12" :md="8">
						<el-form-item label="课时">
							<el-select
								v-model="filterForm.courseId"
								placeholder="请选择课时"
								clearable
								filterable
								style="width: 100%"
							>
								<el-option
									v-for="course in availableCourses"
									:key="course.id"
									:label="course.name"
									:value="course.id"
								/>
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :xs="24" :sm="12" :md="8">
						<el-form-item label="练习类型">
							<el-select
								v-model="filterForm.practiceTypeId"
								placeholder="请选择练习类型"
								clearable
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

					<el-col :xs="24" :sm="12" :md="12">
						<el-form-item label="创建时间范围">
							<el-date-picker
								v-model="filterForm.createdAtRange"
								type="datetimerange"
								start-placeholder="开始时间"
								end-placeholder="结束时间"
								value-format="YYYY-MM-DDTHH:mm:ss"
								style="width: 100%"
							/>
						</el-form-item>
					</el-col>

					<el-col :xs="24" :sm="12" :md="12">
						<el-form-item label="过期时间范围">
							<el-date-picker
								v-model="filterForm.expiredAtRange"
								type="datetimerange"
								start-placeholder="开始时间"
								end-placeholder="结束时间"
								value-format="YYYY-MM-DDTHH:mm:ss"
								style="width: 100%"
							/>
						</el-form-item>
					</el-col>
				</el-row>

				<div class="filter-actions">
					<el-button
						type="primary"
						:loading="tableLoading"
						@click="handleSearch"
					>
						查询
					</el-button>
					<el-button @click="handleReset">重置</el-button>
				</div>
			</el-form>
		</el-card>

		<el-card class="table-card" shadow="never">
			<template #header>
				<div class="card-header">
					<div class="header-left">
						<span class="card-title">练习列表</span>
						<span class="card-subtitle">共 {{ sortedTableData.length }} 条</span>
					</div>
					<div class="header-actions">
						<el-button type="primary" @click="handleOpenUploader">新建练习</el-button>
						<el-button @click="handleManageQuestionBank">管理题库</el-button>
						<el-button type="success" @click="handleReviewPractice">批改练习</el-button>
					</div>
				</div>
			</template>

			<el-table
				v-loading="tableLoading"
				:data="pagedTableData"
				border
				stripe
				@sort-change="handleSortChange"
				style="width: 100%"
			>
				<el-table-column prop="name" label="练习名称" min-width="160" show-overflow-tooltip sortable="custom" />
				<el-table-column prop="questionNum" label="问题数量" width="100" align="center" sortable="custom" />
				<el-table-column prop="practiceTypeName" label="练习类型" width="140" align="center" sortable="custom" />
				<el-table-column prop="createdAt" label="创建时间" min-width="180" sortable="custom" />
				<el-table-column prop="expiredAt" label="过期时间" min-width="180" sortable="custom" />
				<el-table-column label="课程与课时" min-width="320">
					<template #default="{ row }">
						<div class="index-list">
							<el-tag
								v-for="item in row.indexDisplayList"
								:key="item"
								class="index-tag"
								type="info"
								effect="plain"
							>
								{{ item }}
							</el-tag>
							<span v-if="!row.indexDisplayList.length" class="empty-text">无关联</span>
						</div>
					</template>
				</el-table-column>
				<el-table-column label="操作" width="140" align="center" fixed="right">
					<template #default="{ row }">
						<el-button link type="primary" @click="handleEditPractice(row)">编辑</el-button>
						<el-button link type="danger" @click="handleDeletePractice(row)">删除</el-button>
					</template>
				</el-table-column>
			</el-table>

			<div class="pagination-wrap">
				<el-pagination
					v-model:current-page="pagination.currentPage"
					v-model:page-size="pagination.pageSize"
					:page-sizes="[10, 20, 50, 100]"
					:total="sortedTableData.length"
					layout="total, sizes, prev, pager, next, jumper"
					@size-change="handlePageSizeChange"
					@current-change="handleCurrentPageChange"
				/>
			</div>
		</el-card>

		<PracticeUploader
			v-model:visible="uploaderVisible"
			:course-section-id="filterForm.courseSectionId"
			@success="handleUploaderSuccess"
		/>
	</div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getPracticeIndexes } from '@/api/modules/practice/practiceIndex'
import { deleteQuestionIndexByIds, getQuestionIndexes } from '@/api/modules/practice/questionIndex'
import { deletePracticeById, getPractices, getPracticeTypes } from '@/api/modules/practice/practice'
import { getCourseSections } from '@/api/modules/teaching/CourseSectionAPI'
import { getCourses } from '@/api/modules/teaching/CourseAPI'
import { teacherStore } from '@/store/modules/teacher/teacherStore'
import PracticeUploader from './PracticeUploader.vue'

const tableLoading = ref(false)
const uploaderVisible = ref(false)
const router = useRouter()
const store = teacherStore()

const filterForm = reactive({
	courseSectionId: null,
	courseId: null,
	practiceTypeId: null,
	createdAtRange: [],
	expiredAtRange: []
})

const practiceTypes = ref([])
const courseSections = ref([])
const courses = ref([])
const tableData = ref([])
const sortState = reactive({
	prop: '',
	order: ''
})
const pagination = reactive({
	currentPage: 1,
	pageSize: 10
})

const courseSectionNameMap = computed(() => {
	const map = new Map()
	courseSections.value.forEach((item) => {
		map.set(item.id, item.name)
	})
	return map
})

const courseNameMap = computed(() => {
	const map = new Map()
	courses.value.forEach((item) => {
		map.set(item.id, item.name)
	})
	return map
})

const practiceTypeNameMap = computed(() => {
	const map = new Map()
	practiceTypes.value.forEach((item) => {
		map.set(item.id, item.name)
	})
	return map
})

const availableCourses = computed(() => {
	if (!filterForm.courseSectionId) {
		return courses.value
	}
	return courses.value.filter((course) => course.sectionId === filterForm.courseSectionId)
})

const sortedTableData = computed(() => {
	const cloned = [...tableData.value]
	if (!sortState.prop || !sortState.order) {
		return cloned
	}

	const direction = sortState.order === 'ascending' ? 1 : -1
	cloned.sort((a, b) => {
		const aValue = getSortableValue(a, sortState.prop)
		const bValue = getSortableValue(b, sortState.prop)

		if (aValue === bValue) {
			return 0
		}
		if (aValue > bValue) {
			return direction
		}
		return -direction
	})

	return cloned
})

const pagedTableData = computed(() => {
	const start = (pagination.currentPage - 1) * pagination.pageSize
	const end = start + pagination.pageSize
	return sortedTableData.value.slice(start, end)
})

const normalizeList = (res) => {
	if (res?.code !== 200 || !Array.isArray(res.data)) {
		return []
	}
	return res.data
}

const formatDateTime = (value) => {
	if (!value) {
		return '-'
	}
	return String(value).replace('T', ' ')
}

const getSortableValue = (row, prop) => {
	if (prop === 'createdAt' || prop === 'expiredAt') {
		if (!row[prop] || row[prop] === '-') {
			return 0
		}
		return new Date(row[prop]).getTime() || 0
	}

	const value = row[prop]
	if (value === null || value === undefined) {
		return ''
	}
	if (typeof value === 'number') {
		return value
	}
	return String(value)
}

const buildIndexDisplay = (index) => {
	const sectionName = courseSectionNameMap.value.get(index.courseSectionId) || `课程#${index.courseSectionId}`
	const hasCourse = Number(index.courseId) > 0
	const courseName = hasCourse
		? (courseNameMap.value.get(index.courseId) || `课时#${index.courseId}`)
		: '课程通用'
	return `${sectionName} / ${courseName}`
}

const loadBaseOptions = async () => {
	const [typeRes, sectionRes, courseRes] = await Promise.all([
		getPracticeTypes(),
		getCourseSections(),
		getCourses()
	])

	practiceTypes.value = normalizeList(typeRes)
	courseSections.value = normalizeList(sectionRes)
	courses.value = normalizeList(courseRes)
}

const queryPractices = async () => {
	const createdAtStart = filterForm.createdAtRange?.[0]
	const createdAtEnd = filterForm.createdAtRange?.[1]
	const expiredAtStart = filterForm.expiredAtRange?.[0]
	const expiredAtEnd = filterForm.expiredAtRange?.[1]

	const [practiceRes, indexRes] = await Promise.all([
		getPractices(
			undefined,
			undefined,
			filterForm.practiceTypeId || undefined,
			createdAtStart,
			createdAtEnd,
			expiredAtStart,
			expiredAtEnd
		),
		getPracticeIndexes({
			courseSectionId: filterForm.courseSectionId || undefined,
			courseId: filterForm.courseId || undefined
		})
	])

	const practiceList = normalizeList(practiceRes)
	const filteredIndexList = normalizeList(indexRes)

	const hasIndexFilter = Boolean(filterForm.courseSectionId || filterForm.courseId)
	const filteredPracticeIdSet = new Set(filteredIndexList.map((item) => item.practiceId))

	let filteredPractices = practiceList
	if (hasIndexFilter) {
		filteredPractices = practiceList.filter((practice) => filteredPracticeIdSet.has(practice.id))
	}

	const allIndexRes = await getPracticeIndexes()
	const allIndexList = normalizeList(allIndexRes)
	const indexMap = new Map()

	allIndexList.forEach((item) => {
		if (!indexMap.has(item.practiceId)) {
			indexMap.set(item.practiceId, [])
		}
		indexMap.get(item.practiceId).push(item)
	})

	tableData.value = filteredPractices.map((practice) => {
		const relatedIndexes = indexMap.get(practice.id) || []
		return {
			...practice,
			practiceTypeName: practiceTypeNameMap.value.get(practice.practiceTypeId) || `类型#${practice.practiceTypeId}`,
			createdAt: formatDateTime(practice.createdAt),
			expiredAt: formatDateTime(practice.expiredAt),
			indexDisplayList: relatedIndexes.map((index) => buildIndexDisplay(index))
		}
	})
}

const handleSearch = async () => {
	tableLoading.value = true
	try {
		await queryPractices()
		pagination.currentPage = 1
	} catch (error) {
		ElMessage.error('查询练习失败，请稍后重试')
	} finally {
		tableLoading.value = false
	}
}

const handleSortChange = ({ prop, order }) => {
	sortState.prop = prop || ''
	sortState.order = order || ''
	pagination.currentPage = 1
}

const handlePageSizeChange = (size) => {
	pagination.pageSize = size
	pagination.currentPage = 1
}

const handleCurrentPageChange = (page) => {
	pagination.currentPage = page
}

const handleEditPractice = (row) => {
	store.choosePractice(row.id)
	router.push('/teacher/pe')
}

const handleDeletePractice = async (row) => {
	if (!row?.id) {
		ElMessage.warning('未获取到练习ID，无法删除')
		return
	}

	try {
		await ElMessageBox.confirm(
			'确定要删除该练习吗？该操作会尝试清理关联的题目索引。',
			'删除确认',
			{
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}
		)

		tableLoading.value = true

		const deletePracticeRes = await deletePracticeById(row.id)
		if (deletePracticeRes?.code && deletePracticeRes.code !== 200) {
			throw new Error(deletePracticeRes.message || '删除练习失败')
		}

		const questionIndexRes = await getQuestionIndexes({ practiceId: row.id })
		if (questionIndexRes?.code && questionIndexRes.code !== 200) {
			throw new Error(questionIndexRes.message || '查询题目索引失败')
		}

		const questionIndexIds = normalizeList(questionIndexRes)
			.map((item) => Number(item.id))
			.filter((id) => Number.isFinite(id) && id > 0)

		if (questionIndexIds.length > 0) {
			const deleteQuestionIndexRes = await deleteQuestionIndexByIds(questionIndexIds)
			if (deleteQuestionIndexRes?.code && deleteQuestionIndexRes.code !== 200) {
				throw new Error(deleteQuestionIndexRes.message || '删除题目索引失败')
			}
		}

		await queryPractices()
		ElMessage.success('练习删除成功')
	} catch (error) {
		if (error !== 'cancel' && error !== 'close') {
			console.error('删除练习失败:', error)
			ElMessage.error(error?.message || '删除练习失败，请稍后重试')
		}
	} finally {
		tableLoading.value = false
	}
}

const handleOpenUploader = () => {
	uploaderVisible.value = true
}

const handleManageQuestionBank = () => {
	router.push('/teacher/qe')
}

const handleReviewPractice = () => {
	router.push('/teacher/uae')
}

const handleUploaderSuccess = async () => {
	await handleSearch()
}

const handleReset = () => {
	filterForm.courseSectionId = null
	filterForm.courseId = null
	filterForm.practiceTypeId = null
	filterForm.createdAtRange = []
	filterForm.expiredAtRange = []
	handleSearch()
}

watch(
	() => filterForm.courseSectionId,
	() => {
		if (!filterForm.courseSectionId) {
			return
		}
		const hasSelectedCourse = availableCourses.value.some((item) => item.id === filterForm.courseId)
		if (!hasSelectedCourse) {
			filterForm.courseId = null
		}
	}
)

onMounted(async () => {
	tableLoading.value = true
	try {
		await loadBaseOptions()
		await queryPractices()
	} catch (error) {
		ElMessage.error('初始化练习展示页面失败')
	} finally {
		tableLoading.value = false
	}
})
</script>

<style scoped>
.practice-display {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

.filter-card,
.table-card {
	border-color: var(--border-primary-lighter);
	background: var(--bg-white);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.header-actions {
	display: flex;
	gap: 8px;
}

.header-left {
	display: flex;
	align-items: center;
	gap: 8px;
}

.card-title {
	font-size: 16px;
	font-weight: 600;
	color: var(--text-primary);
}

.card-subtitle {
	font-size: 13px;
	color: var(--text-regular);
}

.filter-actions {
	display: flex;
	justify-content: flex-end;
	gap: 8px;
	margin-top: 8px;
}

.index-list {
	display: flex;
	flex-wrap: wrap;
	gap: 6px;
}

.pagination-wrap {
	display: flex;
	justify-content: flex-end;
	margin-top: 16px;
}

.index-tag {
	border-color: var(--border-primary-lighter);
	color: var(--primary-dark);
	background: var(--bg-primary-light);
}

.empty-text {
	color: var(--text-placeholder);
}

:deep(.el-card__header) {
	border-bottom: 1px solid var(--border-light);
	background: var(--bg-primary-light);
}

:deep(.el-button--primary:hover) {
	background: var(--primary-hover);
	border-color: var(--primary-hover);
}

:deep(.el-table th.el-table__cell) {
	background: var(--bg-primary-light);
	color: var(--text-primary);
}
</style>
