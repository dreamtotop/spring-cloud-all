package org.top.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.top.registry.zk.util.CuratorUtils;
import org.top.remote.transport.netty.server.NettyRpcServer;
import org.top.utils.thread.ThreadPoolFactoryUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class CustomShutdownHook {

    private static final Logger log = LoggerFactory.getLogger(CustomShutdownHook.class);

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook(){
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll(){
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try{
                InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), socketAddress);
            } catch (Exception ex){
                log.error("ShutdownHook error {}", ex.getMessage());
            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }

}
