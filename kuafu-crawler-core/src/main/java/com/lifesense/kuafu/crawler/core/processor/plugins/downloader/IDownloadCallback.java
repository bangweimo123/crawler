package com.lifesense.kuafu.crawler.core.processor.plugins.downloader;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

public interface IDownloadCallback {
    public void onSuccess(Request request, Task task);

    public void onFailed(Request request, Task task);
}
