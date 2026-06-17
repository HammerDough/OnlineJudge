package com.hammerdough.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class JudgeSandboxUtil {

    private static final String SANDBOX_URL = "http://127.0.0.1:5050/run";
    private static final String FILE_DEL_URL = "http://127.0.0.1:5050/file/";


    private static final int STACK_LIMIT = 16777216;
    private static final int PROC_LIMIT = 50;
    private static final int FILE_MAX = 10240;

    private static final String LANG_JAVA = "java";
    private static final String LANG_CPP = "cpp";
    private static final String LANG_PYTHON = "python";
    private static final String PYTHON_FLAG = "PYTHON_NO_COMPILE";

    @Autowired
    private RestTemplate restTemplate;

    // 线程级缓存，存放当前请求的Python源码，多线程安全
    private final ThreadLocal<String> pythonSourceCache = new ThreadLocal<>();

    /**
     * 编译逻辑
     */
    public String compileCode(String code, String language,long timeMs,long memoryKb) {
        long cpuLimitNs = timeMs *1000_1000;
        long memoryLimitByte = memoryKb * 1024;

        if (!StringUtils.hasText(language) || !StringUtils.hasText(code)) {
            return null;
        }

        // Python 无需编译，缓存源码并返回标记
        if (LANG_PYTHON.equalsIgnoreCase(language)) {
            pythonSourceCache.remove();
            pythonSourceCache.set(code);
            return PYTHON_FLAG;
        }

        String srcFileName;
        String outFileName;
        List<String> args = new ArrayList<>();

        if (LANG_JAVA.equalsIgnoreCase(language)) {
            srcFileName = "Main.java";
            outFileName = "Main.class";
            args.add("/usr/bin/javac");
            args.add(srcFileName);
        } else if (LANG_CPP.equalsIgnoreCase(language)) {
            srcFileName = "main.cpp";
            outFileName = "main";
            args.add("/usr/bin/g++");
            args.add(srcFileName);
            args.add("-o");
            args.add(outFileName);
        } else {
            return null;
        }

        Map<String, Object> compileReq = new HashMap<>();
        List<Map<String, Object>> cmdList = new ArrayList<>();
        Map<String, Object> cmdItem = new HashMap<>();

        cmdItem.put("args", args);
        cmdItem.put("env", Arrays.asList("PATH=/usr/bin:/bin"));

        List<Map<String, Object>> files = new ArrayList<>();
        Map<String, Object> emptyFile = new HashMap<>();
        emptyFile.put("content", "");
        files.add(emptyFile);

        Map<String, Object> stdoutFile = new HashMap<>();
        stdoutFile.put("name", "stdout");
        stdoutFile.put("max", FILE_MAX);
        files.add(stdoutFile);

        Map<String, Object> stderrFile = new HashMap<>();
        stderrFile.put("name", "stderr");
        stderrFile.put("max", FILE_MAX);
        files.add(stderrFile);
        cmdItem.put("files", files);

        cmdItem.put("cpuLimit", cpuLimitNs);
        cmdItem.put("memoryLimit", memoryLimitByte);
        cmdItem.put("stackLimit", STACK_LIMIT);
        cmdItem.put("procLimit", PROC_LIMIT);

        Map<String, Object> copyIn = new HashMap<>();
        Map<String, Object> sourceContent = new HashMap<>();
        sourceContent.put("content", code);
        copyIn.put(srcFileName, sourceContent);
        cmdItem.put("copyIn", copyIn);

        cmdItem.put("copyOutCached", Collections.singletonList(outFileName));
        cmdItem.put("copyOut", Arrays.asList("stdout", "stderr"));

        cmdList.add(cmdItem);
        compileReq.put("cmd", cmdList);

        String reqJson = JSON.toJSONString(compileReq);
        String resp = restTemplate.postForObject(SANDBOX_URL, reqJson, String.class);

        if (!resp.startsWith("[")) {
            return null;
        }
        List<JSONObject> respArr = JSON.parseArray(resp, JSONObject.class);
        JSONObject result = respArr.get(0);
        if (!"Accepted".equals(result.getString("status"))) {
            return null;
        }

        JSONObject fileIds = result.getJSONObject("fileIds");
        return fileIds.getString(outFileName);
    }

    /**
     * 运行逻辑
     */
    public JSONObject runCode(String fileId, String input, String language, long timeMs, long memoryKb) {
        long cpuLimitNs = timeMs * 1000_000;
        long memoryLimitByte = memoryKb * 1024;

        if (!StringUtils.hasText(language)) {
            return null;
        }

        // ===================== Python 分支 =====================
        if (LANG_PYTHON.equalsIgnoreCase(language)) {
            // 从缓存取出真实Python代码
            String realCode = pythonSourceCache.get();
            if (!StringUtils.hasText(realCode)) {
                pythonSourceCache.remove();
                return null;
            }

            Map<String, Object> runReq = new HashMap<>();
            List<Map<String, Object>> cmdList = new ArrayList<>();
            Map<String, Object> cmdItem = new HashMap<>();

            List<String> args = new ArrayList<>();
            args.add("/usr/bin/python3");
            args.add("main.py");
            cmdItem.put("args", args);
            cmdItem.put("env", Arrays.asList("PATH=/usr/bin:/bin"));

            List<Map<String, Object>> files = new ArrayList<>();
            Map<String, Object> inputFile = new HashMap<>();
            inputFile.put("content", input);
            files.add(inputFile);

            Map<String, Object> stdoutFile = new HashMap<>();
            stdoutFile.put("name", "stdout");
            stdoutFile.put("max", FILE_MAX);
            files.add(stdoutFile);

            Map<String, Object> stderrFile = new HashMap<>();
            stderrFile.put("name", "stderr");
            stderrFile.put("max", FILE_MAX);
            files.add(stderrFile);
            cmdItem.put("files", files);

            cmdItem.put("cpuLimit", cpuLimitNs);
            cmdItem.put("memoryLimit", memoryLimitByte);
            cmdItem.put("stackLimit", STACK_LIMIT);
            cmdItem.put("procLimit", PROC_LIMIT);

            // 写入真实业务代码
            Map<String, Object> copyIn = new HashMap<>();
            Map<String, Object> sourceContent = new HashMap<>();
            sourceContent.put("content", realCode);
            copyIn.put("main.py", sourceContent);
            cmdItem.put("copyIn", copyIn);
            cmdItem.put("copyOut", Arrays.asList("stdout", "stderr"));

            cmdList.add(cmdItem);
            runReq.put("cmd", cmdList);

            String reqJson = JSON.toJSONString(runReq);
            System.out.println("Python 请求报文：" + reqJson);
            String resp = restTemplate.postForObject(SANDBOX_URL, reqJson, String.class);
            System.out.println("沙箱原始响应：" + resp);


            if (!resp.startsWith("[")) {
                pythonSourceCache.remove();
                return null;
            }
            List<JSONObject> respArr = JSON.parseArray(resp, JSONObject.class);
            if (respArr == null || respArr.isEmpty()) {
                pythonSourceCache.remove();
                return null;
            }
            return respArr.get(0);
        }

        // ===================== Java / C++ 原有逻辑 =====================
        if (!StringUtils.hasText(fileId)) {
            return null;
        }
        List<String> args = new ArrayList<>();
        String runFile;

        if (LANG_JAVA.equalsIgnoreCase(language)) {
            args.add("/usr/bin/java");
            args.add("Main");
            runFile = "Main.class";
        } else if (LANG_CPP.equalsIgnoreCase(language)) {
            args.add("./main");
            runFile = "main";
            runFile = "main";
        } else {
            return null;
        }

        Map<String, Object> runReq = new HashMap<>();
        List<Map<String, Object>> cmdList = new ArrayList<>();
        Map<String, Object> cmdItem = new HashMap<>();

        cmdItem.put("args", args);
        cmdItem.put("env", Arrays.asList("PATH=/usr/bin:/bin"));

        List<Map<String, Object>> files = new ArrayList<>();
        Map<String, Object> inputFile = new HashMap<>();
        inputFile.put("content", input);
        files.add(inputFile);

        Map<String, Object> stdoutFile = new HashMap<>();
        stdoutFile.put("name", "stdout");
        stdoutFile.put("max", FILE_MAX);
        files.add(stdoutFile);

        Map<String, Object> stderrFile = new HashMap<>();
        stderrFile.put("name", "stderr");
        stderrFile.put("max", FILE_MAX);
        files.add(stderrFile);
        cmdItem.put("files", files);

        cmdItem.put("cpuLimit", cpuLimitNs);
        cmdItem.put("memoryLimit", memoryLimitByte);
        cmdItem.put("stackLimit", STACK_LIMIT);
        cmdItem.put("procLimit", PROC_LIMIT);

        Map<String, Object> copyIn = new HashMap<>();
        Map<String, Object> fileObj = new HashMap<>();
        fileObj.put("fileId", fileId);
        copyIn.put(runFile, fileObj);
        cmdItem.put("copyIn", copyIn);
        cmdItem.put("copyOut", Arrays.asList("stdout", "stderr"));

        cmdList.add(cmdItem);
        runReq.put("cmd", cmdList);

        String reqJson = JSON.toJSONString(runReq);
        String resp = restTemplate.postForObject(SANDBOX_URL, reqJson, String.class);

        if (!resp.startsWith("[")) {
            return null;
        }
        List<JSONObject> respArr = JSON.parseArray(resp, JSONObject.class);
        return respArr.get(0);
    }

    public void clearPythonCache() {
        pythonSourceCache.remove();
    }

    /**
     * 删除沙箱文件
     */
    public void deleteFile(String fileId) {
        // Python 无远端文件，跳过删除
        if (PYTHON_FLAG.equals(fileId)) {
            return;
        }
        if (StringUtils.hasText(fileId)) {
            restTemplate.delete(FILE_DEL_URL + fileId);
        }
    }
}