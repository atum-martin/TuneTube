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

    private static DocumentFile documentDir = null;

    public static void init(Context context){
        setSDCardDirectory(context);
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

        return null;
    }

    public static void setDocumentFile(DocumentFile documentFile, Uri treeUri) {
        FileUtils.documentDir = documentFile;
        DatabaseConnection.getInstance().persistDocumentUri(treeUri);
    }

    public static DocumentFile getDocumentDir(){
        return documentDir;
    }
}
