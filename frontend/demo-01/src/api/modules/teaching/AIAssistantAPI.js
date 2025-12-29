import request from "@/utils/request"

export let teach = (teachingInput) => {
    return request.post('http://localhost:80/api/teaching/ai/teach',
        teachingInput,{
            headers:{
                'Content-Type': 'application/json'
            }
            //responseType: 'stream'
        }
    )
}

export let answer = (teachingInput) => {
    return request.post('http://localhost:80/api/teaching/ai/answer',
        teachingInput,{
            headers:{
                'Content-Type': 'application/json'
            }
            //responseType: 'stream'
        }
    )
}
