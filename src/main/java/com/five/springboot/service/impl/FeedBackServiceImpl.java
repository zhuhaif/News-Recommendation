package com.five.springboot.service.impl;

import com.five.springboot.mapper.NewsMapper;
import com.five.springboot.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//收集用户反馈
@Service
public class FeedBackServiceImpl implements FeedBackService {

    @Autowired
    NewsMapper newsMapper;

    @Override
    public void collectTopFeedBack(String newsId, int newFeedBack) {
        int oldFeedBack = newsMapper.getTopFeedBackByNewsId(newsId);
        //System.out.println(newsId + " " + oldFeedBack);
        if(oldFeedBack != 2){//如果原反馈是2就不用更新了
            newsMapper.updateTopFeedBack(newsId, newFeedBack);
        }
    }

    @Override
    public void collectPersonalFeedBack(String userId, String newsId, int newFeedBack) {
        int oldFeedBack = newsMapper.getPersonalFeedBackById(userId, newsId);
        //System.out.println(newsId + " " + oldFeedBack);
        if(oldFeedBack != 2){//如果原反馈是2就不用更新了
            newsMapper.updatePersonalFeedBack(userId, newsId, newFeedBack);
        }
    }

    @Override
    public void collectSimilarBack(String sourceNewsId, String newsId, int newFeedBack) {
        int oldFeedBack = newsMapper.getSimilarFeedBackById(sourceNewsId, newsId);
        //System.out.println(newsId + " " + oldFeedBack);
        if(oldFeedBack != 2){//如果原反馈是2就不用更新了
            newsMapper.updateSimilarFeedBack(sourceNewsId, newsId, newFeedBack);
        }
    }
}
