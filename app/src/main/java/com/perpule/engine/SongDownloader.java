package com.perpule.engine;

import android.content.Context;
import android.util.Log;
import com.perpule.downloadmanager.DownloadException;
import com.perpule.downloadmanager.DownloadService;
import com.perpule.util.Util;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mani on 28/02/18.
 */

public class SongDownloader {
  private static final SongDownloader ourInstance = new SongDownloader();

  public static SongDownloader getInstance() {
    return ourInstance;
  }

  private SongDownloader() {
  }

  public String downloadSongIfNotAvailableLocally(Context context,String requestUrl) throws IOException, DownloadException {
    int count;
    String idStr = "-1";
    try {
      URL url = new URL(requestUrl);

      String[] segments = url.getPath().split("/");
      idStr = segments[segments.length-1];

      File file = new File(context.getCacheDir(), idStr);
      Util.Logger("Already downloaded: " + file.exists());

      if(!file.exists()) {
        URLConnection conexion = url.openConnection();
        conexion.connect();
        int lenghtOfFile = conexion.getContentLength();
        Util.Logger("Length of file: " + lenghtOfFile);
        InputStream input = new BufferedInputStream(url.openStream());

        file.createNewFile();
        OutputStream output = new FileOutputStream(file);
        byte data[] = new byte[1024];
        while ((count = input.read(data)) != -1) {
          output.write(data, 0, count);
        }

        output.flush();
        output.close();
        input.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
      new DownloadException("Error Occured");
    }
    return idStr;
  }
}
