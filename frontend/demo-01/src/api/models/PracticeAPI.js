import request from "../../utils/request";

export let evaluate = (submission) => {
    return request.post('/api/analysis/evaluate', submission)
}