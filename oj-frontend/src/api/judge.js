import request from "@/utils/request";

export const judgeCode = (data) =>request.post("/judge/submit",data)