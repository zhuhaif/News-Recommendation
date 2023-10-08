package com.five.springboot.service;

import com.github.pagehelper.PageInfo;

//对select的结果做分页处理
public interface DataPageService {

    PageInfo getPageTopRecommends(int pageNum, int pageSize);

    PageInfo getPageNewsByModule(String moduleId, int pageNum, int pageSize);

    PageInfo getPageTopRecommendsByModule(String moduleId, int pageNum, int pageSize);

    PageInfo getPageSimilarRecommends(String sourceNewsId, int pageNum, int pageSize);

    PageInfo getPageAuthorRecommends(String source, String newsId, int pageNum, int pageSize);

    PageInfo getPagePersonalRecommendsByModule(String userId, String moduleId, int pageNum, int pageSize);

    PageInfo getPageNewsByKeyWord(String keyWord, int pageNum, int pageSize);

    PageInfo getPageNewsByKeyWordAndModuleId(String keyWord, String moduleId, int pageNum, int pageSize);

    PageInfo getPageCommentsByNewsId(String newsId, int pageNum, int pageSize);

    PageInfo getBrowsingHistoryByModuleId(String userId, String moduleId, int pageNum, int pageSize);

    PageInfo getBrowsingHistory(String userId, int pageNum, int pageSize);
}
