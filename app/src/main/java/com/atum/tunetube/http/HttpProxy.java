package com.atum.tunetube.http;

import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.atum.tunetube.Constants;
import com.atum.tunetube.MainActivity;
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

    private boolean cacheSupport = true;

    public HttpProxy(boolean cacheSupport) throws IOException {
        super(8093);
        this.cacheSupport = cacheSupport;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public HttpProxy() throws IOException {
        this(true);
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
        Log.i(Constants.HTTP_TAG,"receiving http buffer for: "+title+ " "+url);
        try {

            InputStream in = null;
            HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
            if (parms.get("Range") != null) {
                http.setRequestProperty("Range", parms.get("Range"));
                Log.i(Constants.HTTP_TAG,"range: " + parms.get("Range"));
            }
            if(cacheSupport) {
                OutputStream fileOut = null;
                if (FileUtils.getDocumentDir() != null) {
                    DocumentFile newFile = FileUtils.getDocumentDir().createFile("audio/orbis", FileUtils.getStringForTitle(title));
                    fileOut = MainActivity.getInstance().getContentResolver().openOutputStream(newFile.getUri());
                    in = new RelayInputStream(http.getInputStream(), fileOut);
                }
            }
            if(in == null){
                in = http.getInputStream();
            }
            return newChunkedResponse(Response.Status.OK, session.getHeaders().get("Content-Type"), in);
        } catch (IOException e) {
            e.printStackTrace();
            return newFixedLengthResponse("exception: "+e.getCause().toString());
        }

    }
}
