import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useDiscussionStore = defineStore('discussion', () => {
  // State
  const discussions = ref([
    {
      id: '1',
      courseId: '1',
      title: 'Python安装遇到的问题求助',
      content: '我在Windows上安装Python 3.10时遇到了问题，安装程序总是报错，有同学遇到过类似问题吗？',
      author: {
        id: 'student1',
        name: '小明',
        avatar: '/avatars/student1.jpg',
        role: 'student'
      },
      publishTime: '2024-12-02 14:30:00',
      lastReplyTime: '2024-12-02 16:45:00',
      replyCount: 5,
      viewCount: 23,
      isTop: false,
      isLocked: false,
      tags: ['求助', 'Python安装'],
      attachments: [
        {
          name: 'error-screenshot.png',
          url: '/files/error.png',
          size: '245KB'
        }
      ]
    },
    {
      id: '2',
      courseId: '1',
      title: '【置顶】Python学习资源分享',
      content: '这里分享一些优质的Python学习资源，包括文档、教程、视频等。大家也可以补充自己的资源。',
      author: {
        id: 'teacher1',
        name: '张老师',
        avatar: '/avatars/teacher1.jpg',
        role: 'teacher'
      },
      publishTime: '2024-12-01 09:00:00',
      lastReplyTime: '2024-12-02 10:30:00',
      replyCount: 12,
      viewCount: 156,
      isTop: true,
      isLocked: false,
      tags: ['资源', '置顶'],
      attachments: []
    },
    {
      id: '3',
      courseId: '1',
      title: '讨论：Python变量命名规范',
      content: '大家觉得Python的变量命名应该遵循哪些规范？PEP 8的标准在实际项目中大家都严格执行吗？',
      author: {
        id: 'student2',
        name: '李华',
        avatar: '/avatars/student2.jpg',
        role: 'student'
      },
      publishTime: '2024-12-01 16:20:00',
      lastReplyTime: '2024-12-02 15:10:00',
      replyCount: 8,
      viewCount: 67,
      isTop: false,
      isLocked: false,
      tags: ['讨论', '规范'],
      attachments: []
    },
    {
      id: '4',
      courseId: '2',
      title: '二叉树遍历算法对比',
      content: '最近在学习二叉树的三种遍历方式（前序、中序、后序），想和大家讨论一下各自的优缺点和适用场景。',
      author: {
        id: 'student3',
        name: '王芳',
        avatar: '/avatars/student3.jpg',
        role: 'student'
      },
      publishTime: '2024-12-01 11:00:00',
      lastReplyTime: '2024-12-02 09:30:00',
      replyCount: 15,
      viewCount: 89,
      isTop: false,
      isLocked: false,
      tags: ['数据结构', '算法'],
      attachments: [
        {
          name: 'traversal-comparison.pdf',
          url: '/files/traversal.pdf',
          size: '567KB'
        }
      ]
    },
    {
      id: '5',
      courseId: '3',
      title: 'CSS Grid vs Flexbox：什么时候使用哪个？',
      content: '作为前端新手，我很好奇CSS Grid和Flexbox的区别。在实际项目中，大家是如何选择使用哪一个的？',
      author: {
        id: 'student4',
        name: '赵强',
        avatar: '/avatars/student4.jpg',
        role: 'student'
      },
      publishTime: '2024-12-02 10:00:00',
      lastReplyTime: '2024-12-02 14:20:00',
      replyCount: 9,
      viewCount: 45,
      isTop: false,
      isLocked: false,
      tags: ['CSS', '前端'],
      attachments: []
    }
  ])

  const currentDiscussion = ref(null)
  const replies = ref({
    '1': [
      {
        id: '1-1',
        authorId: 'student5',
        authorName: '小红',
        authorAvatar: '/avatars/student5.jpg',
        authorRole: 'student',
        content: '我也遇到了同样的问题！后来发现是系统环境变量的问题。你检查一下PATH设置了吗？',
        publishTime: '2024-12-02 15:00:00',
        likeCount: 3
      },
      {
        id: '1-2',
        authorId: 'teacher1',
        authorName: '张老师',
        authorAvatar: '/avatars/teacher1.jpg',
        authorRole: 'teacher',
        content: '请尝试使用Anaconda，它会自动管理Python环境，避免很多安装问题。',
        publishTime: '2024-12-02 15:30:00',
        likeCount: 8
      },
      {
        id: '1-3',
        authorId: 'student1',
        authorName: '小明',
        authorAvatar: '/avatars/student1.jpg',
        authorRole: 'student',
        content: '谢谢老师和同学的建议！我试试Anaconda。',
        publishTime: '2024-12-02 16:00:00',
        likeCount: 1
      }
    ],
    '2': [
      {
        id: '2-1',
        authorId: 'student6',
        authorName: '小刚',
        authorAvatar: '/avatars/student6.jpg',
        authorRole: 'student',
        content: '推荐廖雪峰的Python教程，讲解很清晰。',
        publishTime: '2024-12-01 10:00:00',
        likeCount: 12
      }
    ]
  })
  const loading = ref(false)

  // Getters
  const getDiscussionsByCourseId = computed(() => {
    return (courseId) => {
      const list = discussions.value.filter(d => d.courseId === courseId)
      // 置顶的放在最前面
      return list.sort((a, b) => {
        if (a.isTop && !b.isTop) return -1
        if (!a.isTop && b.isTop) return 1
        return new Date(b.lastReplyTime) - new Date(a.lastReplyTime)
      })
    }
  })

  const discussionCount = computed(() => discussions.value.length)

  const getDiscussionById = computed(() => {
    return (discussionId) => discussions.value.find(d => d.id === discussionId)
  })

  // 根据标签筛选讨论
  const getDiscussionsByTag = computed(() => {
    return (tag) => {
      if (tag === 'all') return discussions.value
      return discussions.value.filter(d => d.tags.includes(tag))
    }
  })

  // 获取热门讨论（根据回复数和查看数）
  const getHotDiscussions = computed(() => {
    return (courseId) => {
      const list = courseId
        ? discussions.value.filter(d => d.courseId === courseId)
        : discussions.value

      return list
        .filter(d => !d.isLocked)
        .sort((a, b) => {
          const aScore = a.replyCount * 10 + a.viewCount
          const bScore = b.replyCount * 10 + b.viewCount
          return bScore - aScore
        })
        .slice(0, 5)
    }
  })

  // 获取我参与的讨论
  const getMyDiscussions = computed(() => {
    const currentUserId = 'student1' // 应该从用户信息中获取
    return discussions.value.filter(d => {
      const userReplies = replies.value[d.id] || []
      return d.author.id === currentUserId ||
        userReplies.some(r => r.authorId === currentUserId)
    })
  })

  // Actions
  const setCurrentDiscussion = (discussion) => {
    currentDiscussion.value = discussion
  }

  const createDiscussion = (discussion) => {
    const newDiscussion = {
      ...discussion,
      id: Date.now().toString(),
      publishTime: new Date().toISOString(),
      lastReplyTime: new Date().toISOString(),
      replyCount: 0,
      viewCount: 0,
      isTop: false,
      isLocked: false
    }
    discussions.value.unshift(newDiscussion)
    return newDiscussion
  }

  const updateDiscussion = (discussionId, updates) => {
    const index = discussions.value.findIndex(d => d.id === discussionId)
    if (index !== -1) {
      discussions.value[index] = { ...discussions.value[index], ...updates }
      if (currentDiscussion.value?.id === discussionId) {
        currentDiscussion.value = discussions.value[index]
      }
      return discussions.value[index]
    }
    return null
  }

  const deleteDiscussion = (discussionId) => {
    const index = discussions.value.findIndex(d => d.id === discussionId)
    if (index !== -1) {
      discussions.value.splice(index, 1)
      delete replies.value[discussionId]
      if (currentDiscussion.value?.id === discussionId) {
        currentDiscussion.value = null
      }
      return true
    }
    return false
  }

  // 添加回复
  const addReply = (discussionId, reply) => {
    if (!replies.value[discussionId]) {
      replies.value[discussionId] = []
    }

    const newReply = {
      ...reply,
      id: `${discussionId}-${replies.value[discussionId].length + 1}`,
      publishTime: new Date().toISOString(),
      likeCount: 0
    }

    replies.value[discussionId].push(newReply)

    // 更新讨论的回复数和最后回复时间
    const discussion = discussions.value.find(d => d.id === discussionId)
    if (discussion) {
      discussion.replyCount++
      discussion.lastReplyTime = newReply.publishTime
    }

    return newReply
  }

  // 删除回复
  const deleteReply = (discussionId, replyId) => {
    const discussionReplies = replies.value[discussionId]
    if (discussionReplies) {
      const index = discussionReplies.findIndex(r => r.id === replyId)
      if (index !== -1) {
        discussionReplies.splice(index, 1)
        // 更新讨论的回复数
        const discussion = discussions.value.find(d => d.id === discussionId)
        if (discussion) {
          discussion.replyCount = discussionReplies.length
        }
        return true
      }
    }
    return false
  }

  // 点赞回复
  const likeReply = (discussionId, replyId, userId) => {
    const reply = replies.value[discussionId]?.find(r => r.id === replyId)
    if (reply) {
      reply.likeCount++
    }
  }

  // 搜索讨论
  const searchDiscussions = (searchTerm, courseId = null) => {
    const term = searchTerm.toLowerCase().trim()
    let filtered = courseId
      ? discussions.value.filter(d => d.courseId === courseId)
      : discussions.value

    if (!term) return filtered

    return filtered.filter(discussion =>
      discussion.title.toLowerCase().includes(term) ||
      discussion.content.toLowerCase().includes(term) ||
      discussion.author.name.toLowerCase().includes(term) ||
      discussion.tags.some(tag => tag.toLowerCase().includes(term))
    )
  }

  // 置顶/取消置顶
  const toggleTopDiscussion = (discussionId) => {
    const discussion = discussions.value.find(d => d.id === discussionId)
    if (discussion) {
      discussion.isTop = !discussion.isTop
    }
  }

  // 锁定/解锁讨论
  const toggleLockDiscussion = (discussionId) => {
    const discussion = discussions.value.find(d => d.id === discussionId)
    if (discussion) {
      discussion.isLocked = !discussion.isLocked
    }
  }

  // 增加查看次数
  const incrementViewCount = (discussionId) => {
    const discussion = discussions.value.find(d => d.id === discussionId)
    if (discussion) {
      discussion.viewCount++
    }
  }

  // 清空所有讨论
  const clearAll = () => {
    discussions.value = []
    currentDiscussion.value = null
    replies.value = {}
  }

  return {
    // State
    discussions,
    currentDiscussion,
    replies,
    loading,

    // Getters
    getDiscussionsByCourseId,
    discussionCount,
    getDiscussionById,
    getDiscussionsByTag,
    getHotDiscussions,
    getMyDiscussions,

    // Actions
    setCurrentDiscussion,
    createDiscussion,
    updateDiscussion,
    deleteDiscussion,
    addReply,
    deleteReply,
    likeReply,
    searchDiscussions,
    toggleTopDiscussion,
    toggleLockDiscussion,
    incrementViewCount,
    clearAll
  }
}, {
  persist: {
    key: 'discussion-store',
    storage: localStorage,
    paths: ['discussions', 'replies']
  }
})