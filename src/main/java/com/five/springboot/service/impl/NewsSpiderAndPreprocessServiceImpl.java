package com.five.springboot.service.impl;

import com.five.springboot.mapper.NewsMapper;
import com.five.springboot.scala.UserBasedCollaborativeFiltering;
import com.five.springboot.service.NewsPreprocessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

//定时任务: 爬取新闻、预处理、计算推荐结果
@Component
public class NewsSpiderAndPreprocessServiceImpl implements NewsPreprocessService {

    @Autowired
    NewsMapper newsMapper;

    @Value("${recommendNum.collaborativeFilteringRecommend}")
    private int cfRecommendNum;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    @Scheduled(fixedDelay = 1)//上一次执行完毕时间点之后 6 秒再执行
    public void spiderAndPreprocessNews() {

        String maxTime =newsMapper.getMaxTime();

        /*//1.爬取数据
        System.out.println("1.爬取数据开始："+format.format(new Date()));
        System.out.println("1.爬取数据进行中...");
        NewsSpiderCMD newsSpiderCMD = new NewsSpiderCMD();
        newsSpiderCMD.execCmd(maxTime.replace(" ","-").replace(":","-"));
        System.out.println("1.爬取数据结束："+format.format(new Date()));

        //2.模块划分
        System.out.println("2.模块划分开始："+format.format(new Date()));
        System.out.println("2.模块划分进行中...");
        ModuleDivision moduleDivision = new ModuleDivision();
        moduleDivision.moduleDivision(maxTime);
        System.out.println("2.模块划分结束："+format.format(new Date()));

        //3.相似推荐算法预处理
        System.out.println("3.相似推荐算法预处理开始："+format.format(new Date()));
        System.out.println("3.相似推荐算法预处理进行中...");
        //方法调用
        System.out.println("3.相似推荐算法预处理结束："+format.format(new Date()));*/

        /*//4.热点推荐
        System.out.println("4.热点推荐开始："+format.format(new Date()));
        System.out.println("4.热点推荐进行中...");
        //方法调用
        System.out.println("4.热点推荐结束："+format.format(new Date()));*/

        /*//5.个性化推荐
        System.out.println("5.个性化推荐开始："+format.format(new Date()));
        System.out.println("5.个性化推荐进行中...");
        UserBasedCollaborativeFiltering userBasedCollaborativeFiltering = new UserBasedCollaborativeFiltering();
        userBasedCollaborativeFiltering.collaborativeFilteringRecommend(cfRecommendNum);
        System.out.println("5.个性化推荐结束："+format.format(new Date()));*/

        /*//6.相似新闻推荐
        System.out.println("6.相似新闻推荐开始："+format.format(new Date()));
        System.out.println("6.相似新闻推荐进行中...");
        //方法调用
        System.out.println("6.相似新闻推荐结束："+format.format(new Date()));*/


    }


}
