import request from "@/utils/request";

export const login = (data) => request.post("/user/login",data)

export const register = (data) =>request.post("/user/register",data)

export const getUserInfo = ()=>request.get("/user/info")

export const getUserInfoById = (userId)=>request.get(`/user/info/${userId}`)

export const updateUserInfo = (data) => request.put("/user/update",data)