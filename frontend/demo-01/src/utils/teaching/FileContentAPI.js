import request from "../request";

export let downloadFile = (fileName) => {
    return request.get('api/teaching/file/download', {
        params: {
            fileName: fileName
        },
        responseType: 'blob'
    })
}

export let uploadFile = (fileContent, file) => {
    const formData = new FormData()
    formData.append('fileContent', JSON.stringify(fileContent))
    formData.append('file', file)
    return request.post('api/teaching/file', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

export let getFileContents = (id, type, name, size, matId) => {
    return request.get('api/teaching/file', {
        params: {
            id: id,
            type: type,
            name: name,
            size: size,
            matId: matId
        }
    })
}