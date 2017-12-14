package purpsuite.model;

import purpsuite.controller.ProxyController;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class OutConnection extends ThrowToLogger implements Runnable{
    private static ProxyController UIController;
    private int backid;
    private String host;
    private String port;
    private String inString;
    private HttpHeader httpHeader;

    public OutConnection(int id, String host, String port, String inString) {
        this.backid = id;
        this.host = host;
        this.port = port;
        this.inString = inString;
    }

    public static void setUIController(ProxyController UIController) {
        OutConnection.UIController = UIController;
    }

    public void run(){
        try {
            connect();
            UIController.HandleBack(backid, getRespContent());
        } catch ( Exception e ) {
            if (e instanceof NullPointerException) UIController.HandleBackFail(backid);
            Throw(e);
        }
    }

    private void connect(){
        try {
            Socket socket = new Socket(host, Integer.parseInt(port));
            socket.setSoTimeout(1000);
            socket.setKeepAlive(true);
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            os.write(inString.getBytes(Charset.forName("UTF-8")));
            os.flush();
            httpHeader = HttpHeader.readHeader(is);
            os.close();
            is.close();
            socket.close();
        } catch (Exception e){
            Throw(e);
        }
    }

    public String getRespContent() throws NullPointerException{
        return httpHeader.toString();
    }
}
