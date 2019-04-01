package com.lifesense.kuafu.crawler.core.processor.plugins.proxy;

import org.apache.http.HttpHost;
import org.eclipse.jetty.client.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class LSProxyUtil {
    private static final Logger logger = LoggerFactory.getLogger(LSProxyUtil.class);
    // TODO 改为单例
    private InetAddress localAddr;

    public InetAddress getLocalAddr() {
        return localAddr;
    }

    public void setLocalAddr(InetAddress localAddr) {
        this.localAddr = localAddr;
    }

    public static LSProxyUtil init() {
        LSProxyUtil lsProxyUtil = new LSProxyUtil();
        Enumeration<InetAddress> localAddrs;
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                if (networkInterface.isVirtual())
                    continue;
                if (!networkInterface.isUp())
                    continue;
                localAddrs = networkInterface.getInetAddresses();
                while (localAddrs.hasMoreElements()) {
                    InetAddress tmp = localAddrs.nextElement();
                    if (!tmp.isLoopbackAddress() && !tmp.isLinkLocalAddress() && !(tmp instanceof Inet6Address)) {
                        lsProxyUtil.setLocalAddr(tmp);
                        logger.info("local IP:" + lsProxyUtil.getLocalAddr().getHostAddress());
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failure when init ProxyUtil", e);
            logger.error("choose NetworkInterface\n" + getNetworkInterface());
        }
        return lsProxyUtil;
    }

    public boolean validateProxy2(HttpHost p) {
        boolean isReachable = false;
        Socket socket = null;
        try {
            InetAddress ad = InetAddress.getByName(p.getAddress().getHostAddress());
            boolean state = ad.isReachable(1000);//测试是否可以达到该地址 ,判断ip是否可以连接 1000是超时时间
            if (!state) {
                throw new IOException();
            }
            isReachable = true;
        } catch (Exception e) {
            logger.warn("FAILRE - CAN not connect! Local: " + localAddr.getHostAddress() + " remote: " + p);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.warn("Error occurred while closing socket of validating proxy", e);
                }
            }
        }
        return isReachable;
    }

    public boolean validateProxy(HttpHost p) {
        if (localAddr == null) {
            logger.error("cannot get local ip");
            return false;
        }
        boolean isReachable = false;
        Socket socket = null;
        try {
            socket = new Socket();
            socket.bind(new InetSocketAddress(localAddr, 0));
            InetSocketAddress endpointSocketAddr = new InetSocketAddress(p.getAddress().getHostAddress(), p.getPort());
            socket.connect(endpointSocketAddr, 3000);
            logger.debug("SUCCESS - connection established! Local: " + localAddr.getHostAddress() + " remote: " + p);
            isReachable = true;
        } catch (IOException e) {
            logger.warn("FAILRE - CAN not connect! Local: " + localAddr.getHostAddress() + " remote: " + p);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.warn("Error occurred while closing socket of validating proxy", e);
                }
            }
        }
        return isReachable;
    }

    private static String getNetworkInterface() {
        String networkInterfaceName = "";
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        while (enumeration.hasMoreElements()) {
            NetworkInterface networkInterface = enumeration.nextElement();
            networkInterfaceName += networkInterface.toString() + '\n';
            Enumeration<InetAddress> addr = networkInterface.getInetAddresses();
            while (addr.hasMoreElements()) {
                networkInterfaceName += "\tip:" + addr.nextElement().getHostAddress() + "\n";
            }
        }
        return networkInterfaceName;
    }
}