package com.atum.tunetube.http;

import com.atum.tunetube.util.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by atum-martin on 11/06/2017.
 */

public class HttpProxy extends NanoHTTPD {

    public HttpProxy() throws IOException {
        super(8093);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> parms = session.getParms();
        if (parms.get("url") == null) {
            return newFixedLengthResponse("invalid url");
        }
        if (parms.get("title") == null) {
            return newFixedLengthResponse("invalid title");
        }
        String url = parms.get("url");
        String title = parms.get("title");
        System.out.println("receiving http buffer for: "+title+ " "+url);
        try {

            InputStream in = FileUtils.getInputStreamForTitle(title);
            if(in != null){
                for(Map.Entry<String, String> e : session.getHeaders().entrySet()){
                    System.out.println("headers: "+e.getKey()+" "+e.getValue());
                }
            } else {
                HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
                if (parms.get("Range") != null) {
                    http.setRequestProperty("Range", parms.get("Range"));
                    System.out.println("range: " + parms.get("Range"));
                }
                String filePath = FileUtils.getLocationForTitle(title);
                FileOutputStream fileOut = new FileOutputStream(filePath);
                in = new RelayInputStream(http.getInputStream(), fileOut);
            }
            return newChunkedResponse(Response.Status.OK, session.getHeaders().get("Content-Type"), in);
        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse("exception: "+e.getCause().toString());
        }

    }
}
