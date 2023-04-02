package org.top.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * rpc 配置
 */
@Getter
@AllArgsConstructor
public enum RpcConfigEnum {

    RPC_CONFIG_PATH("rpc.properties"),

    ZK_ADDRESS("rpc.zookeeper.address");

    private String propertyValue;
}
