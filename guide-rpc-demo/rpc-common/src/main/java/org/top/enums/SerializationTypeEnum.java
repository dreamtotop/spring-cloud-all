package org.top.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 序列化方式
 */
@Getter
@AllArgsConstructor
public enum SerializationTypeEnum {

    KYRO((byte) 0x01, "kyro"),

    PROTOSTUFF((byte) 0x02, "protostuff"),

    HESSIAN((byte) 0X03, "hessian");

    private byte code;
    private String name;

    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
