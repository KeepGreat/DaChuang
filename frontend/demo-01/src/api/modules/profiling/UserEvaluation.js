import request from "@/api/request";

export const getUserEvaluation = () => {
    return request.get('api/evaluation/evaluation')
}