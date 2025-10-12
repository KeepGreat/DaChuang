import request from "../request";

export let addMaterial = (Material) => {
    return request.post('api/teaching/material', Material)
}

export let deleteMaterial = (id) => {
    return request.delete(`api/teaching/material/${id}`)
}

export let updateMaterial = (Material) => {
    return request.put('api/teaching/material', Material)
}

export let getMaterials = (id, type, createdTime, updatedTime, courseId) => {
    return request.get('api/teaching/material', {
        params: {
            id: id,
            type: type,
            createdTime: createdTime,
            updatedTime: updatedTime,
            courseId: courseId
        }
    })
}

export let getMaterialsPage = (pageNo, pageSize, id, type, createdTime, updatedTime, courseId) => {
    return request.get(`api/teaching/material/${pageNo}/${pageSize}`, {
        params: {
            id: id,
            type: type,
            createdTime: createdTime,
            updatedTime: updatedTime,
            courseId: courseId
        }
    })
}