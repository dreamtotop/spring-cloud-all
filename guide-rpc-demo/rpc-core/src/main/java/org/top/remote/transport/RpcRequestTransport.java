package org.top.remote.transport;

import org.top.extension.SPI;
import org.top.remote.dto.RpcRequest;

@SPI
public interface RpcRequestTransport {

    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);

}
