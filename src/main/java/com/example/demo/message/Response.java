package com.example.demo.message;

import java.util.List;

import com.example.demo.model.User;



public class Response {

    String msg;
    List<User> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<User> getResult() {
        return result;
    }

    public void setResult(List<User> result) {
        this.result = result;
    }

}
