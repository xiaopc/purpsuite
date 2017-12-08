package purpsuite.model;

import purpsuite.controller.ProxyController;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class InProxyConPool extends ThrowToLogger implements Runnable{
    private static final String AUTHORED = "HTTP/1.1 200 Connection established\r\n\r\n";
    private static final String UNAUTHORED="HTTP/1.1 407 Unauthorized\r\n\r\n";
    private static final String SERVERERROR = "HTTP/1.1 500 Connection FAILED\r\n\r\n";

    private static List<Socket> pool = new LinkedList<Socket>();
    private Socket socket;
    private static boolean isUIHandling = false;
    private boolean exit = false;

    private static ProxyController uiController;
    private static HttpHeader header;

    public static void SetUIController(ProxyController p){
        uiController = p;
    }

    public static String getTopContent() {
        header.modifyProxy();
        return header.toString();
    }
    public static String getTopHost() { return header.getHost(); }
    public static String getTopPort() { return header.getPort(); }
    public static String getUri() { return header.getUri(); }
    public static String getTopMethod() { return header.getMethod(); }

    public void run(){
        Throw(new Exception("InProxyConPool start running"));
        while(!exit) {
            synchronized(pool) {
                while(pool.isEmpty() || isUIHandling) {
                    try {
                        pool.wait();
                    }
                    catch (InterruptedException e) {
                        Throw(e);
                    }
                }
                socket = pool.get(0);
            }
            handleConnection();
        }
    }

    public static void processRequest(Socket requestToHandle) {
        synchronized(pool) {
            pool.add(pool.size(), requestToHandle);
            pool.notifyAll();
            Throw(new Exception("Add socket to pool, now "+pool.size()));
        }
    }

    public void handleConnection() {
        try {
            socket.setSoTimeout(2000);
            socket.setKeepAlive(false);
            header = HttpHeader.readHeader(socket.getInputStream());
            if (header.getMethod().equals(HttpHeader.METHOD_CONNECT)){
                returnConnection(AUTHORED);
            } else {
                uiController.HandleNotify();
                isUIHandling = true;
            }
        }
        catch (IOException e) {
            //Throw(e);
        }
    }

    public void returnConnection(String back) throws IOException{
        try {
            OutputStream os = socket.getOutputStream();
            //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            os.write(back.getBytes("UTF-8"));
            os.flush();
            os.close();
            socket.getInputStream().close();
        } catch (Exception e){
            //Throw(e);
        }
        clearTopCon();
    }

    public void clearTopCon(){
        try {
            synchronized (pool) {
                header.close();
                socket.close();
                pool.remove(0);
                isUIHandling = false;
                pool.notifyAll();
                Throw(new Exception("Delete socket from pool, now "+pool.size()));
            }
        } catch (Exception e){
            Throw(e);
        }
    }

    public void close() { exit=true; }
}
