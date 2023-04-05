package org.top.config;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RpcServiceConfig {

    /**
     * 服务版本
     */
    private String version = "";

    /**
     * 服务组
     */
    private String group = "";

    /**
     * 服务
     */
    private Object service;

    public String getRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

}
