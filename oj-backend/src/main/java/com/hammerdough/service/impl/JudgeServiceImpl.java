package com.hammerdough.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hammerdough.common.JudgeStatusConst;
import com.hammerdough.dto.JudgeSubmitDTO;
import com.hammerdough.entity.*;
import com.hammerdough.mapper.ProblemDetailMapper;
import com.hammerdough.mapper.UserDailySignMapper;
import com.hammerdough.mapper.UserDailyStatMapper;
import com.hammerdough.mapper.UserMapper;
import com.hammerdough.service.JudgeService;
import com.hammerdough.service.ProblemTestCaseService;
import com.hammerdough.service.SubmitRecordService;
import com.hammerdough.util.JudgeSandboxUtil;
import com.hammerdough.util.UnitConvertUtil;
import com.hammerdough.vo.CaseJudgeResultVO;
import com.hammerdough.vo.JudgeResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Autowired
    private ProblemTestCaseService testCaseService;
    @Autowired
    private SubmitRecordService submitRecordService;
    @Autowired
    private ProblemDetailMapper problemDetailMapper;
    @Autowired
    private JudgeSandboxUtil sandboxUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserDailyStatMapper dailyStatMapper;
    @Autowired
    private UserDailySignMapper userDailySignMapper;

    // 标记当前题目是否AC（同一题多次用例只算一次完成）
    private final ThreadLocal<Boolean> problemAcFlag = new ThreadLocal<>();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JudgeResultVO judge(JudgeSubmitDTO submitDTO, Integer userId) {
        Integer problemId = submitDTO.getProblemId();
        String code = submitDTO.getCode();
        String language = submitDTO.getLanguage();
        Integer operateType = submitDTO.getOperateType();

        String fileId = null;
        try {
            // 1. 查询题目限制
            ProblemDetail detail = problemDetailMapper.selectDetailByProblemId(problemId);
            if (detail == null) {
                throw new RuntimeException("题目不存在");
            }

            long timeMs = detail.getTimeLimit();
            long memoryKb = detail.getMemoryLimit();

            long maxRunTime = 0L;
            long maxRunMemory = 0L;

            // 2. 根据运行/提交类型获取测试用例
            List<ProblemTestCase> caseList;
            if (0 == operateType) {
                caseList = testCaseService.getRunTestCase(problemId);
            } else {
                caseList = testCaseService.getSubmitTestCase(problemId);
            }
            if (caseList.isEmpty()) {
                throw new RuntimeException("暂无可用测试用例");
            }

            // 3. 编译代码
            fileId = sandboxUtil.compileCode(code, language,timeMs,memoryKb);
            if (!StringUtils.hasText(fileId)) {
                JudgeResultVO resultVO = new JudgeResultVO();
                resultVO.setTotalStatus(JudgeStatusConst.COMPILE_ERROR);
                resultVO.setPassCount(0);
                resultVO.setTotalCount(caseList.size());
                resultVO.setCaseResultList(new ArrayList<>());
                return resultVO;
            }

            // 4. 遍历用例执行判题
            List<CaseJudgeResultVO> caseResultList = new ArrayList<>();
            int passCount = 0;
            problemAcFlag.set(false);

            for (ProblemTestCase testCase : caseList) {
                CaseJudgeResultVO caseVO = new CaseJudgeResultVO();
                caseVO.setCaseId(testCase.getId());
                caseVO.setCaseInput(testCase.getInputContent());
                caseVO.setStandardOutput(testCase.getOutputContent());

                JSONObject runResp = sandboxUtil.runCode(fileId, testCase.getInputContent(), language,timeMs,memoryKb);
                if (runResp == null) {
                    caseVO.setJudgeStatus(JudgeStatusConst.RE);
                    caseResultList.add(caseVO);
                    continue;
                }
                String status = runResp.getString("status");
                long runTimeNs = runResp.getLong("runTime");
                maxRunTime = Math.max(maxRunTime, runTimeNs);
                long memoryByte = runResp.getLong("memory");
                maxRunMemory = Math.max(maxRunMemory, memoryByte);

                // 时间、内存单位转换
                caseVO.setTimeCost(UnitConvertUtil.nsToMs(runTimeNs));
                caseVO.setMemoryCost(UnitConvertUtil.byteToKb(memoryByte));

                JSONObject files = runResp.getJSONObject("files");
                String userOut = files.getString("stdout");
                String errOut = files.getString("stderr");
                caseVO.setUserOutput(userOut);

                // 判题逻辑
                if ("Time Limit Exceeded".equals(status)) {
                    caseVO.setJudgeStatus(JudgeStatusConst.TLE);
                } else if ("Memory Limit Exceeded".equals(status)) {
                    caseVO.setJudgeStatus(JudgeStatusConst.MLE);
                } else if ("Nonzero Exit Status".equals(status) || StringUtils.hasText(errOut)) {
                    caseVO.setJudgeStatus(JudgeStatusConst.RE);
                } else {
                    // ========== 优化：彻底清除首尾空白、换行、空格 ==========
                    String userTrim = userOut == null ? "" : userOut.trim();
                    String stdTrim = testCase.getOutputContent() == null ? "" : testCase.getOutputContent().trim();

                    if (userTrim.equals(stdTrim)) {
                        caseVO.setJudgeStatus(JudgeStatusConst.AC);
                        passCount++;
                        problemAcFlag.set(true);
                    } else {
                        caseVO.setJudgeStatus(JudgeStatusConst.WA);
                    }
                }
                caseResultList.add(caseVO);
            }

            sandboxUtil.clearPythonCache();

            OjSubmitRecord record = new OjSubmitRecord();
            record.setUserId(userId);
            record.setProblemId(problemId);
            record.setLanguage(language);
            record.setSubmitCode(code);
            long runTimeMs = UnitConvertUtil.nsToMs(maxRunTime);
            long runMemKb = UnitConvertUtil.byteToKb(maxRunMemory);

            // 兜底截断，适配数据库 int 字段
            record.setRunTime(runTimeMs > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) runTimeMs);
            record.setRunMemory(runMemKb > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) runMemKb);
            record.setTotalCase(caseResultList.size());
            record.setPassCase(passCount);
            // 5. 正式提交 更新用户统计数据
            // 正式提交：一定保存提交记录
            if (1 == operateType) {
                submitRecordService.insertRecord(record);
                // 只有本题有通过，才更新用户做题统计
                if (problemAcFlag.get()) {
                    updateUserStat(userId);
                }
            }

            // 6. 封装返回结果
            JudgeResultVO finalResult = new JudgeResultVO();
            finalResult.setTotalCount(caseList.size());
            finalResult.setPassCount(passCount);
            finalResult.setCaseResultList(caseResultList);
            if (passCount == caseList.size()) {
                finalResult.setTotalStatus(JudgeStatusConst.TOTAL_AC);
            } else {
                finalResult.setTotalStatus(JudgeStatusConst.TOTAL_FAIL);
            }
            return finalResult;

        } finally {
            // ========== 优化：无论正常/异常，强制释放沙箱文件 + 清理ThreadLocal ==========
            if (StringUtils.hasText(fileId)) {
                sandboxUtil.deleteFile(fileId);
            }
            problemAcFlag.remove();
        }
    }

    /**
     * 更新用户每日统计 & 总完成题数
     */
    private void updateUserStat(Integer userId) {
        LocalDate now = LocalDate.now();
        Integer dailyId = dailyStatMapper.selectIdByUserIdAndDate(userId, now);
        if (dailyId == null) {
            UserDailyStat stat = new UserDailyStat();
            stat.setUserId(userId);
            stat.setStatDate(now);
            stat.setFinishCount(1);
            dailyStatMapper.insertUserDailyStat(stat);
        } else {
            dailyStatMapper.incrDailyFinishCount(dailyId);
        }
        // 更新总完成题目数
        userMapper.incrFinishProblemNum(userId);
        userDailySignMapper.insertSign(userId);
    }
}