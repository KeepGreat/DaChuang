import request from "@/utils/request";

export const getUserEvaluation = () => {
    return request.get('api/evaluation/evaluation')
}