package org.top.loadbalance;

import org.top.remote.dto.RpcRequest;
import org.top.utils.CollectionUtil;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance{

    @Override
    public String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest) {

        if(CollectionUtil.isEmpty(serviceUrlList)){
            return null;
        }

        if(serviceUrlList.size() == 1){
            return serviceUrlList.get(0);
        }
        return doSelect(serviceUrlList, rpcRequest);
    }


    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest);
}



