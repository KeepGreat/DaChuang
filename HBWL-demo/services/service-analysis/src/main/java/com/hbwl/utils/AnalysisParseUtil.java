package com.hbwl.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbwl.analysis.pojo.AnalysisOutput;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AnalysisParseUtil {

    private static final Pattern pattern = Pattern.compile("\"\"\"(.*?)\"\"\"[\\s\\r\\n]*(\\{.*\\})", Pattern.DOTALL);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public AnalysisOutput parseAnalysis(String analysis){
        Matcher matcher = pattern.matcher(analysis);

        if (matcher.find()) {
            String textPart = matcher.group(1).trim();
            String jsonString = matcher.group(2).trim();
            // 进一步清理JSON字符串：移除可能的多余空白
            jsonString = jsonString.replaceAll("\\s+", " ");
            try {
                Map<String, Integer> jsonMap = objectMapper.readValue(jsonString, new TypeReference<Map<String, Integer>>() {});
                AnalysisOutput output = new AnalysisOutput();
                output.setAnalysis(textPart);
                output.setScore(jsonMap);
                return output;
            } catch (JsonProcessingException e) {
                System.err.println("提取到的字符串不是有效JSON: " + jsonString);
            }
        }
        return null;
    }
}
