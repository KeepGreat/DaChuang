package com.hbwl.tool;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 调用go-judge开源项目作为代码沙箱
 * 本质上就是构造对应格式的JSON发送给沙箱，再对返回的JSON进行解析
 * 参考文档：https://docs.goj.ac/cn/example
 * https://gitee.com/himitzh0730/hoj/blob/master/hoj-springboot/JudgeServer/src/main/java/top/hcode/hoj/judge/SandboxRun.java
 * */
public class CodeExecutionTool {

    private static final RestTemplate restTemplate;

    private static final String SANDBOX_BASE_URL = "http://localhost:5050";

    private static final int MAX_PROCESS_NUMBER = 64;

    private static final int TIME_LIMIT_MS = 1600 * 1000;

    private static final int MEMORY_LIMIT_MB = 512;

    private static final int STACK_LIMIT_MB = 128;

    private static final int STDIO_SIZE_MB = 32;

    static {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(20000);
        simpleClientHttpRequestFactory.setReadTimeout(180000);
        restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
    }

    public JSONArray run(String url, JSONObject param){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(JSONUtil.toJsonStr(param), headers);
        ResponseEntity<String> postForEntity;
        try {
            postForEntity = restTemplate.postForEntity(SANDBOX_BASE_URL + url, request, String.class);
            return JSONUtil.parseArray(postForEntity.getBody());
        } catch (RestClientResponseException ex) {
            if (ex.getRawStatusCode() != 200) {
                System.out.println("Cannot Connect to Sandbox: " + ex.getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deleteCached(String fileId){
        try {
            restTemplate.delete(SANDBOX_BASE_URL + "/file/{0}", fileId);
        } catch (RestClientResponseException ex) {
            if (ex.getRawStatusCode() != 200) {
                System.out.println("安全沙箱判题的删除内存中的文件缓存操作异常:\n" + ex.getResponseBodyAsString());
            }
        }
    }

    private static final JSONArray COMPILE_FILES = new JSONArray();

    static {
        JSONObject content = new JSONObject();
        content.set("content", "");

        JSONObject stdout = new JSONObject();
        stdout.set("name", "stdout");
        stdout.set("max", 1024 * 1024 * STDIO_SIZE_MB);

        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * STDIO_SIZE_MB);
        COMPILE_FILES.put(content);
        COMPILE_FILES.put(stdout);
        COMPILE_FILES.put(stderr);
    }

    public JSONArray compile(String codeLanguage, String code){
        JSONObject cmd = new JSONObject();

        //python是解释型语言，不需要编译
        List<String> args;
        if (codeLanguage.equals("java")) args = List.of("/usr/bin/javac", "Main.java");
        else if (codeLanguage.equals("cpp")) args = List.of("/usr/bin/g++", "a.cpp", "-o", "a");
        else {
            System.out.println("Unsupported code language: " + codeLanguage);
            return null;
        }
        cmd.set("args", args);

        List<String> envs = List.of("PATH=/usr/bin:/bin");
        cmd.set("env", envs);
        cmd.set("files", COMPILE_FILES);

        cmd.set("cpuLimit", TIME_LIMIT_MS * 1000 );
        cmd.set("memoryLimit", MEMORY_LIMIT_MB * 1024 * 1024);
        cmd.set("procLimit", MAX_PROCESS_NUMBER);
        cmd.set("stackLimit", STACK_LIMIT_MB * 1024 * 1024);

        JSONObject content = new JSONObject();
        content.set("content", code);
        JSONObject copyIn = new JSONObject();
        if (codeLanguage.equals("java")) copyIn.set("Main.java", content);
        else if (codeLanguage.equals("cpp")) copyIn.set("a.cpp", content);
        cmd.set("copyIn", copyIn);
        cmd.set("copyOut", new JSONArray().put("stdout").put("stderr"));
        if (codeLanguage.equals("java")) cmd.set("copyOutCached", new JSONArray().put("Main.class"));
        else if (codeLanguage.equals("cpp")) cmd.set("copyOutCached", new JSONArray().put("a"));

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        JSONArray result = run("/run", param);
        return result;
    }

    public String getFileId(JSONArray compileResult, String codeLanguage){
        JSONObject result = compileResult.getJSONObject(0);
        JSONObject fileIds = result.getJSONObject("fileIds");
        if (codeLanguage.equals("java")) return fileIds.getStr("Main.class");
        else if (codeLanguage.equals("cpp")) return fileIds.getStr("a");
        else return null;
    }

    public JSONArray execute(String codeLanguage, String input, String fileID){
        JSONObject cmd = new JSONObject();

        List<String> args;
        if (codeLanguage.equals("java")) args = List.of("/usr/bin/java", "Main");
        else if (codeLanguage.equals("cpp")) args = List.of("a");
        else {
            System.out.println("Unsupported code language: " + codeLanguage);
            return null;
        }
        cmd.set("args", args);

        List<String> envs = List.of("PATH=/usr/bin:/bin");
        cmd.set("env", envs);

        JSONArray file = new JSONArray();
        JSONObject content = new JSONObject();
        if (input == null || input.isEmpty()) content.set("content", "");
        else content.set("content", input);
        file.put(content);
        JSONObject stdout = new JSONObject();
        stdout.set("name", "stdout");
        stdout.set("max", 1024 * 1024 * STDIO_SIZE_MB);
        file.put(stdout);
        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * STDIO_SIZE_MB);
        file.put(stderr);
        cmd.set("files", file);

        cmd.set("cpuLimit", TIME_LIMIT_MS * 1000);
        cmd.set("memoryLimit", MEMORY_LIMIT_MB * 1024 * 1024);
        cmd.set("procLimit", MAX_PROCESS_NUMBER);
        cmd.set("stackLimit", STACK_LIMIT_MB * 1024 * 1024);

        JSONObject preparedFile = new JSONObject();
        preparedFile.set("fileId", fileID);
        JSONObject copyIn = new JSONObject();
        if (codeLanguage.equals("java")) copyIn.set("Main.class", preparedFile);
        else if (codeLanguage.equals("cpp")) copyIn.set("a", preparedFile);
        cmd.set("copyIn", copyIn);

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        JSONArray result = run("/run", param);
        return result;
    }

    public JSONArray executeForPython(String code, String input){
        JSONObject cmd = new JSONObject();

        List<String> args = List.of("/usr/bin/python3", "a.py");
        cmd.set("args", args);
        List<String> envs = List.of("PATH=/usr/bin:/bin");
        cmd.set("env", envs);

        JSONArray file = new JSONArray();
        JSONObject content = new JSONObject();
        if (input == null || input.isEmpty()) content.set("content", "");
        else content.set("content", input);
        file.put(content);
        JSONObject stdout = new JSONObject();
        stdout.set("name", "stdout");
        stdout.set("max", 1024 * 1024 * STDIO_SIZE_MB);
        file.put(stdout);
        JSONObject stderr = new JSONObject();
        stderr.set("name", "stderr");
        stderr.set("max", 1024 * 1024 * STDIO_SIZE_MB);
        file.put(stderr);
        cmd.set("files", file);

        cmd.set("cpuLimit", TIME_LIMIT_MS * 1000);
        cmd.set("memoryLimit", MEMORY_LIMIT_MB * 1024 * 1024);
        cmd.set("procLimit", MAX_PROCESS_NUMBER);
        cmd.set("stackLimit", STACK_LIMIT_MB * 1024 * 1024);

        JSONObject fileContent = new JSONObject();
        fileContent.set("content", code);
        JSONObject copyIn = new JSONObject();
        copyIn.set("a.py", fileContent);
        cmd.set("copyIn", copyIn);

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        JSONArray result = run("/run", param);
        return result;
    }

    public String compileAndExecuteForOther(String codeLanguage, String code, String input){
        JSONArray compile = compile(codeLanguage, code);
        String fileId = getFileId(compile, codeLanguage);
        JSONArray execute = execute(codeLanguage, input, fileId);
        deleteCached(fileId);
        return execute.getJSONObject(0).toString();
    }

    public String executePython(String code, String input){
        JSONArray execute = executeForPython(code, input);
        return execute.getJSONObject(0).toString();
    }

    public String compileAndExecute(String codeLanguage,
                                    String code,
                                    String input){
        if (codeLanguage.equals("python")) return executePython(code, input);
        else if (codeLanguage.equals("java") || codeLanguage.equals("cpp"))
            return compileAndExecuteForOther(codeLanguage, code, input);
        else return "Unsupported code language: " + codeLanguage;
    }
}
//cpp运行结果[{"memory":524288,"procPeak":1,"files":{"stdout":"3","stderr":""},"time":1041000,"runTime":743535,"exitStatus":0,"status":"Accepted"}]
//java运行结果[{"memory":23261184,"procPeak":20,"files":{"stdout":"3\n","stderr":""},"time":46723000,"runTime":34608489,"exitStatus":0,"status":"Accepted"}]
//python运行结果[{"memory":2883584,"procPeak":1,"files":{"stdout":"3\n","stderr":""},"time":11820000,"runTime":11829754,"exitStatus":0,"status":"Accepted"}]