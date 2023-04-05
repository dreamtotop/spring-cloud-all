package org.top.loadbalance;

import org.top.extension.SPI;
import org.top.remote.dto.RpcRequest;

import java.util.List;

@SPI
public interface LoadBalance {

    /**
     * 获取服务的负载均衡器
     * @param serviceUrlList
     * @param rpcRequest
     * @return
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);

}
