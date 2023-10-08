package com.five.springboot.service;

//收集用户浏览记录
public interface FeedBackService {

    void collectTopFeedBack(String newsId, int feedBack);

    void collectPersonalFeedBack(String userId,String newsId, int feedBack);

    void collectSimilarBack(String sourceNewsId, String newsId, int feedBack);

}
