package org.top.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum RpcResponseCodeEnum {

    SUCCESS(200, "The remote call is successful"),

    FAIL(500, "The remote call is fail");

    private int code;

    private String message;
}
