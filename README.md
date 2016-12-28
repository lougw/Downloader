# Downloader
Android Downloader
1:init downloader 
  Application--onCreate()--> Downloader.getInstance().Init(this);
2: download
 DownLoadItem downloaditem = new DownLoadItem();
 downloaditem.setDownLoadUrl(video.getUrl());
 downloaditem.setGuid(MD5Util.mD5(video.getUrl()));
 downloaditem.setFileName(videoName);
 Downloader.getInstance().getDownloadManager().downLoad(downloaditem); 
 3:pause
 Downloader.getInstance().getDownloadManager().pause(BaseModel model) 
 4:resume
 Downloader.getInstance().getDownloadManager().resume(DownloadRequest request)
 5:delete
 Downloader.getInstance().getDownloadManager().delete(DownloadRequest request)
