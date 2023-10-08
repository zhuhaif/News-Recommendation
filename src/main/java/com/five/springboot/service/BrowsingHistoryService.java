package com.five.springboot.service;

import com.github.pagehelper.PageInfo;

//收集用户浏览记录
public interface BrowsingHistoryService {

    void collectBrowsingHistory(String userId, String newsId);

}
