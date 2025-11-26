import request from "@/utils/request";

export function register(data) {
  return request({
    url: "/user/register",
    method: "POST",
    data: data,
    headers: {
      Authorization: "no-auth",
    },
  });
}

export function login(data) {
  return request({
    url: "/authenticate",
    method: "POST",
    data: data,
    headers: {
      Authorization: "no-auth",
    },
  });
}

export function refreshToken(data) {
  return request({
    url: "/refreshtoken",
    method: "POST",
    data: data,
    headers: {
      Authorization: "no-auth",
    },
  });
}
