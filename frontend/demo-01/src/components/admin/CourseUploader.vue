<template>
	<el-dialog
		v-model="dialogVisible"
		title="新建课程"
		width="560px"
		destroy-on-close
	>
		<div class="create-course-dialog">
			<div class="dialog-section">
				<h4 class="dialog-section-title">课程字段编辑</h4>
				<el-form :model="newCourseForm" label-width="110px">
					<el-form-item v-if="!presetCourseSectionId" label="课程章节" required>
						<el-select
							v-model="selectedCourseSectionId"
							placeholder="请选择课程章节"
							filterable
							style="width: 100%"
						>
							<el-option
								v-for="section in courseSectionOptions"
								:key="section.id"
								:label="section.name"
								:value="section.id"
							/>
						</el-select>
					</el-form-item>

					<el-form-item label="课程名称" required>
						<el-input v-model="newCourseForm.name" placeholder="请输入课程名称" />
					</el-form-item>

					<el-form-item label="课程描述">
						<el-input
							v-model="newCourseForm.description"
							type="textarea"
							:rows="4"
							placeholder="请输入课程描述"
						/>
					</el-form-item>
				</el-form>
			</div>
		</div>

		<template #footer>
			<el-button @click="dialogVisible = false">取消</el-button>
			<el-button
				type="primary"
				:loading="creatingCourse"
				@click="handleCreateCourse"
				style="background: var(--primary); border-color: var(--primary)"
			>
				新建课时
			</el-button>
		</template>
	</el-dialog>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addCourse } from '@/api/modules/teaching/CourseAPI'
import { getCourseSections } from '@/api/modules/teaching/CourseSectionAPI'

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

const creatingCourse = ref(false)
const courseSectionOptions = ref([])
const newCourseForm = ref({
	name: '',
	description: ''
})

const resetForm = () => {
	selectedCourseSectionId.value = presetCourseSectionId.value
	newCourseForm.value = {
		name: '',
		description: ''
	}
}

const loadCourseSectionOptions = async () => {
	const res = await getCourseSections()
	courseSectionOptions.value = res.data || []
}

const initializeDialog = async () => {
	try {
		if (!presetCourseSectionId.value) {
			await loadCourseSectionOptions()
		}
		resetForm()
	} catch (error) {
		ElMessage.error('初始化新建课程弹窗失败')
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

const handleCreateCourse = async () => {
	if (!activeCourseSectionId.value) {
		ElMessage.warning('请先选择课程章节')
		return
	}

	const courseName = newCourseForm.value.name.trim()
	if (!courseName) {
		ElMessage.warning('请输入课程名称')
		return
	}

	creatingCourse.value = true
	try {
		const payload = {
			name: courseName,
			description: newCourseForm.value.description?.trim() || '',
			sectionId: activeCourseSectionId.value
		}

		const res = await addCourse(payload)
		if (res?.code && res.code !== 200) {
			throw new Error(res.message || '创建课程失败')
		}

		ElMessage.success('新建课程成功')
		dialogVisible.value = false
		emit('success')
	} catch (error) {
		console.error('新建课程失败:', error)
		ElMessage.error('新建课程失败')
	} finally {
		creatingCourse.value = false
	}
}
</script>

<style scoped lang="scss">
.create-course-dialog {
	display: flex;
	flex-direction: column;
	gap: 16px;
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

:deep(.el-dialog__header) {
	background: var(--bg-primary-lighter);
	padding: 20px;
}

:deep(.el-dialog__title) {
	color: var(--text-primary);
	font-weight: 600;
}

:deep(.el-textarea__inner) {
	&:hover {
		border-color: var(--primary-lighter);
	}

	&:focus {
		border-color: var(--primary);
	}
}
</style>
