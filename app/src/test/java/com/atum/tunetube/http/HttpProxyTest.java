package com.atum.tunetube.http;

import android.util.Log;

import com.atum.tunetube.youtube.YoutubeHttp;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class HttpProxyTest {

    private static int portNumber;
    private static String address;

    @BeforeClass
    public static void setupProxy() throws IOException {
        HttpProxy proxy = new HttpProxy(false);
    }

    @Before
    public void setup(){
        PowerMockito.mockStatic(Log.class);
        portNumber = 8093;
        address = "localhost";
    }

    public String createBaseUrl(){
        return "http://"+address+":"+portNumber+"/";
    }

    @Test
    public void test1(){
        String url = createBaseUrl()+"?url=notaurl&title=test1";
        Assert.assertEquals(0, testUrl(url).size());
    }

    @Test
    public void test2(){
        String url = createBaseUrl()+"?url=notaurl";
        Assert.assertEquals("invalid title", testUrl(url).get(0));
    }

    @Test
    public void test3(){
        String url = createBaseUrl()+"?title=test3";
        Assert.assertEquals("invalid url", testUrl(url).get(0));
    }

    @Test
    public void test4(){
        String proxyUrl = "http://example.com/";
        String url = null;
        try {
            url = createBaseUrl()+"?url="+ URLEncoder.encode(proxyUrl, "UTF-8")+"&title=test4";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<String> htmlContent = testUrl(url);
        Assert.assertTrue(htmlContent.contains("    <h1>Example Domain</h1>"));
    }

    private List<String> testUrl(String url) {
        List<String> output = YoutubeHttp.getSingleton().openUrl(url);
        return output;
    }
}
