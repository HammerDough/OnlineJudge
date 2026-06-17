import request from "@/utils/request";

export const getUserDailyStat = (userId,startDate,endDate) => request.get(`/user/daily-stat/${userId}`,startDate,endDate)

export const updateUserDailyMinute = (data)=>request.post("/user/daily-stat/add-minute",data)