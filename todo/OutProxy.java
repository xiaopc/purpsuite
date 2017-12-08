package purpsuite.model;

import java.io.IOException;
import java.net.*;
import java.io.*;

public class OutProxy {
    private Proxy proxy;
    private URL url;

    public OutProxy() throws IOException {
        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8080));
    }

    public void setURL(String s) throws MalformedURLException {
        url = new URL(s);
    }

    public void connect() throws NullPointerException, IOException{
        HttpURLConnection action = (HttpURLConnection) url.openConnection(proxy);
        InputStream in = action.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String lin = System.getProperty("line.separator");
        for (String temp = br.readLine(); temp != null; temp = br.readLine()) {
            sb.append(temp + lin);
        }
        br.close();
        in.close();
        System.out.println(sb);
    }
}
