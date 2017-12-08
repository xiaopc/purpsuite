package purpsuite.model;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpHeader extends ThrowToLogger {

    private Map<String,String> header=new LinkedHashMap<String,String>();
    private String method;
    private String host;
    private String port;
    private String uri;
    private BufferedReader br;

    public static final int MAXLINESIZE = 4096;
    public static final String METHOD_GET="GET";
    public static final String METHOD_POST="POST";
    public static final String METHOD_CONNECT="CONNECT";
    private static final String RESHEAD_HTTP="HTTP";

    public static HttpHeader readHeader(InputStream in) throws IOException {
        HttpHeader header = new HttpHeader();
        StringBuilder sb = new StringBuilder();

        header.br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        int c = 0;
        while ((char)(c = header.br.read()) != '\n') {
            sb.append((char)c);
            if (sb.length() == MAXLINESIZE) {
                break;
            }
        }

        if (header.addHeaderMethod(sb.toString()) != null) {
            int countpart = 1;
            try {
                do {
                    sb = new StringBuilder();
                    while (true){
                        c = header.br.read();
                        if ((countpart==1 && (char)c=='\n') || (countpart==2 && c==-1)) break;
                        sb.append((char)c);
                    }
                    if (countpart==1 && sb.length() > 1) {
                        header.addHeaderString(sb.toString());
                    } else if (countpart==1) {
                        countpart++;
                    } else break;
                } while (true);
            } catch (SocketTimeoutException e) {}
        }
        header.setHeader("PS-Content", sb.toString());

        return header;
    }

    private void addHeaderString(String str){
        str=str.trim();
        Pattern p = Pattern.compile(":");
        Matcher m = p.matcher(str);
        m.find();
        String key = str.substring(0,m.start());
        String val = str.substring(m.start()+1,str.length()).trim();
        header.put(key,val);

        if(key.equals("Host")){
            String[] sp = val.split(":");
            host=sp[0];
            if(method.endsWith(METHOD_CONNECT)){
                port=(sp.length==2)?sp[1]:"443";
            } else if(method.endsWith(METHOD_GET)||method.endsWith(METHOD_POST)){
                port=(sp.length==2)?sp[1]:"80";
            }
        }
    }

    private String addHeaderMethod(String str){
        header.put("PS-Method",str.trim());
        if(str.startsWith(METHOD_CONNECT)){
            method=METHOD_CONNECT;
        } else if(str.startsWith(METHOD_GET)){
            method=METHOD_GET;
        } else if(str.startsWith(METHOD_POST)){
            method=METHOD_POST;
        } else if(str.startsWith(RESHEAD_HTTP)){
            method=RESHEAD_HTTP;
        }
        return method;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        Iterator it = header.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry e = (Map.Entry) it.next();
            if (e.getKey().equals("PS-Content")) continue;
            else if (!e.getKey().equals("PS-Method")){
                sb.append(e.getKey()).append(": ");
            }
            sb.append(e.getValue()).append("\r\n");
        }
        sb.append("\r\n");
        if (header.containsKey("PS-Content"))
            sb.append(header.get("PS-Content"));
        return sb.toString();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUri() { return header.get("PS-Method").split(" ")[1]; }

    public void setHeader(String key, String value){
        header.put(key,value);
    }

    public String getHeader(String key){
        return header.get(key);
    }

    public void delHeader(String key) { header.remove(key); }

    public void modifyProxy(){
        delHeader("Proxy-Connection");
        if (header.containsKey("Accept-Encoding")) header.remove("Accept-Encoding");
        String[] method = header.get("PS-Method").split(" ");
        setHeader("PS-Method",method[0]+" "+method[1].replaceAll(".*//([-0-9a-zA-Z:.]+?)+","")+" "+method[2]);
    }

    public void close(){ try{ br.close(); } catch (Exception e) {Throw(e);} }
}
