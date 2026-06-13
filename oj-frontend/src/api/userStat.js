import request from "@/utils/request";

export const getUserDailyStat = (userId,startDate,endDate) => request.get(`/user/daily-stat/${userId}`,startDate,endDate)