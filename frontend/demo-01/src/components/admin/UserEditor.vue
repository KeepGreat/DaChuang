<template>
	<section class="user-editor-page">
		<div v-if="!isAdmin" class="permission-card">
			<el-result
				icon="warning"
				title="无权限访问"
				sub-title="该页面仅对管理员开放，请使用管理员账号登录。"
			/>
		</div>

		<template v-else>
			<header class="page-header">
				<div>
					<h2>用户信息管理</h2>
					<p>支持用户基本信息查看、编辑与学习评分总览。</p>
				</div>
				<el-button
					type="primary"
					:loading="loadingUsers"
					@click="handleRefresh"
				>
					刷新数据
				</el-button>
			</header>

			<el-row :gutter="16" class="panel-row">
				<el-col :xs="24" :lg="24">
					<el-card shadow="never" class="panel-card">
						<template #header>
							<div class="panel-header">
								<span>用户列表</span>
								<el-tag effect="plain" type="primary">{{ users.length }} 人</el-tag>
							</div>
						</template>

						<el-table
							v-loading="loadingUsers"
							:data="users"
							stripe
							border
							class="user-table"
							empty-text="暂无用户数据"
						>
							<el-table-column prop="id" label="用户 ID" min-width="120" />
							<el-table-column prop="username" label="用户名" min-width="140" />
							<el-table-column label="角色" min-width="120">
								<template #default="{ row }">
									<el-tag :type="roleTagType(row.role)">{{ roleText(row.role) }}</el-tag>
								</template>
							</el-table-column>
							<el-table-column label="操作" min-width="120" fixed="right">
								<template #default="{ row }">
									<el-button type="primary" link @click="openEditDialog(row)">
										编辑
									</el-button>
								</template>
							</el-table-column>
						</el-table>
					</el-card>
				</el-col>
			</el-row>

			<el-dialog
				v-model="editDialogVisible"
				title="编辑用户"
				width="420px"
				destroy-on-close
			>
				<el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="84px">
					<el-form-item label="用户 ID">
						<el-input v-model="editForm.id" disabled />
					</el-form-item>
					<el-form-item label="用户名" prop="username">
						<el-input v-model="editForm.username" placeholder="请输入用户名" />
					</el-form-item>
					<el-form-item label="角色" prop="role">
						<el-select v-model="editForm.role" placeholder="请选择角色" style="width: 100%">
							<el-option label="管理员" value="admin" />
							<el-option label="教师" value="teacher" />
							<el-option label="学生" value="student" />
						</el-select>
					</el-form-item>
					<el-form-item label="新密码" prop="password">
						<el-input
							v-model="editForm.password"
							type="password"
							show-password
							placeholder="留空则不修改密码"
							autocomplete="new-password"
						/>
					</el-form-item>
				</el-form>

				<template #footer>
					<el-button @click="editDialogVisible = false">取消</el-button>
					<el-button type="primary" :loading="submitting" @click="handleSubmitEdit">
						保存修改
					</el-button>
				</template>
			</el-dialog>
		</template>
	</section>
</template>

<script setup>
import { getAllUsers, updateUser } from "@/api/modules/user"
import { useUserStore } from "@/store"
import { ElMessage } from "element-plus"
import { computed, onMounted, reactive, ref } from "vue"

const userStore = useUserStore()
const isAdmin = computed(() => userStore.role === "admin")

const loadingUsers = ref(false)
const submitting = ref(false)

const users = ref([])

const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editForm = reactive({
	id: "",
	username: "",
	role: "student",
	password: ""
})

const editRules = {
	username: [
		{ required: true, message: "请输入用户名", trigger: "blur" },
		{ min: 2, max: 20, message: "长度应在 2 到 20 个字符", trigger: "blur" }
	],
	role: [{ required: true, message: "请选择角色", trigger: "change" }],
	password: [
		{
			validator: (rule, value, callback) => {
				if (!value || value.length >= 6) {
					callback()
				} else {
					callback(new Error("密码至少 6 位，或留空不修改"))
				}
			},
			trigger: "blur"
		}
	]
}

const roleTextMap = {
	admin: "管理员",
	teacher: "教师",
	student: "学生"
}

function roleText(role) {
	return roleTextMap[role] || role || "未知"
}

function roleTagType(role) {
	if (role === "admin") return "danger"
	if (role === "teacher") return "warning"
	return "success"
}

function normalizeUsers(response) {
	const payload = response?.data ?? response

	if (Array.isArray(payload)) {
		return payload
	}

	if (Array.isArray(payload?.records)) {
		return payload.records
	}

	if (Array.isArray(payload?.data)) {
		return payload.data
	}

	if (Array.isArray(payload?.data?.records)) {
		return payload.data.records
	}

	return []
}

async function fetchUsers() {
	loadingUsers.value = true
	try {
		const response = await getAllUsers()
		users.value = normalizeUsers(response)
	} catch (error) {
		console.error("获取用户列表失败:", error)
		users.value = []
		ElMessage.error("获取用户列表失败")
	} finally {
		loadingUsers.value = false
	}
}

function openEditDialog(row) {
	editForm.id = row.id ?? ""
	editForm.username = row.username ?? ""
	editForm.role = row.role ?? "student"
	editForm.password = ""
	editDialogVisible.value = true
}

async function handleSubmitEdit() {
	if (!editFormRef.value) return

	const valid = await editFormRef.value.validate().catch(() => false)
	if (!valid) return

	submitting.value = true
	try {
		const payload = {
			id: editForm.id,
			username: editForm.username,
			role: editForm.role
		}

		if (editForm.password) {
			payload.password = editForm.password
		}

		await updateUser(payload)
		ElMessage.success("用户信息更新成功")
		editDialogVisible.value = false
		await fetchUsers()
	} catch (error) {
		console.error("更新用户失败:", error)
		ElMessage.error("更新用户失败")
	} finally {
		submitting.value = false
	}
}

async function handleRefresh() {
	if (!isAdmin.value) return
	await fetchUsers()
}

onMounted(() => {
	if (!isAdmin.value) {
		ElMessage.warning("该页面仅管理员可访问")
		return
	}
	handleRefresh()
})
</script>

<style scoped lang="scss">
.user-editor-page {
	min-height: 100vh;
	padding: 24px;
	background:
		radial-gradient(circle at 8% 12%, var(--primary-alpha-10) 0%, transparent 38%),
		radial-gradient(circle at 86% 90%, var(--primary-alpha-10) 0%, transparent 34%),
		linear-gradient(180deg, var(--bg-white) 0%, var(--bg-light) 100%);
}

.permission-card {
	max-width: 680px;
	margin: 60px auto;
	border: 1px solid var(--border-light);
	border-radius: 14px;
	background: var(--bg-white);
}

.page-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 16px;
	margin-bottom: 18px;

	h2 {
		margin: 0;
		color: var(--text-primary);
		font-size: 24px;
		font-weight: 700;
	}

	p {
		margin: 8px 0 0;
		color: var(--text-regular);
		font-size: 14px;
	}
}

.panel-row {
	margin-top: 8px;
}

.panel-card {
	border: 1px solid var(--border-light);
	border-radius: 14px;
	background: linear-gradient(180deg, var(--bg-white) 0%, var(--bg-primary-light) 100%);

	:deep(.el-card__header) {
		border-bottom-color: var(--border-light);
		background: var(--primary-alpha-10);
	}
}

.panel-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	color: var(--primary-dark);
	font-weight: 600;
}

.user-table {
	:deep(.el-table__header th) {
		background: var(--bg-primary-light);
		color: var(--primary-dark);
	}
}

@media (max-width: 992px) {
	.user-editor-page {
		padding: 16px;
	}

	.page-header {
		flex-direction: column;
		align-items: flex-start;
	}
}
</style>
