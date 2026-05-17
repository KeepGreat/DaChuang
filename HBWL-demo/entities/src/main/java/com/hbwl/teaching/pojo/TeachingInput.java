package com.hbwl.teaching.pojo;

import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import lombok.Data;

import java.util.List;

@Data
public class TeachingInput {
    private String question;
    private CodeSandboxInput codeSandboxInput;
    private List<Integer> relativeMaterialIds; //用于RAG检索时进行元数据过滤
}
