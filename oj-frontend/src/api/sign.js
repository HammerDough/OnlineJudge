import request from "@/utils/request";

export const getSignCalendar = (params) => {
  return request.get("/user/daily-sign", { params })
}