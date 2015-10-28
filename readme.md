#Downloadmanager使用

> DownloadManager是android提供的一个下载管理器

使用方法:

1. 创建实例:
  
  ```
   DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
  ```

2. 创建下载任务:

  ```
  DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URL));
  //指定保存地址
  request.setDestinationInExternalPublicDir("chuyi", "meizhi.apk");
  //设置允许下载的网络状况
  request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
  //设置通知栏的行为
  request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
  //通过id唯一标识此下载任务
  long id = manager.enqueue(request);
  ```

3. 删除下载任务:

  ```
  manager.remove(id);
  ```

4. 查询下载任务:

  ```
    DownloadManager.Query query = new DownloadManager.Query();
    query.setFilterById(id);
    Cursor cursor = manager.query(query);
  
    if(cursor.moveToFirst()){
        String filename = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
        String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
        Toast.makeText(context,"下载完成...name:"+filename+",uri:"+fileUri,Toast.LENGTH_SHORT).show();
    }else {
      //TODO
    }
    cursor.close();
  ```

5. 查询下载进度:

  ```
	DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
	Cursor c = null;
	try {
		c = downloadManager.query(query);
		if (c != null && c.moveToFirst()) {
			int downloadedBytes = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
			int totalBytes = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
			int state = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
		}
	} finally {
		if (c != null) {
			c.close();
		}
	}
 
  ```

6. 监听下载结束通知:
  
  >可以通过接收DownloadManager.ACTION_DOWNLOAD_COMPLETE广播来监听下载结束的通知
  
  ```
   IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mReceiver = new BroadcastReceiver(){
          public void onReceive(Context c,Intent i){
              long downId = i.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
          }
        }
        context.registerReceiver(mReceiver,filter);
  ```

7. 监听下载进度:

  >主要有三种方案：1.FileReceiver 2.ContentObserver 3.定时任务
    
  ```
  class DownloadChangeObserver extends ContentObserver {

  	public DownloadChangeObserver(){
  		super(handler);
  	}
  
  	@Override
  	public void onChange(boolean selfChange) {
  		//查询进度
  	}

  }
  //in activity
  private DownloadChangeObserver downloadObserver;
    
  @Override
  protected void onCreate(Bundle savedInstanceState) {
  	super.onCreate(savedInstanceState);
  	setContentView(R.layout.download_manager_demo);
  	……
  	downloadObserver = new DownloadChangeObserver();
  }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	/** observer download change **/
    	getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true,
    												 downloadObserver);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	getContentResolver().unregisterContentObserver(downloadObserver);
    }
  ```
  上面这种做法可能对性能有些损耗，因为会不断触发**onChange**
  
  推荐使用**ScheduledExecutorService**
  
  ```
  public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
  Runnable command = new Runnable() {
   
  		@Override
  		public void run() {
  			updateView();
  		}
  	};
  scheduledExecutorService.scheduleAtFixedRate(command, 0, 3, TimeUnit.SECONDS);
  ```
  
