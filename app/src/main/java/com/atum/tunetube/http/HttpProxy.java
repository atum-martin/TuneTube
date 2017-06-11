package com.atum.tunetube.http;

import android.os.Environment;

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
        if (parms.get("url") != null) {
            String url = parms.get("url");
            String title = parms.get("title");
            try {
                HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
                FileOutputStream fileOut = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+title.replaceAll(" ", "_")+".m3u");
                InputStream in = new RelayInputStream(http.getInputStream(), fileOut);
                return newChunkedResponse(Response.Status.OK, session.getHeaders().get("Content-Type"), in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newFixedLengthResponse("invalid url");
    }

    private class RelayInputStream extends InputStream
    {
        private InputStream mInputStream = null;
        private OutputStream mOutputStream = null;
        private long total = 0L;

        public RelayInputStream(InputStream is, OutputStream os)
        {
            mInputStream = is;
            mOutputStream = os;
        }

        @Override
        public int available() throws IOException
        {
            System.out.println("available available = " + mInputStream.available());
            mInputStream.mark(mInputStream.available());
            return mInputStream.available();
        }

        @Override
        public int read(byte[] buffer) throws IOException
        {
            System.out.println("read buffer = " + buffer.toString());
            int read = mInputStream.read(buffer);
            mOutputStream.write(buffer, 0, read);
            return read;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException
        {
            int read = mInputStream.read(buffer, offset, length);
            total += read;
            System.out.println("read buffer = " + buffer.toString() + "; offset = " + offset + "; length = " + length+" total: "+(total / 1024L));
            mOutputStream.write(buffer, offset, read);
            mOutputStream.flush();
            return read;
        }

        @Override
        public int read() throws IOException
        {
            System.out.println("read no data");
            int b = mInputStream.read();
            mOutputStream.write(b);
            return b;
        }

        @Override
        public void close() throws IOException {
            System.out.println("http proxy conn closed");
            mOutputStream.flush();
            mOutputStream.close();
            mInputStream.close();
        }
    }


}
