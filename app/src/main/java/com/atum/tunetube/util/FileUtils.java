package com.atum.tunetube.util;

import android.content.Context;
import android.os.Environment;

import com.github.axet.vget.vhs.YouTubeParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mchapman on 07/12/17.
 */

public class FileUtils {

    private static String DIRECTORY_NAME = "/TestTube";

    private static String cacheTrackDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+DIRECTORY_NAME;

    public static void init(Context context){
        cacheTrackDirectory = getSDCardDirectory(context);
        File f = new File(cacheTrackDirectory);
        if(!f.exists() && !createDirIfNotExists(cacheTrackDirectory)){
            cacheTrackDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+DIRECTORY_NAME;
            f = new File(cacheTrackDirectory);
            createDirIfNotExists(f.getAbsolutePath());
        }
    }

    private static String getSDCardDirectory(Context context){
        return GetExternalStorage.getExternalStoragePath(context, true)+DIRECTORY_NAME;
    }

    public static String getWorkingDirectory(){
        return cacheTrackDirectory;
    }

    public static String getLocationForTitle(String title) {
        return getWorkingDirectory()+"/"+title.replaceAll(" ", "_").replaceAll("/", "_")+".m3u";
    }

    public static YouTubeParser.VideoDownload checkForLocalCachedCopy(String title) {
        String filePath = getLocationForTitle(title);
        File f = new File(filePath);
        if(f.exists()) {
            try {
                System.out.println("local file found for: "+title+" '"+f.getAbsolutePath()+"'");
                return new YouTubeParser.VideoDownload(null, new URL("file://"+f.getAbsolutePath()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static InputStream getInputStreamForTitle(String title){
        String dir = getWorkingDirectory();
        String filePath = getLocationForTitle(title);
        File f = new File(filePath);
        if(f.exists()){
            try {
                return new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static boolean createDirIfNotExists(String dir) {
        File f = new File(dir);
        System.out.println("creating sd dir: "+dir);
        if(!f.exists())
            return f.mkdir();
        return true;
    }

}
