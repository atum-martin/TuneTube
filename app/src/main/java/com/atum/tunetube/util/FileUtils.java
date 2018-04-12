package com.atum.tunetube.util;

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

    public static String getSDCardDirectory(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+DIRECTORY_NAME;
    }

    private static String cacheTrackDirectory = getSDCardDirectory();

    static {
        File f = new File(getSDCardDirectory());
        if(!f.exists() && !createDirIfNotExists(getSDCardDirectory())){
            cacheTrackDirectory = null;
        }
    }

    public static void setCacheDirectoryPath(String path){
        if(cacheTrackDirectory == null) {
            cacheTrackDirectory = path+DIRECTORY_NAME;
            createDirIfNotExists(cacheTrackDirectory);
        }
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
        if(!f.exists())
            return f.mkdir();
        return true;
    }

}
