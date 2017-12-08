package purpsuite.model;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import purpsuite.controller.ProxyController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class InProxy extends ThrowToLogger implements Runnable {
    public class InPackageInfo {
        private String itemHost;
        private String itemContent;
        private String port;
        private String method;
        private String uri;

        public InPackageInfo(String itemHost, String itemContent, String port, String uri, String method) {
            this.itemHost = itemHost;
            this.itemContent = itemContent;
            this.uri = uri;
            this.port = port;
            this.method = method;
        }

        public String getItemHostName() {
            return itemHost;
        }

        public String getItemContent() {
            return itemContent;
        }

        public String getPort() { return port; }

        public String getMethod() { return method; }

        public String getUri() { return uri; }
    }

    ;
    private ServerSocket server;
    private InProxyConPool pool;
    private Thread PoolThread;

    private int port;
    private boolean isSet;
    private boolean isNew;
    private boolean exit=false;
    StringBuffer LatestRecived;

    public InProxy(ProxyController uiController) {
        InProxyConPool.SetUIController(uiController);
        isSet = false;
    }

    public boolean getIsSet() {
        return isSet;
    }

    public void initProxy(int port) {
        this.port = port;
        try {
            server = new ServerSocket(port);
            pool = new InProxyConPool();
            PoolThread = new Thread(pool);
            PoolThread.start();
            isSet = true;
        } catch (Exception e) {
            isSet = false;
            Throw(e);
        }
    }

    public void close(){ pool.close(); exit=true; }

    public void run() {
        action();
    }

    private void handleConnection(Socket connectionToHandle) {
        InProxyConPool.processRequest(connectionToHandle);
    }


    public void action() {
        Socket inComingConn = null;
        while (!exit) {
            try {
                inComingConn = server.accept();
                handleConnection(inComingConn);
            } catch (IOException e) {
                Throw(e);
            }
        }
    }

    public InPackageInfo getPacketInfo() {
        return new InPackageInfo(InProxyConPool.getTopHost(), InProxyConPool.getTopContent(), InProxyConPool.getTopPort(), InProxyConPool.getUri(), InProxyConPool.getTopMethod());
    }

    public void clearTopCon() {
        pool.clearTopCon();
    }

    public void returnConnection(String back) throws IOException{
        pool.returnConnection(back);
    }
}