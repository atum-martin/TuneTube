package com.atum.tunetube.util;

import android.support.v4.provider.DocumentFile;

import com.atum.tunetube.sql.DatabaseConnection;
import com.atum.tunetube.youtube.YoutubeLink;

import java.io.File;

/**
 * Created by atum-martin on 13/08/2017.
 * This class is used for recreating the applications cache once its been deleted.
 */

public class IndexDiskFiles {

    private final DatabaseConnection db;

    public IndexDiskFiles(DatabaseConnection db){
        this.db = db;
    }

    public void indexDirectory(DocumentFile provider){
        if(provider == null || !provider.exists() || !provider.isDirectory() || provider.listFiles() == null)
            return;
        for(DocumentFile file : provider.listFiles()){

            if(!file.getName().endsWith(".m3u")){
                continue;
            }
            String title = file.getName().replaceAll("_", " ");
            title = title.substring(0, title.length()-4);
            YoutubeLink link = new YoutubeLink(title.replaceAll("'", ""), title);
            db.updatePlaytime(link);
        }
    }
}
