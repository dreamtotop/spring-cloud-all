package org.top.remote.transport.socket;

import lombok.extern.slf4j.Slf4j;
import org.top.exception.RpcException;
import org.top.extension.ExtensionLoader;
import org.top.factory.SingletonFactory;
import org.top.registry.ServiceDiscovery;
import org.top.remote.dto.RpcRequest;
import org.top.remote.transport.RpcRequestTransport;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class SocketRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient(){
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        InetSocketAddress address = serviceDiscovery.lookupService(rpcRequest);
        try(Socket socket = new Socket()){
            socket.connect(address);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // Send data to the server through the output stream
            objectOutputStream.writeObject(rpcRequest);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // Read RpcResponse from the input stream
            return objectInputStream.readObject();
        } catch (Exception e){
            throw new RpcException("调用服务失败:", e);
        }
    }
}
