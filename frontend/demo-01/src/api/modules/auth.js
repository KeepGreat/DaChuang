import request from "@/utils/request";

export function register(data) {
  return request({
    url: "/user/register",
    method: "POST",
    data: data,
  });
}

export function login(data) {
  return request({
    url: "/authenticate",
    method: "POST",
    headers: {
      Authorization: "no-auth",
    },
    data: data,
  });
}

export function refreshToken(data) {
  return request({
    url: "/refreshtoken",
    method: "POST",
    headers: {
      Authorization: "no-auth",
    },
    data: data,
  });
}
