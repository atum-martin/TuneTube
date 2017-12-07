package com.atum.tunetube.http;

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
        System.out.println("available available = " + mInputStream.available());
        mInputStream.mark(mInputStream.available());
        return mInputStream.available();
    }

    @Override
    public int read(byte[] buffer) throws IOException
    {
        System.out.println("read buffer;");
        int read = mInputStream.read(buffer);
        mOutputStream.write(buffer, 0, read);
        return read;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException
    {
        int read = mInputStream.read(buffer, offset, length);
        total += read;
        System.out.println("read buffer offset = " + offset + "; length = " + length+" total: "+(total / 1024L));
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