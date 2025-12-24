package com.hbwl.codesandbox.pojo;

import lombok.Data;

@Data
public class CodeSandboxOutput {
    private String stdout;
    private String stderr;
    private String status;
    private Integer exitStatus;
    private Long memory;
    private Long runTime;
    private Integer procPeak;
    private Long time;
}
