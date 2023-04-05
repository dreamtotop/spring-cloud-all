package org.top.remote.transport.socket;

import lombok.extern.slf4j.Slf4j;
import org.top.config.CustomShutdownHook;
import org.top.config.RpcServiceConfig;
import org.top.factory.SingletonFactory;
import org.top.provider.ServiceProvider;
import org.top.provider.impl.ZkServiceProviderImpl;
import org.top.utils.thread.ThreadPoolFactoryUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static org.top.remote.transport.netty.server.NettyRpcServer.PORT;

@Slf4j
public class SocketRpcServer {

    private final ExecutorService threadPool;

    private final ServiceProvider serviceProvider;

    public SocketRpcServer(){
        threadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    public void start(){
        try(ServerSocket server = new ServerSocket()){
            String host =  InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, PORT));
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            while ((socket = server.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (Exception e){
            log.error("occur IOException:", e);
        }
    }
}
