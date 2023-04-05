package org.top.remote.handler;

import lombok.extern.slf4j.Slf4j;
import org.top.exception.RpcException;
import org.top.factory.SingletonFactory;
import org.top.provider.ServiceProvider;
import org.top.provider.impl.ZkServiceProviderImpl;
import org.top.remote.dto.RpcRequest;

import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {

    private final ServiceProvider serviceProvider;

    public RpcRequestHandler(){
        serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    /**
     * Processing rpcRequest: call the corresponding method, and then return the method
     * @param rpcRequest
     * @return
     */
    public Object handle(RpcRequest rpcRequest){
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    public Object invokeTargetMethod(RpcRequest rpcRequest, Object service){
        Object result;
        try{
            Method method = service.getClass().getDeclaredMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (Exception e){
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
