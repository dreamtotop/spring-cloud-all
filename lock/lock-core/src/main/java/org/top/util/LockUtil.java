package org.top.util;

import com.sun.xml.internal.bind.v2.TODO;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.UUID;

public class LockUtil {

    public static String getLockId() {
        return simpleUUID();
    }


    /**
     * 去除-的uuid
     *
     * @return simpleUUID
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取本机网卡地址
     * @return
     */
    public static String getLocalMAC(){
        String localMac;
        try{
            InetAddress localHost = InetAddress.getLocalHost();
            // 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
            byte[] mac = NetworkInterface.getByInetAddress(localHost).getHardwareAddress();
            // 下面代码是把mac地址拼装成String
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                // mac[i] & 0xFF 是为了把byte转化为正整数
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            // 把字符串所有小写字母改为大写成为正规的mac地址并返回
            localMac = sb.toString();
        } catch (Exception ex){
            // 获取mac地址应该要做到最大化兼容
            localMac = UUID.randomUUID().toString();
        }
        return localMac.toUpperCase().replace("-", "");
    }
}
