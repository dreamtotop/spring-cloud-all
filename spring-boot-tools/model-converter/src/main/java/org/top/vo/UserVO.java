package org.top.vo;


import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserVO {

    @JsonProperty(value = "id")
    private Long userId;


    @JSONField(name = "userName")
    @JsonProperty(value = "userName")
    private String name;

    private String password;

    public  UserVO(){

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
