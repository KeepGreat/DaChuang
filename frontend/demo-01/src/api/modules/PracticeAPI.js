import request from "@/api/request";

export let evaluate = (submission) => {
    return request.post('/api/analysis/evaluate', submission)
}