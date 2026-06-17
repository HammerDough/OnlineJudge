package com.hammerdough.common;

/**
 * 判题状态常量
 */
public class JudgeStatusConst {

    // 通用状态
    public static final String AC = "AC";      // 答案正确
    public static final String WA = "WA";      // 答案错误
    public static final String TLE = "TLE";    // 时间超限
    public static final String MLE = "MLE";    // 内存超限
    public static final String RE = "RE";      // 运行时异常
    public static final String CE = "CE";      // 编译错误

    // 整体汇总状态文案（给前端展示）
    public static final String TOTAL_AC = "全部通过";
    public static final String TOTAL_FAIL = "存在错误";
    public static final String COMPILE_ERROR = "编译失败";
}