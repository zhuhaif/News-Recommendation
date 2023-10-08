package com.five.springboot.controller;

import com.five.springboot.bean.News;
import com.five.springboot.bean.NewsModule;
import com.five.springboot.bean.User;
import com.five.springboot.mapper.NewsMapper;
import com.five.springboot.mapper.UserMapper;
import com.five.springboot.service.FeedBackService;
import com.five.springboot.service.impl.BrowsingHistoryServiceImpl;
import com.five.springboot.service.impl.DataPageServiceImpl;
import com.five.springboot.service.impl.FeedBackServiceImpl;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class NewsController {

//    @DeleteMapping
//    @PutMapping
//    @GetMapping

    @Autowired
    NewsMapper newsMapper;

    @Autowired
    DataPageServiceImpl dataPageServiceImpl;

    @Autowired
    BrowsingHistoryServiceImpl browsingHistoryServiceImpl;

    @Autowired
    FeedBackServiceImpl feedBackServiceImpl;

    //根据newsId来到新闻详情页面
    @GetMapping("/newsDetails/{newsId}")
    public String toNewsDetails(@PathVariable("newsId") String newsId,
                                @RequestParam(value = "recommendType", defaultValue = "非推荐") String recommendType,
                                @RequestParam(value = "sourceNewsId", defaultValue = "0") String sourceNewsId,
                                @RequestParam(value = "contentPageNum", defaultValue = "1") int contentPageNum,
                                @RequestParam(value = "authorPageNum", defaultValue = "1") int authorPageNum,
                                @RequestParam(value = "commentPageNum", defaultValue = "1") int commentPageNum,
                                HttpSession session, Model model){
        //获取新闻模块
        Collection<NewsModule> newsModules = newsMapper.getModules();
        model.addAttribute("newsModules",newsModules);

        News news = newsMapper.getNewsById(newsId);
        news.setUrl(news.getUrl().substring(14));
        NewsModule newsModule = newsMapper.getModuleById(news.getModuleId());
        model.addAttribute("news",news);
        model.addAttribute("newsModule",newsModule);
        //获取相似推荐结果
        PageInfo similarRecommends = dataPageServiceImpl.getPageSimilarRecommends(newsId,contentPageNum,10);
        model.addAttribute("similarRecommends",similarRecommends);
        //获取相同作者推荐结果
        PageInfo authorRecommends = dataPageServiceImpl.getPageAuthorRecommends(news.getSource(),newsId,authorPageNum,10);
        model.addAttribute("authorRecommends",authorRecommends);
        //获取用户评论
        PageInfo pageComments = dataPageServiceImpl.getPageCommentsByNewsId(news.getNewsId(),commentPageNum,10);
        model.addAttribute("pageComments",pageComments);
        boolean isFirst = true;//记录是否是第一次打开新闻详情页面
        Enumeration<String> sessionAttributes = session.getAttributeNames();
        //如果是第一次来到详情页面，session没有commentResult属性，所以为了不报错应该先判断
        while(sessionAttributes.hasMoreElements()){
            String attributeName = sessionAttributes.nextElement();
            //System.out.println(attributeName);
            if(attributeName.equals("commentResult")){
                isFirst = false;
                break;
            }
        }
        if(isFirst){//第一次来到详情页面
            model.addAttribute("commentResult","未发表评论");
        }
        else{
            String commentResult = (String) session.getAttribute("commentResult");
            if(commentResult.equals("未发表评论")){//未发表评论进入详情页面
                model.addAttribute("commentResult","未发表评论");
            }else{//发表评论后进入详情页面
                model.addAttribute("commentResult",commentResult);
            }
        }
        session.setAttribute("commentResult","未发表评论");
        //添加浏览记录
        User user = (User) session.getAttribute("user");
        browsingHistoryServiceImpl.collectBrowsingHistory(user.getUserId(),newsId);
        //更新用户反馈
        switch(recommendType){
            case "热点":
                session.setAttribute("recommendType","热点");
                feedBackServiceImpl.collectTopFeedBack(newsId, 1);
                break;
            case "个性":
                session.setAttribute("recommendType","个性");
                feedBackServiceImpl.collectPersonalFeedBack(user.getUserId(), newsId,1);
                break;
            case "内容":
                session.setAttribute("recommendType","内容");
                feedBackServiceImpl.collectSimilarBack(sourceNewsId, newsId, 1);
                break;
            default:
                session.setAttribute("recommendType","非推荐");
        }
        model.addAttribute("sourceNewsId", sourceNewsId);//供后面用户评论反馈使用
        //转到新闻详情页面;
        return "newsDetails";
    }

    //根据moduleId来到指定新闻模块页面
    @GetMapping("/newsModule/{moduleId}")
    public String toNewsModule(@PathVariable("moduleId") String moduleId,
                               @RequestParam(value = "listPageNum", defaultValue = "1") int listPageNum,
                               @RequestParam(value = "topPageNum", defaultValue = "1") int topPageNum,
                               @RequestParam(value = "personalPageNum", defaultValue = "1") int personalPageNum,
                               Model model,HttpSession session){
        //获取新闻模块
        Collection<NewsModule> newsModules = newsMapper.getModules();
        model.addAttribute("newsModules",newsModules);

        Collection<News> newsCollection = newsMapper.getAllNewsByModule(moduleId);
        NewsModule newsModule = newsMapper.getModuleById(moduleId);
        model.addAttribute("newsCollection", newsCollection);
        model.addAttribute("newsModule", newsModule);
        if(moduleId.equals("1")){
            return "redirect:/news";
        }
        else {//查询模块新闻
            //获取模块新闻分页数据
            PageInfo pageNews = dataPageServiceImpl.getPageNewsByModule(moduleId, listPageNum, 10);
            model.addAttribute("pageNews",pageNews);
            //处理前端分页栏数字变动问题(保持只显示5个页码)
            List pageNums = new ArrayList();
            if (pageNews.getPages() > 5) {
                if(listPageNum < 4){
                    for(int i = 1;i < 6;i++){
                        pageNums.add(i);
                    }
                }
                else if(listPageNum >= pageNews.getPages() - 2){
                    for(int i = pageNews.getPages() - 4;i < pageNews.getPages()+1;i++){
                        pageNums.add(i);
                    }
                }
                else{
                    for(int i = listPageNum - 2;i < listPageNum + 3;i++){
                        pageNums.add(i);
                    }
                }
            }
            else{
                for(int i = 1;i < pageNews.getPages()+1;i++){
                    pageNums.add(i);
                }
            }
            model.addAttribute("pageNums",pageNums);
            //获取模块热点新闻推荐分页数据
            PageInfo moduleTopRecommends = dataPageServiceImpl.getPageTopRecommendsByModule(moduleId,topPageNum, 5);
            model.addAttribute("moduleTopRecommends",moduleTopRecommends);
            //获取模块个性化新闻推荐分页数据
            User user = (User) session.getAttribute("user");
            PageInfo modulePersonalRecommends = dataPageServiceImpl.getPagePersonalRecommendsByModule(user.getUserId(), newsModule.getModuleId(), personalPageNum,5);
            model.addAttribute("modulePersonalRecommends",modulePersonalRecommends);
            return "newsModule";
        }
    }

    //回到首页
    @RequestMapping("/news")
    public String toNews(Model model, HttpSession session,
                         @RequestParam(value = "topPageNum", defaultValue = "1") int topPageNum){
        //获取新闻模块
        Collection<NewsModule> newsModules = newsMapper.getModules();
        model.addAttribute("newsModules",newsModules);
        //获取6条最新国际新闻
        Collection<News> interNews = newsMapper.getNewsByModule("5",6);
        model.addAttribute("interNews",interNews);
        //获取最新的政务新闻
        Collection<News> latestGovernmentNews = newsMapper.getNewsByModule("4",1);
        model.addAttribute("latestGovernmentNews",latestGovernmentNews.iterator().next());
        //获取热点新闻
        PageInfo pageTopRecommends = dataPageServiceImpl.getPageTopRecommends(topPageNum,10);
        model.addAttribute("pageTopRecommends",pageTopRecommends);
        //获取协同过滤新闻
        User user = (User) session.getAttribute("user");
        Collection<News> personalRecommends = newsMapper.getPersonalRecommends(user.getUserId());
        //分割pictureUrls字符串，将每个url存储在数组里
        for (News news : personalRecommends) {
            if(news.getPictureUrls() != null){
                news.setPictureUrlArray(news.getPictureUrls().split(","));
            }
        }
        model.addAttribute("personalRecommends", personalRecommends);
        return "news";
    }

    //根据关键字来到指定搜索结果页面
    @GetMapping("/search")
    public String toSearchResult(Model model, HttpSession session,
                                @RequestParam("keyWord") String keyWord,
                                @RequestParam(value = "moduleId", defaultValue = "1") String moduleId,
                                @RequestParam(value = "pageNum", defaultValue = "1") int pageNum) {
        //获取新闻模块
        Collection<NewsModule> newsModules = newsMapper.getModules();
        model.addAttribute("newsModules",newsModules);
        PageInfo pageNews;
        if(!moduleId.equals("1")){
            pageNews  = dataPageServiceImpl.getPageNewsByKeyWordAndModuleId(keyWord, moduleId, pageNum,10);
        }else{
            pageNews  = dataPageServiceImpl.getPageNewsByKeyWord(keyWord, pageNum,10);
        }
        model.addAttribute("pageNews",pageNews);
        model.addAttribute("moduleId",moduleId);
        //处理前端分页栏数字变动问题(保持只显示5个页码)
        List pageNums = new ArrayList();
        if (pageNews.getPages() > 5) {
            if(pageNum < 4){
                for(int i = 1;i < 6;i++){
                    pageNums.add(i);
                }
            }
            else if(pageNum >= pageNews.getPages() - 2){
                for(int i = pageNews.getPages() - 4;i < pageNews.getPages()+1;i++){
                    pageNums.add(i);
                }
            }
            else{
                for(int i = pageNum - 2;i < pageNum + 3;i++){
                    pageNums.add(i);
                }
            }
        }
        else{
            for(int i = 1;i < pageNews.getPages()+1;i++){
                pageNums.add(i);
            }
        }
        model.addAttribute("pageNums",pageNums);
        model.addAttribute("title","搜索结果");
        model.addAttribute("keyWord",keyWord);
        return "list";
    }


    @GetMapping("/browsingHistory")
    public String toBrowsingHistory(Model model, HttpSession session,
                                    @RequestParam(value = "moduleId", defaultValue = "1") String moduleId,
                                    @RequestParam(value = "pageNum", defaultValue = "1") int pageNum){
        //获取新闻模块
        Collection<NewsModule> newsModules = newsMapper.getModules();
        model.addAttribute("newsModules",newsModules);
        User user = (User) session.getAttribute("user");
        PageInfo pageNews;
        if(!moduleId.equals("1")){
            pageNews  = dataPageServiceImpl.getBrowsingHistoryByModuleId(user.getUserId(), moduleId, pageNum,10);
        }else{
            pageNews  = dataPageServiceImpl.getBrowsingHistory(user.getUserId(), pageNum,10);
        }
        model.addAttribute("pageNews",pageNews);
        model.addAttribute("moduleId",moduleId);
        //处理前端分页栏数字变动问题(保持只显示5个页码)
        List pageNums = new ArrayList();
        if (pageNews.getPages() > 5) {
            if(pageNum < 4){
                for(int i = 1;i < 6;i++){
                    pageNums.add(i);
                }
            }
            else if(pageNum >= pageNews.getPages() - 2){
                for(int i = pageNews.getPages() - 4;i < pageNews.getPages()+1;i++){
                    pageNums.add(i);
                }
            }
            else{
                for(int i = pageNum - 2;i < pageNum + 3;i++){
                    pageNums.add(i);
                }
            }
        }
        else{
            for(int i = 1;i < pageNews.getPages()+1;i++){
                pageNums.add(i);
            }
        }
        model.addAttribute("pageNums",pageNums);
        model.addAttribute("title","浏览记录");
        return "list";
    }


}
