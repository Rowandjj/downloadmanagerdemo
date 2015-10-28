package com.taobao.downloadmanagerdemo;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.taobao.downloadmanagerdemo.receiver.DownloadReceiver;


/**
 * Created by rowandjj on 15/10/26.
 */
public class Download {

    private Download() {
    }

    private static long id;


    private static final String URL = "http://pkg.fir.im/5221a1dc928944c84341b36af17e1fd48308a438?attname=Meizhi_v2.3.1_2015-10-10_fir.apk_2.3.1.apk";
    public static final int PERMISSION_REQ_CODE = 10;

    private static DownloadReceiver mReceiver;

    public static void init(final Context context){
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mReceiver = new DownloadReceiver(new DownloadReceiver.Callback() {
            @Override
            public void callback(long id) {
                if(Download.id == id){
                    DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(id);
                    Cursor cursor = manager.query(query);

                    if(cursor.moveToFirst()){
                        String filename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Toast.makeText(context,"下载完成...name:"+filename+",uri:"+fileUri,Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context,"下载完成...",Toast.LENGTH_SHORT).show();
                    }

                    cursor.close();

                }
            }
        });
        context.registerReceiver(mReceiver,filter);

    }

    public static void uninit(Context context){
        if(mReceiver != null){
            context.unregisterReceiver(mReceiver);
        }
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean download(Context context) {
        if(Build.VERSION.SDK_INT <= 22 || checkPermission(context)){
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URL));

            //指定保存地址
            request.setDestinationInExternalPublicDir("chuyi", "meizhi.apk");

            //设置允许下载的网络状况
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            //设置通知栏的行为
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            id = manager.enqueue(request);

            return true;
        }else {
            askPermission(context);
            return false;
        }
    }


    public static void remove(Context context){
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if(id != 0){
            manager.remove(id);
        }
    }

    public static void askPermission(Context context) {
        if (!checkPermission(context)) {
            //判断是否需要 向用户解释，为什么要申请该权限
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(context,"没有写sd卡权限...",Toast.LENGTH_SHORT).show();
            }else{
                //如果用户勾选了不再提醒授权，可以通过这个方法提醒用户去设置页面更改权限
            }
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
        }
    }

    private static boolean checkPermission(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void performPermissionResult(int requestCode,String permissions[], int[] grantResults,Context context) {
        if(requestCode == PERMISSION_REQ_CODE){
           if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               Toast.makeText(context,"权限获取成功..",Toast.LENGTH_SHORT).show();
           }else{
               Toast.makeText(context,"权限获取失败..",Toast.LENGTH_SHORT).show();
           }
        }
    }

}
