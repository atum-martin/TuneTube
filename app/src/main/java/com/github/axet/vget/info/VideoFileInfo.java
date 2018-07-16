package com.github.axet.vget.info;

import android.net.Uri;

import java.io.File;
import java.net.URL;

import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.ProxyInfo;

public class VideoFileInfo extends DownloadInfo {
    public File targetFile;

    public VideoFileInfo(Uri source) {
        super(source);
    }

    public VideoFileInfo(Uri source, ProxyInfo p) {
        super(source, p);
    }

    /**
     * set target file download for current DownloadInfo
     * 
     * @param file
     */
    public void setTarget(File file) {
        targetFile = file;
    }

    public File getTarget() {
        return targetFile;
    }
}
