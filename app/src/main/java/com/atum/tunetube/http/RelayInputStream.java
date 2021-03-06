package com.atum.tunetube.http;

import android.util.Log;

import com.atum.tunetube.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mchapman on 07/12/17.
 * This class allows the user to open an input stream while also redirecting the contents
 * to a file in parallel during the stream.
 */

class RelayInputStream extends InputStream
{
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;
    private long total = 0L;

    RelayInputStream(InputStream is, OutputStream os)
    {
        mInputStream = is;
        mOutputStream = os;
    }

    @Override
    public int available() throws IOException
    {
        checkStreams();
        Log.i(Constants.HTTP_TAG,"available available = " + mInputStream.available());
        mInputStream.mark(mInputStream.available());
        return mInputStream.available();
    }

    private void checkStreams() throws IOException {
        if(mInputStream == null){
            throw new IOException("input stream cannot be null.");
        }
        if(mOutputStream == null){
            throw new IOException("ouput stream cannot be null.");
        }
    }

    @Override
    public int read(byte[] buffer) throws IOException
    {
        Log.i(Constants.HTTP_TAG,"read buffer;");
        return read(buffer, 0, buffer.length);
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException
    {
        checkStreams();
        int read = mInputStream.read(buffer, offset, length);
        total += read;
        Log.i(Constants.HTTP_TAG,"read buffer offset = " + offset + "; length = " + length+" total: "+(total / 1024L));
        mOutputStream.write(buffer, offset, read);
        mOutputStream.flush();
        return read;
    }

    @Override
    public int read() throws IOException
    {
        checkStreams();
        Log.i(Constants.HTTP_TAG,"read no data");
        int b = mInputStream.read();
        mOutputStream.write(b);
        return b;
    }

    @Override
    public void close() throws IOException {
        checkStreams();
        Log.i(Constants.HTTP_TAG,"http proxy conn closed");
        mOutputStream.flush();
        mOutputStream.close();
        mInputStream.close();
    }
}