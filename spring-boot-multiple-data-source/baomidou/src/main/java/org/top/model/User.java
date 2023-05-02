package org.top.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class User {

    private Long id;

    private String userName;

    private String password;

    private Date createTime;

    private Date updateTime;
}
