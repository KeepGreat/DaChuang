<template>
  <div>
    <form @submit.prevent="uploadFile">
      <div>
        <label>文件名称:</label>
        <input v-model="fileContent.name" type="text" />
      </div>
      <div>
        <label>文件类型:</label>
        <input v-model="fileContent.type" type="text" />
      </div>
      <div>
        <label>文件大小:</label>
        <input v-model="fileContent.size" type="text" />
      </div>
      <div>
        <label>文件对应的资料ID:</label>
        <input v-model="fileContent.matId" type="text" />
      </div>
      <div>
        <label>选择文件:</label>
        <input type="file" @change="handleFileChange" ref="fileInputRef" />
      </div>
      <div>
        <p>填写material信息</p>
        <div>
          <label>资料种类:</label>
          <input type="text" v-model="material.type" />
        </div>
        <div>
          <label>资料描述:</label>
          <input type="text" v-model="material.description" />
        </div>
        <div>
          <label>资料对应的课程ID:</label>
          <input type="text" v-model="material.courseId" />
        </div>
        <div>
          <p>创建时间: {{ material.createdTime }}</p>
        </div>
        <div>
          <p>更新时间: {{ material.updatedTime }}</p>
        </div>
      </div>
      <button type="submit" :disabled="uploading">
        {{ uploading ? "上传中..." : "上传文件" }}
      </button>
    </form>
    <div v-if="uploadMessage">{{ uploadMessage }}</div>
  </div>
  <hr />
  <div>
    <div>
      <label>文件名称：</label>
      <input type="text" v-model="downloadFileName" />
    </div>
    <div>
      <button type="submit" @click="downloadFile">下载文件</button>
    </div>
    <div v-if="downloadMessage">{{ downloadMessage }}</div>
  </div>
  <hr />
  <p>删除文件和相关资料记录</p>
  <div>
    <div>
      <label>文件名称：</label>
      <input type="text" v-model="deleteFileName" />
    </div>
    <div>
      <label>文件记录id：</label>
      <input type="text" v-model="deleteFileId" />
    </div>
    <div>
      <label>文件对应的资料ID：</label>
      <input type="text" v-model="deleteMatId" />
    </div>
    <div>
      <button @click="deleteFile">删除文件</button>
    </div>
    <div v-if="deleteMessage">{{ deleteMessage }}</div>
  </div>
  <hr />
  <div>
    <div>
      <label>文件名称：</label>
      <input type="text" v-model="displayFileName" />
    </div>
    <div>
      <button @click="displayFile">展示文件</button>
    </div>
    <div v-if="displayMessage">{{ displayMessage }}</div>

    <!-- 根据文件类型展示不同的组件 -->
    <div v-if="fileUrl" class="file-display-container">
      <!-- 图片展示 -->
      <img
        v-if="fileType.startsWith('image/')"
        :src="fileUrl"
        alt="展示图片"
        class="display-image"
      />

      <!-- PDF展示 -->
      <div v-else-if="fileType.startsWith('application/pdf')" class="pdf-container">
        <div v-if="pdfLoadError" class="pdf-error">
          <p>PDF文件加载失败，可能是由于浏览器安全限制或文件格式不支持。</p>
          <a :href="fileUrl" :download="displayFileName">下载PDF文件</a>
        </div>
        <iframe
          :src="fileUrl"
          class="pdf-iframe"
          @error="handlePdfError"
          @load="handlePdfLoad"
        ></iframe>
      </div>

      <!-- PPT展示 (简化处理，实际可能需要更复杂的处理) -->
      <div
        v-else-if="
          fileType.includes('powerpoint') ||
          fileType.includes('pptx') ||
          fileType.includes('ppt')
        "
        class="ppt-container"
      >
        <p>PPT文件展示：{{ displayFileName }}</p>
        <p>提示：由于浏览器安全限制，PPT文件无法直接在页面中预览，请下载后查看。</p>
        <a :href="fileUrl" :download="displayFileName">下载PPT文件</a>
      </div>

      <!-- 其他文件类型 -->
      <div v-else class="other-file-container">
        <p>文件类型：{{ fileType }}</p>
        <p>文件名：{{ displayFileName }}</p>
        <a :href="fileUrl" :download="displayFileName">下载文件</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { addMaterial } from "@/api";
import axios from "axios";
import { reactive, ref } from "vue";

const fileInputRef = ref(null); // 用于引用DOM元素
const selectedFile = ref(null); // 用于存储选中的文件
const uploading = ref(false);
const uploadMessage = ref(""); // 用于存储上传文件的消息
const material = ref({
  type: "",
  description: "",
  createdTime: "",
  updatedTime: "",
  courseId: null,
});

const downloadMessage = ref(""); // 用于存储下载文件的消息
const downloadFileName = ref("");

const deleteMessage = ref(""); // 用于存储删除文件的消息
const deleteFileName = ref("");
const deleteFileId = ref("");
const deleteMatId = ref("");

// 文件展示相关的变量 - 独立命名不与其他功能复用
const displayMessage = ref(""); // 用于存储文件展示的消息
const displayFileName = ref(""); // 用于存储要展示的文件名
const fileType = ref(""); // 用于存储文件类型
const fileUrl = ref(""); // 用于存储文件URL
const pdfLoadError = ref(false); // 用于标识PDF加载是否出错

const fileContent = reactive({
  id: null,
  type: "",
  name: "",
  size: 0,
  matId: 1,
});

const handleFileChange = (event) => {
  selectedFile.value = event.target.files[0];
  fileContent.size = selectedFile.value.size;
  fileContent.type = selectedFile.value.type;
  fileContent.name = selectedFile.value.name;
  console.log(fileContent);
  console.log("选中的文件:", selectedFile.value);
};

const uploadFile = async () => {
  if (!selectedFile.value) {
    uploadMessage.value = "请选择文件";
    return;
  }

  uploading.value = true;
  uploadMessage.value = "";

  try {
    const formData = new FormData();

    // 添加文件内容信息（转换为JSON字符串）
    formData.append("fileContent", JSON.stringify(fileContent));

    // 添加文件
    formData.append("file", selectedFile.value);

    const response = await axios.post("http://localhost:80/api/teaching/file", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    material.value.createdTime = getCurrentTime();
    material.value.updatedTime = getCurrentTime();
    await addMaterial(material.value);

    uploadMessage.value = response.data;
  } catch (error) {
    console.error("上传失败:", error);
    uploadMessage.value = "上传失败: " + (error.response?.data || error.message);
  } finally {
    uploading.value = false;
  }
};

const downloadFile = async () => {
  if (!downloadFileName.value) {
    downloadMessage.value = "请输入文件名";
    return;
  }

  try {
    const response = await axios.get(
      "http://localhost:80/api/teaching/file/download?fileName=" + downloadFileName.value,
      {
        responseType: "blob",
      }
    );

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", downloadFileName.value);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    downloadMessage.value = "文件下载成功";
  } catch (error) {
    console.error("下载失败:", error);
    downloadMessage.value = "下载失败: " + (error.response?.data || error.message);
  }
};

const deleteFile = async () => {
  if (!deleteFileName.value) {
    deleteMessage.value = "请输入文件名";
    return;
  }

  try {
    const response = await axios.delete(
      "http://localhost:80/api/teaching/file/" +
        deleteFileId.value +
        "?fileName=" +
        deleteFileName.value
    );
    await axios.delete("http://localhost:80/api/teaching/material/" + deleteMatId.value);
    deleteMessage.value = response.data;
  } catch (error) {
    console.error("删除失败:", error);
    deleteMessage.value = "删除失败: " + (error.response?.data || error.message);
  }
};

const displayFile = async () => {
  if (!displayFileName.value) {
    displayMessage.value = "请输入文件名";
    return;
  }

  try {
    displayMessage.value = "正在获取文件...";
    pdfLoadError.value = false; // 重置PDF加载错误状态
    const response = await axios.get(
      "http://localhost:80/api/teaching/file/download?fileName=" + displayFileName.value,
      {
        responseType: "blob",
      }
    );

    // 获取文件类型
    fileType.value = response.headers["content-type"] || "";

    // 创建文件URL用于展示
    const blob = new Blob([response.data], { type: fileType.value });
    fileUrl.value = window.URL.createObjectURL(blob);

    displayMessage.value = "文件加载成功";
  } catch (error) {
    console.error("文件展示失败:", error);
    displayMessage.value = "文件展示失败: " + (error.response?.data || error.message);
    fileUrl.value = "";
    fileType.value = "";
    pdfLoadError.value = false;
  }
};

// 处理PDF加载错误
const handlePdfError = () => {
  console.error("PDF加载失败");
  pdfLoadError.value = true;
};

// 处理PDF加载成功
const handlePdfLoad = () => {
  pdfLoadError.value = false;
};

const getCurrentTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = (now.getMonth() + 1).toString().padStart(2, "0");
  const day = now.getDate().toString().padStart(2, "0");
  const hours = now.getHours().toString().padStart(2, "0");
  const minutes = now.getMinutes().toString().padStart(2, "0");
  const seconds = now.getSeconds().toString().padStart(2, "0");

  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
};

// 组件卸载时释放URL资源
import { onUnmounted } from "vue";
onUnmounted(() => {
  if (fileUrl.value) {
    window.URL.revokeObjectURL(fileUrl.value);
  }
});
</script>

<style scoped>
.file-display-container {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.display-image {
  max-width: 100%;
  max-height: 500px;
}

.pdf-container {
  width: 100%;
  height: 600px;
  position: relative;
}

.pdf-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.pdf-error {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #f8f9fa;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 5px;
  z-index: 10;
}

.pdf-error p {
  margin-bottom: 15px;
  color: #666;
  text-align: center;
}

.pdf-error a {
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
}

.pdf-error a:hover {
  background-color: #66b1ff;
}

.ppt-container,
.other-file-container {
  padding: 20px;
  text-align: center;
}

.ppt-container a,
.other-file-container a {
  display: inline-block;
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
}

.ppt-container a:hover,
.other-file-container a:hover {
  background-color: #66b1ff;
}
</style>
