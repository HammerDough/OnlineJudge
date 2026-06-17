package com.hammerdough.vo;

import lombok.Data;
import java.util.List;

/**
 * 整体提交/运行 最终返回VO
 */
@Data
public class JudgeResultVO {

    /**
     * 整体判题状态
     * 全部AC / 部分错误 / 编译失败 等
     */
    private String totalStatus;

    /**
     * 通过用例数
     */
    private Integer passCount;

    /**
     * 总用例数
     */
    private Integer totalCount;

    /**
     * 所有测试用例明细结果
     */
    private List<CaseJudgeResultVO> caseResultList;
}