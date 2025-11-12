import request from "@/utils/request";

export let teach = (teachingInput) => {
    return fetch('http://localhost:80/api/teaching/ai/teach', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(teachingInput)
    })
}

export let answer = (teachingInput) => {
    return fetch('http://localhost:80/api/teaching/ai/answer', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(teachingInput)
    })
}