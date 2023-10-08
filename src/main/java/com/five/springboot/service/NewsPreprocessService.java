package com.five.springboot.service;

//定时任务: 爬取新闻并进行预处理
public interface NewsPreprocessService {

    void spiderAndPreprocessNews();

}
