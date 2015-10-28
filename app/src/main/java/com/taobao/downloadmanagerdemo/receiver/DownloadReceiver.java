package com.taobao.downloadmanagerdemo.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownloadReceiver extends BroadcastReceiver {
    private Callback mCallback;
    public DownloadReceiver(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
            if(mCallback != null){
                mCallback.callback(downId);
            }
        }
    }


    public interface Callback{
        public void callback(long id);
    }
}
