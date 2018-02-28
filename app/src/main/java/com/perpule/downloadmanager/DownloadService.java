package com.perpule.downloadmanager;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import com.perpule.engine.SongDownloader;
import com.perpule.util.Util;

public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "DownloadService";

    private int waitingIntentCount = 0;

    public DownloadService() {
        super(DownloadService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        waitingIntentCount++;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Util.Logger(TAG, "Service Started!: "+waitingIntentCount--);

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");

        Bundle bundle = new Bundle();

        Util.Logger(TAG, "Try downloading "+url);

        if (!TextUtils.isEmpty(url)) {
            /* Update UI: Download Service is Running */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {
                SongDownloader songDownloader = SongDownloader.getInstance();
                String results = songDownloader.downloadSongIfNotAvailableLocally(getApplicationContext(),url);

                /* Sending result back to activity */
                if (!TextUtils.isEmpty(results)) {
                    bundle.putString("result", results);
                    receiver.send(STATUS_FINISHED, bundle);
                }
            } catch (Exception e) {

                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        Util.Logger(TAG, "Service will Stop if no queue");
    }
}