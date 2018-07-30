package com.atum.tunetube.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.atum.tunetube.Constants;
import com.atum.tunetube.sql.DatabaseConnection;
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
    private static DocumentFile documentDir = null;

    public static void init(Context context){
        setSDCardDirectory(context);
        File f = new File(cacheTrackDirectory);
        if(!f.exists() && !createDirIfNotExists(cacheTrackDirectory)){
            cacheTrackDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+DIRECTORY_NAME;
            f = new File(cacheTrackDirectory);
            createDirIfNotExists(f.getAbsolutePath());
        }
    }

    private static void setSDCardDirectory(Context context){
        Uri mediaStorageDir = DatabaseConnection.getInstance().getMediaDocumentUri();
        if(mediaStorageDir == null)
            return;
        try {
            documentDir = DocumentFile.fromTreeUri(context, mediaStorageDir);
        } catch(IllegalArgumentException e){
            //uri not found.
            e.printStackTrace();
        }
    }

    public static String getWorkingDirectory(){
        return cacheTrackDirectory;
    }

    public static String getLocationForTitle(String title) {
        return getWorkingDirectory()+"/"+getStringForTitle(title);
    }

    public static String getStringForTitle(String title) {
        return title.replaceAll(" ", "_").replaceAll("/", "_")+".m3u";
    }

    public static YouTubeParser.VideoDownload checkForLocalCachedCopy(String title) {
        //Check for local SDCard copy.
        if(documentDir != null) {
            DocumentFile media = documentDir.findFile(getStringForTitle(title));
            if(media != null) {
                Log.i(Constants.TAG,"local sd-file found for: " + title + " '" + media.getUri() + "'");
                return new YouTubeParser.VideoDownload(null, media.getUri());
            }
        }
        //check internal cache files
        String filePath = getLocationForTitle(title);
        File f = new File(filePath);
        if(f.exists()) {
            Log.i(Constants.TAG,"local file found for: "+title+" '"+f.getAbsolutePath()+"'");
            return new YouTubeParser.VideoDownload(null, Uri.parse("file://"+f.getAbsolutePath()));
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
        Log.i(Constants.TAG,"creating sd dir: "+dir);
        if(!f.exists())
            return f.mkdir();
        return true;
    }

    public static void setDocumentFile(DocumentFile documentFile, Uri treeUri) {
        FileUtils.documentDir = documentFile;
        DatabaseConnection.getInstance().persistDocumentUri(treeUri);
    }

    public static DocumentFile getDocumentDir(){
        return documentDir;
    }
}
