import request from '@/utils/request'

export let addCourseSection = (CourseSection) => {
    return request.post("api/teaching/coursesection", CourseSection)
}

export let deleteCourseSection = (id) => {
    return request.delete(`api/teaching/coursesection/${id}`)
}

export let updateCourseSection = (CourseSection) => {
    return request.put(`api/teaching/coursesection/`, CourseSection)
}

export let getCourseSections = (id, name) => {
    return request.get("api/teaching/coursesection", {
        params: {
            id: id,
            name: name
        }
    })
}

// 获取所有课程章节（不分页）
export let getAllCourseSections = (studentId) => {
    return request.get("api/teaching/coursesection", {
        params: {
            studentId: studentId
        }
    })
}

export let getCourseSectionsPage = (pageNo, pageSize, studentId, id, name) => {
    return request.get(`api/teaching/coursesection/${pageNo}/${pageSize}`, {
        params: {
            studentId: studentId,
            id: id,
            name: name
        }
    })
}