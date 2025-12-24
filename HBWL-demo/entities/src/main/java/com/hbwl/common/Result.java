package com.hbwl.common;

import lombok.Data;

@Data
public class Result {
    private int code;
    private String message;
    private Object data;

    public static Result success(Object data, String message) {
        Result result = new Result();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static Result success(String message){
        return success(null, message);
    }

    public static Result error(String message){
        Result result = new Result();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
}
