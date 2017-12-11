package com.atum.tunetube.http;

import org.junit.Assert;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mchapman on 11/12/17.
 */

public class HttpRelayStreamTest {

    private byte[] createRandomArray(){
        byte[] arr = new byte[128];
        for(int i = 0; i < arr.length; i++){
            arr[i] = (byte) (-128 + (Math.random() * 256));
        }
        return arr;
    }

    @Test
    public void testRelayInputStream(){
        byte[] arr = createRandomArray();
        InputStream in = new ByteArrayInputStream(arr);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RelayInputStream relay = new RelayInputStream(in , out);

        byte[] inputStreamData = new byte[arr.length];
        try {
            //populate inputStreamData with data from the relayInputStream
            relay.read(inputStreamData);
            //test that the inputstream data is as expected.
            Assert.assertArrayEquals(arr, inputStreamData);
            //test that the output stream data is as expected.
            Assert.assertArrayEquals(arr, out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void finishCodeCoverage(){
        byte[] arr = createRandomArray();
        InputStream in = new ByteArrayInputStream(arr);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        RelayInputStream relay = new RelayInputStream(in , out);

        try {
            Assert.assertTrue(relay.read() != -1);
            Assert.assertEquals(relay.available(), arr.length-1);
            relay.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test(expected = IOException.class)
    public void testRelayInputStreamNulls1() throws IOException {
        RelayInputStream relay = new RelayInputStream(null , null);
        relay.read();
    }

    @Test(expected = IOException.class)
    public void testRelayInputStreamNulls2() throws IOException {
        RelayInputStream relay = new RelayInputStream(null , null);
        relay.read(new byte[1024]);
    }

    @Test(expected = IOException.class)
    public void testRelayInputStreamNulls3() throws IOException {
        RelayInputStream relay = new RelayInputStream(null , null);
        relay.available();
    }

    @Test(expected = IOException.class)
    public void testRelayInputStreamNulls4() throws IOException {
        RelayInputStream relay = new RelayInputStream(null , null);
        relay.close();
    }

    @Test(expected = IOException.class)
    public void testRelayInputStreamNulls5() throws IOException {
        RelayInputStream relay = new RelayInputStream(new ByteArrayInputStream(new byte[1]) , null);
        relay.close();
    }
}
