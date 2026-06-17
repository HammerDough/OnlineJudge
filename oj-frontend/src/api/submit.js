import request from "@/utils/request";

export const getSubmitRecordList = (params) => {
  return request.get("/submit/list", { params })
}

export const getSubmitRecordDetail = (params) => {
  return request.get("/submit/detail", { params })
}