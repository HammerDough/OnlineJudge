package com.hammerdough.vo;

import lombok.Data;

/**
 * 单条测试用例判题结果VO
 */
@Data
public class CaseJudgeResultVO {

    /**
     * 用例ID
     */
    private Integer caseId;

    /**
     * 判题状态码（核心）
     * AC = 答案正确
     * WA = 答案错误
     * TLE = 超时
     * MLE = 内存超限
     * RE = 运行时异常
     * CE = 编译错误
     */
    private String judgeStatus;

    /**
     * 运行耗时 单位：毫秒
     */
    private Long timeCost;

    /**
     * 占用内存 单位：KB
     */
    private Long memoryCost;

    /**
     * 当前用例输入
     */
    private String caseInput;

    /**
     * 用户程序输出
     */
    private String userOutput;

    /**
     * 标准输出（标准答案）
     */
    private String standardOutput;
}