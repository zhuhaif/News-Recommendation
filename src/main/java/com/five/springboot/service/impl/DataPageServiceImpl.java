package com.five.springboot.service.impl;

import com.five.springboot.bean.Comment;
import com.five.springboot.bean.News;
import com.five.springboot.mapper.NewsMapper;
import com.five.springboot.service.DataPageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//对select的结果做分页处理
@Service
public class DataPageServiceImpl implements DataPageService {

    @Autowired
    NewsMapper newsMapper;

    @Override
    public PageInfo getPageTopRecommends(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            List<News> newsList = (List<News>) newsMapper.getTopRecommends();
            PageInfo pageInfo = new PageInfo(newsList);
            return pageInfo;
        }finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public PageInfo getPageNewsByModule(String moduleId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);//底层实现原理采用改写语句 ,将下面的方法中的sql语句获取到然后做个拼接limit
        try {
            List<News> newsList = (List<News>) newsMapper.getAllNewsByModule(moduleId);//全部的数据
            //封装分页之后的数据返回给客户端展示 ，PageInfo作为一个类做了一些封装
            PageInfo pageInfo = new PageInfo(newsList);
            //所有分页属性都可以从pageInfoDemo拿到
            return pageInfo;
        }finally {
            PageHelper.clearPage(); //清理 ThreadLocal存储的分页参数,保证线程安全
        }
    }

    @Override
    public PageInfo getPageTopRecommendsByModule(String moduleId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            List<News> newsList = (List<News>) newsMapper.getTopRecommendsByModule(moduleId);
            PageInfo pageInfo = new PageInfo(newsList);
            return pageInfo;
        }finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public PageInfo getPageSimilarRecommends(String sourceNewsId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            String recIds = newsMapper.getSimilarRecommends(sourceNewsId);
            List<News> newsList = (List<News>) newsMapper.getNewsByRecIds(sourceNewsId);
            PageInfo pageInfo = new PageInfo(newsList);
            return pageInfo;
        }finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public PageInfo getPageAuthorRecommends(String source, String newsId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            List<News> newsList = (List<News>) newsMapper.getAuthorRecommends(source, newsId);
            PageInfo pageInfo = new PageInfo(newsList);
            return pageInfo;
        }finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public PageInfo getPagePersonalRecommendsByModule(String userId, String moduleId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            List<News> newsList = (List<News>) newsMapper.getPersonalRecommendsByModule(userId, moduleId);
            PageInfo pageInfo = new PageInfo(newsList);
            return pageInfo;
        }finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public PageInfo getPageNewsByKeyWord(String keyWord, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            List<News> newsList;
            if(keyWord.equals("")){//不输入关键字返回空表
                newsList = new ArrayList<News>();
            }else{
                newsList = (List<News>) newsMapper.getNewsByKeyWord(keyWord);
            }
            PageInfo pageInfo = new PageInfo(newsList);
            return pageInfo;
        }finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public PageInfo getPageNewsByKeyWordAndModuleId(String keyWord, String moduleId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            List<News> newsList;
            if(keyWord.equals("")){//不输入关键字返回空表
                newsList = new ArrayList<News>();
            }else{
                newsList = (List<News>) newsMapper.getNewsByKeyWordAndModuleId(keyWord,moduleId);
            }
            PageInfo pageInfo = new PageInfo(newsList);
            return pageInfo;
        }finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public PageInfo getPageCommentsByNewsId(String newsId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            List<Comment> newsList = (List<Comment>) newsMapper.getCommentsByNewsId(newsId);
            PageInfo pageInfo = new PageInfo(newsList);
            return pageInfo;
        }finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public PageInfo getBrowsingHistoryByModuleId(String userId, String moduleId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            List<News> newsList = (List<News>) newsMapper.getBrowsingHistoryByModuleId(userId, moduleId);
            PageInfo pageInfo = new PageInfo(newsList);
            return pageInfo;
        }finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public PageInfo getBrowsingHistory(String userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        try {
            List<News> newsList = (List<News>) newsMapper.getBrowsingHistory(userId);
            PageInfo pageInfo = new PageInfo(newsList);
            return pageInfo;
        }finally {
            PageHelper.clearPage();
        }
    }
}
