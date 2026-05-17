import request from "@/api/request";

/**
 * 后端实体类设计
public class User {
    private String id;

    private String username;
    private String password;
    private String role;
}
 */

export function getAllUsers(params = {}) {
  return request({
    url: "/user",
    method: "GET",
    params: params,
  });
}

export function updateUser(data) {
  return request({
    url: "/user",
    method: "PUT",
    data: data,
  });
}

export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: "DELETE",
  });
}
