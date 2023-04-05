package org.top.provider;

import org.top.config.RpcServiceConfig;

/**
 * store and provide service object.
 */
public interface ServiceProvider {

    void addService(RpcServiceConfig rpcServiceConfig);

    Object getService(String rpcServiceName);

    void publishService(RpcServiceConfig rpcServiceConfig);
}
