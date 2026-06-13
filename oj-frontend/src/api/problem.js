import request from "@/utils/request";

export const getProblemList = (params) =>request.get("/problem/list",{params:params})

export const getProblemDetail = (id) =>request.get(`/problem/detail/${id}`)
