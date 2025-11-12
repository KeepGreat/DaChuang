import request from '../request'

export let addCourse = (Course) => {
    return request.post('api/teaching/course', Course)
}

export let deleteCourse = (id) => {
    return request.delete(`api/teaching/course/${id}`)
}

export let updateCourse = (Course) => {
    return request.put('api/teaching/course', Course)
}

export let getCourses = (id, name, sectionId) => {
    return request.get('api/teaching/course', {
        params: {
            id: id,
            name: name,
            sectionId :sectionId
        }
    })
}

export let getCoursesPage = (pageNo, pageSize, id, name, sectionId) => {
    return request.get(`api/teaching/course/${pageNo}/${pageSize}`, {
        params: {
            id: id,
            name: name,
            sectionId :sectionId
        }
    })
}