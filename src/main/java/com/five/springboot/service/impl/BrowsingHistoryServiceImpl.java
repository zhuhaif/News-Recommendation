package com.five.springboot.service.impl;

import com.five.springboot.bean.News;
import com.five.springboot.mapper.NewsMapper;
import com.five.springboot.service.BrowsingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

//收集用户浏览记录
@Service
public class BrowsingHistoryServiceImpl implements BrowsingHistoryService {

    @Autowired
    NewsMapper newsMapper;

    @Override
    public void collectBrowsingHistory(String userId, String newsId) {
        Collection<News> newsCollection = newsMapper.getBrowsingHistoryByUserId(userId, newsId);
        if(newsCollection.isEmpty()){//之前未浏览过,需要insert
            newsMapper.insertBrowsingHistory(userId, newsId);
        }else{//之前有浏览过，直接update
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            newsMapper.updateBrowsingTime(userId,newsId,format.format(date));
        }
    }
}
