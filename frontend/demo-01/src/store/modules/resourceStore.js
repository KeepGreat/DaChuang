import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useResourceStore = defineStore('resource', () => {
  // State
  const resources = ref([
    {
      id: '1',
      courseId: '1',
      name: 'Python基础教程.pdf',
      type: 'document', // document, video, audio, image, folder
      size: 15420000, // bytes
      path: '/resources/python-basics.pdf',
      downloadCount: 145,
      description: 'Python基础语法详解教程',
      tags: ['教程', '基础'],
      uploadTime: '2024-12-01 09:00:00',
      uploader: '张老师',
      folderId: null, // null表示根目录
      thumbnail: '/thumbnails/pdf-icon.png'
    },
    {
      id: '2',
      courseId: '1',
      name: 'Python编程视频教程',
      type: 'folder',
      size: 0, // 文件夹大小为0，实际大小需要计算子文件
      path: '/resources/python-videos',
      description: '包含30个Python编程教学视频',
      tags: ['视频', '教程'],
      uploadTime: '2024-12-01 10:00:00',
      uploader: '张老师',
      folderId: null,
      children: [
        {
          id: '2-1',
          name: '第1课：Python简介.mp4',
          type: 'video',
          size: 125829120,
          path: '/resources/python-videos/lesson1.mp4',
          duration: '15:23',
          tags: ['视频'],
          uploadTime: '2024-12-01 10:05:00'
        },
        {
          id: '2-2',
          name: '第2课：变量与数据类型.mp4',
          type: 'video',
          size: 98304000,
          path: '/resources/python-videos/lesson2.mp4',
          duration: '12:45',
          tags: ['视频'],
          uploadTime: '2024-12-01 10:10:00'
        }
      ]
    },
    {
      id: '3',
      courseId: '1',
      name: 'Python代码示例.zip',
      type: 'archive',
      size: 5242880,
      path: '/resources/python-examples.zip',
      downloadCount: 89,
      description: '包含所有章节的Python代码示例',
      tags: ['代码', '示例'],
      uploadTime: '2024-12-01 11:00:00',
      uploader: '李老师',
      folderId: null,
      thumbnail: '/thumbnails/zip-icon.png'
    },
    {
      id: '4',
      courseId: '2',
      name: '数据结构PPT',
      type: 'folder',
      size: 0,
      path: '/resources/data-structure-ppt',
      description: '数据结构课程所有PPT课件',
      tags: ['PPT', '课件'],
      uploadTime: '2024-12-01 14:00:00',
      uploader: '王教授',
      folderId: null,
      children: [
        {
          id: '4-1',
          name: '第1章：绪论.pptx',
          type: 'document',
          size: 2097152,
          path: '/resources/data-structure-ppt/chapter1.pptx',
          tags: ['PPT'],
          uploadTime: '2024-12-01 14:05:00'
        },
        {
          id: '4-2',
          name: '第2章：线性表.pptx',
          type: 'document',
          size: 3145728,
          path: '/resources/data-structure-ppt/chapter2.pptx',
          tags: ['PPT'],
          uploadTime: '2024-12-01 14:10:00'
        },
        {
          id: '4-3',
          name: '第3章：栈和队列.pptx',
          type: 'document',
          size: 2621440,
          path: '/resources/data-structure-ppt/chapter3.pptx',
          tags: ['PPT'],
          uploadTime: '2024-12-01 14:15:00'
        }
      ]
    },
    {
      id: '5',
      courseId: '3',
      name: 'Web开发速查表',
      type: 'image',
      size: 1048576,
      path: '/resources/web-cheatsheet.png',
      downloadCount: 234,
      description: 'HTML、CSS、JavaScript速查表',
      tags: ['速查表', '前端'],
      uploadTime: '2024-12-01 15:00:00',
      uploader: '陈老师',
      folderId: null,
      thumbnail: '/resources/web-cheatsheet.png'
    },
    {
      id: '6',
      courseId: '3',
      name: '项目源码',
      type: 'folder',
      size: 0,
      path: '/resources/project-source',
      description: '课程项目完整源码',
      tags: ['源码', '项目'],
      uploadTime: '2024-12-02 09:00:00',
      uploader: '陈老师',
      folderId: null,
      children: [
        {
          id: '6-1',
          name: '电商项目',
          type: 'folder',
          size: 0,
          children: [
            {
              id: '6-1-1',
              name: 'index.html',
              type: 'document',
              size: 8192,
              path: '/resources/project-source/ecommerce/index.html'
            },
            {
              id: '6-1-2',
              name: 'style.css',
              type: 'document',
              size: 16384,
              path: '/resources/project-source/ecommerce/style.css'
            },
            {
              id: '6-1-3',
              name: 'script.js',
              type: 'document',
              size: 24576,
              path: '/resources/project-source/ecommerce/script.js'
            }
          ]
        }
      ]
    }
  ])

  const currentFolder = ref(null)
  const currentPath = ref([])
  const loading = ref(false)
  const selectedResources = ref([])

  // Getters
  const getResourcesByCourseId = computed(() => {
    return (courseId) => {
      const list = resources.value.filter(r => r.courseId === courseId)
      return list.sort((a, b) => new Date(b.uploadTime) - new Date(a.uploadTime))
    }
  })

  const resourceCount = computed(() => resources.value.length)

  const getResourceById = computed(() => {
    return (resourceId) => {
      const findInArray = (arr) => {
        for (const item of arr) {
          if (item.id === resourceId) return item
          if (item.children) {
            const found = findInArray(item.children)
            if (found) return found
          }
        }
        return null
      }
      return findInArray(resources.value)
    }
  })

  // 根据类型筛选资源
  const getResourcesByType = computed(() => {
    return (type) => {
      if (type === 'all') return resources.value

      const filterByType = (items) => {
        return items.reduce((acc, item) => {
          if (item.type === type) {
            acc.push(item)
          }
          if (item.children) {
            acc.push(...filterByType(item.children))
          }
          return acc
        }, [])
      }

      return filterByType(resources.value)
    }
  })

  // 获取当前文件夹的内容
  const getCurrentFolderContent = computed(() => {
    if (!currentFolder.value) {
      return resources.value.filter(r => !r.folderId)
    }

    const findFolder = (items, folderId) => {
      for (const item of items) {
        if (item.id === folderId) return item
        if (item.children) {
          const found = findFolder(item.children, folderId)
          if (found) return found
        }
      }
      return null
    }

    const folder = findFolder(resources.value, currentFolder.value)
    return folder ? folder.children || [] : []
  })

  // 计算文件夹大小
  const calculateFolderSize = (folder) => {
    if (!folder.children || folder.children.length === 0) return 0

    return folder.children.reduce((total, item) => {
      if (item.type === 'folder') {
        return total + calculateFolderSize(item)
      }
      return total + (item.size || 0)
    }, 0)
  }

  // 格式化文件大小
  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 B'
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  // 获取文件图标
  const getFileIcon = (type, name) => {
    const iconMap = {
      'document': 'document',
      'video': 'video-play',
      'audio': 'headphones',
      'image': 'picture',
      'folder': 'folder',
      'archive': 'files'
    }

    // 根据文件扩展名确定图标
    const ext = name.split('.').pop()?.toLowerCase()
    if (ext === 'pdf') return 'document'
    if (['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'].includes(ext)) return 'document'
    if (['mp4', 'avi', 'mov', 'wmv'].includes(ext)) return 'video-play'
    if (['mp3', 'wav', 'ogg'].includes(ext)) return 'headphones'
    if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'svg'].includes(ext)) return 'picture'
    if (['zip', 'rar', '7z', 'tar', 'gz'].includes(ext)) return 'files'

    return iconMap[type] || 'document'
  }

  // Actions
  const setCurrentFolder = (folderId) => {
    currentFolder.value = folderId
  }

  const navigateToFolder = (folder) => {
    if (folder.type === 'folder') {
      currentFolder.value = folder.id
      currentPath.value.push(folder.name)
    }
  }

  const goBack = () => {
    if (currentPath.value.length > 0) {
      currentPath.value.pop()
      // 这里简化处理，实际应该维护文件夹层级关系
      currentFolder.value = null
    }
  }

  const createFolder = (name, parentId = null) => {
    const newFolder = {
      id: Date.now().toString(),
      name: name,
      type: 'folder',
      size: 0,
      path: parentId ? `/resources/folder-${Date.now()}` : '/resources/folder',
      description: '',
      tags: ['文件夹'],
      uploadTime: new Date().toISOString(),
      uploader: '用户',
      folderId: parentId,
      children: []
    }

    if (parentId) {
      const addToParent = (items) => {
        for (const item of items) {
          if (item.id === parentId) {
            item.children.push(newFolder)
            return true
          }
          if (item.children && addToParent(item.children)) {
            return true
          }
        }
        return false
      }
      addToParent(resources.value)
    } else {
      resources.value.push(newFolder)
    }

    return newFolder
  }

  const uploadResource = (resource, parentId = null) => {
    const newResource = {
      ...resource,
      id: Date.now().toString(),
      uploadTime: new Date().toISOString(),
      downloadCount: 0,
      folderId: parentId
    }

    if (parentId) {
      const addToParent = (items) => {
        for (const item of items) {
          if (item.id === parentId) {
            item.children = item.children || []
            item.children.push(newResource)
            return true
          }
          if (item.children && addToParent(item.children)) {
            return true
          }
        }
        return false
      }
      addToParent(resources.value)
    } else {
      resources.value.unshift(newResource)
    }

    return newResource
  }

  const deleteResource = (resourceId) => {
    const deleteFromArray = (items) => {
      for (let i = 0; i < items.length; i++) {
        if (items[i].id === resourceId) {
          items.splice(i, 1)
          return true
        }
        if (items[i].children && deleteFromArray(items[i].children)) {
          return true
        }
      }
      return false
    }

    return deleteFromArray(resources.value)
  }

  const downloadResource = (resourceId) => {
    const resource = getResourceById.value(resourceId)
    if (resource) {
      resource.downloadCount++
      // 这里应该触发文件下载
      console.log(`Downloading: ${resource.path}`)
    }
  }

  const searchResources = (searchTerm, courseId = null, folderId = null) => {
    const term = searchTerm.toLowerCase().trim()

    const searchInArray = (items) => {
      return items.reduce((acc, item) => {
        if (item.name.toLowerCase().includes(term) ||
            (item.description && item.description.toLowerCase().includes(term)) ||
            item.tags.some(tag => tag.toLowerCase().includes(term))) {
          acc.push(item)
        }
        if (item.children) {
          acc.push(...searchInArray(item.children))
        }
        return acc
      }, [])
    }

    let filtered = resources.value
    if (courseId) {
      filtered = filtered.filter(r => r.courseId === courseId)
    }

    if (!term) return filtered
    return searchInArray(filtered)
  }

  const toggleResourceSelection = (resourceId) => {
    const index = selectedResources.value.indexOf(resourceId)
    if (index === -1) {
      selectedResources.value.push(resourceId)
    } else {
      selectedResources.value.splice(index, 1)
    }
  }

  const clearSelection = () => {
    selectedResources.value = []
  }

  const selectAll = () => {
    const getAllIds = (items) => {
      return items.reduce((acc, item) => {
        acc.push(item.id)
        if (item.children) {
          acc.push(...getAllIds(item.children))
        }
        return acc
      }, [])
    }

    selectedResources.value = getAllIds(getCurrentFolderContent.value)
  }

  // 清空所有资源
  const clearAll = () => {
    resources.value = []
    currentFolder.value = null
    currentPath.value = []
    selectedResources.value = []
  }

  return {
    // State
    resources,
    currentFolder,
    currentPath,
    loading,
    selectedResources,

    // Getters
    getResourcesByCourseId,
    resourceCount,
    getResourceById,
    getResourcesByType,
    getCurrentFolderContent,

    // Actions
    setCurrentFolder,
    navigateToFolder,
    goBack,
    createFolder,
    uploadResource,
    deleteResource,
    downloadResource,
    searchResources,
    toggleResourceSelection,
    clearSelection,
    selectAll,
    clearAll,

    // Utils
    calculateFolderSize,
    formatFileSize,
    getFileIcon
  }
}, {
  persist: {
    key: 'resource-store',
    storage: localStorage,
    paths: ['resources']
  }
})