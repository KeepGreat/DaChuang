import request from "@/api/request";

export const getUserInfo = () => {
    return request.get('/user/id');
}