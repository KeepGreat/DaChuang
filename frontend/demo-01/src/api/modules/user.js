import request from "@/utils/request";

export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: "DELETE",
  });
}

export function updateUser(data) {
  return request({
    url: "/user",
    method: "PUT",
    data: data,
  });
}

export function getAllUsers(params = {}) {
  return request({
    url: "/user",
    method: "GET",
    params: params,
  });
}
