import request from '../request'

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

export let getCourseSectionsPage = (pageNo, pageSize, id, name) => {
    return request.get(`api/teaching/coursesection/${pageNo}/${pageSize}`, {
        params: {
            id: id,
            name: name
        }
    })
}