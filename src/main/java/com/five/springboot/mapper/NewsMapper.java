package com.five.springboot.mapper;

import com.five.springboot.bean.Comment;
import com.five.springboot.bean.News;
import com.five.springboot.bean.NewsModule;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;

//@Mapper或者@MapperScan将接口扫描装配到容器中
public interface NewsMapper {

    //获取新闻模块
    @Select("SELECT * " +
            "FROM newsModule")
    public Collection<NewsModule> getModules();

    //根据moduleId获取新闻模块
    @Select("SELECT * " +
            "FROM newsModule " +
            "WHERE moduleId = #{moduleId}")
    public NewsModule getModuleById(String moduleId);

    /*@Select("SELECT news.* FROM news NATURAL JOIN news_module WHERE news_module.moduleName = #{moduleName} ORDER BY date DESC LIMIT #{number}")
    public Collection<News> getNewsByModule(String moduleName,int number);*/

    //根据指定新闻模块获取指定数量的最新新闻
    @Select("SELECT * " +
            "FROM news " +
            "WHERE moduleId = #{moduleId} " +
            "ORDER BY date DESC " +
            "LIMIT #{number}")
    public Collection<News> getNewsByModule(String moduleId,int number);

    //根据指定新闻模块获取该模块的所有新闻
    @Select("SELECT * " +
            "FROM news " +
            "WHERE moduleId = #{moduleId} " +
            "ORDER BY date DESC")
    public Collection<News> getAllNewsByModule(String moduleId);

    //根据newsId获取新闻
    @Select("SELECT * " +
            "FROM news " +
            "WHERE newsId = #{newsId}")
    public News getNewsById(String newsId);

    //获取热点推荐新闻
    @Select("SELECT news.* " +
            "FROM hotnews JOIN news " +
            "WHERE hotnews.newsId = news.newsId " +
            "ORDER BY heat DESC, recommendTime DESC")
    public Collection<News> getTopRecommends();

    //根据moduleId获取指热点推荐新闻
    @Select("SELECT news.* " +
            "FROM hotnews JOIN news " +
            "WHERE moduleId = #{moduleId} AND hotnews.newsId = news.newsId " +
            "ORDER BY heat DESC, recommendTime DESC")
    public Collection<News> getTopRecommendsByModule(String moduleId);

    //根据userId获取个性化推荐新闻
    @Select("SELECT news.* " +
            "FROM personalRecommend NATURAL JOIN news " +
            "WHERE userId = #{userId} AND personalRecommend.newsId = news.newsId " +
            "ORDER BY scores DESC, recommendTime DESC")
    public Collection<News> getPersonalRecommends(String userId);

    //根据userId和moduleId获取个性化推荐新闻
    @Select("SELECT news.* " +
            "FROM personalRecommend NATURAL JOIN news " +
            "WHERE userId = #{userId} AND moduleId = #{moduleId} AND personalRecommend.newsId = news.newsId " +
            "ORDER BY scores DESC, recommendTime DESC")
    public Collection<News> getPersonalRecommendsByModule(String userId, String moduleId);

    //根据sourceNewsId获取相似内容推荐新闻
    @Select("SELECT recIds " +
            "FROM similarNews " +
            "WHERE newsid = #{sourceNewsId}")
    public String getSimilarRecommends(String sourceNewsId);



    //根据recIds获取相似内容推荐新闻
    @Select("SELECT * " +
            "FROM news " +
            "WHERE newsId in (#{recIds})" +
            "ORDER BY date DESC" )
    public Collection<News> getNewsByRecIds(String recIds);



    //根据source和newsId获取相同作者推荐新闻
    @Select("SELECT * " +
            "FROM news " +
            "WHERE source = #{source} AND newsId != #{newsId} " +
            "ORDER BY date DESC")
    public Collection<News> getAuthorRecommends(String source, String newsId);

    //根据关键字获取新闻
    @Select("SELECT * " +
            "FROM news " +
            "WHERE title LIKE '%${keyWord}%' OR summary LIKE '%${keyWord}%' OR content LIKE '%${keyWord}%' " +
            "ORDER BY date DESC")
    public Collection<News> getNewsByKeyWord(String keyWord);

    //根据关键字和moduleId获取新闻
    @Select("SELECT * " +
            "FROM news " +
            "WHERE moduleId = #{moduleId} AND (title LIKE '%${keyWord}%' OR summary LIKE '%${keyWord}%' OR content LIKE '%${keyWord}%') " +
            "ORDER BY date DESC")
    public Collection<News> getNewsByKeyWordAndModuleId(String keyWord,String moduleId);

    //根据newsId获取用户评论
    @Select("SELECT * " +
            "FROM comment " +
            "WHERE newsId = #{newsId} " +
            "ORDER BY commentTime DESC")
    public Collection<Comment> getCommentsByNewsId(String newsId);

    //根据userId获取浏览记录
    @Select("SELECT news.* " +
            "FROM browsingHistory,news " +
            "WHERE userId = #{userId} AND browsingHistory.newsId = #{newsId} AND browsingHistory.newsId = news.newsId " +
            "ORDER BY browsingTime DESC")
    public Collection<News> getBrowsingHistoryByUserId(String userId, String newsId);

    //插入浏览记录
    @Insert("INSERT INTO browsingHistory(userId,newsId) " +
            "VALUES (#{userId},#{newsId})")
    public int insertBrowsingHistory(String userId, String newsId);

    //更新浏览时间
    @Update("UPDATE browsingHistory " +
            "SET browsingTime=#{browsingTime} " +
            "WHERE userId = #{userId} AND newsId = #{newsId}")
    public int updateBrowsingTime(String userId, String newsId, String browsingTime);

    //更新用户是否评论
    @Update("UPDATE browsingHistory " +
            "SET isComment = #{isComment} " +
            "WHERE userId = #{userId} AND newsId = #{newsId}")
    public int updateIsComment(String userId, String newsId,int isComment);

    //更新热点推荐用户反馈
    @Update("UPDATE hotnews " +
            "SET feedBack = #{feedBack} " +
            "WHERE newsId = #{newsId}")
    public int updateTopFeedBack(String newsId, int feedBack);

    //更新个性化推荐用户反馈
    @Update("UPDATE personalRecommend " +
            "SET feedBack = #{feedBack} " +
            "WHERE userId = #{userId} AND newsId = #{newsId}")
    public int updatePersonalFeedBack(String userId,String newsId, int feedBack);

    //更新相似内容推荐用户反馈
    @Update("UPDATE similarNews " +
            "SET feedBack = #{feedBack} " +
            "WHERE newsid = #{sourceNewsId} AND newsId = #{newsId}")
    public int updateSimilarFeedBack(String sourceNewsId, String newsId, int feedBack);

    //根据newsId获取热点推荐用户反馈
    @Select("SELECT feedBack " +
            "FROM hotnews " +
            "WHERE newsId = #{newsId}")
    public int getTopFeedBackByNewsId(String newsId);

    //根据userId和newsId获取个性推荐用户反馈
    @Select("SELECT feedBack " +
            "FROM personalRecommend " +
            "WHERE userId = #{userId} AND newsId = #{newsId}")
    public int getPersonalFeedBackById(String userId, String newsId);

    //根据sourceNewsId和newsId获取相似内容推荐用户反馈
    @Select("SELECT feedBack " +
            "FROM similarNews " +
            "WHERE newsid = #{sourceNewsId} AND newsId = #{newsId}")
    public int getSimilarFeedBackById(String sourceNewsId, String newsId);

    //根据userId和moduleId获取浏览记录
    @Select("SELECT news.newsId, news.title, news.moduleId, news.summary, browsingHistory.browsingTime as date, news.source, news.heat, news.readNum, news.commentsNum, news.forwardNum, news.favorNum, news.content, news.pictureUrls, news.url, news.keyword " +
            "FROM browsingHistory,news " +
            "WHERE userId = #{userId} AND news.moduleId = #{moduleId} AND browsingHistory.newsId = news.newsId " +
            "ORDER BY browsingTime DESC")
    public Collection<News> getBrowsingHistoryByModuleId(String userId, String moduleId);

    //根据userId获取浏览记录
    @Select("SELECT news.newsId, news.title, news.moduleId, news.summary, browsingHistory.browsingTime as date, news.source, news.heat, news.readNum, news.commentsNum, news.forwardNum, news.favorNum, news.content, news.pictureUrls, news.url, news.keyword " +
            "FROM browsingHistory,news " +
            "WHERE userId = #{userId} AND browsingHistory.newsId = news.newsId " +
            "ORDER BY browsingTime DESC")
    public Collection<News> getBrowsingHistory(String userId);

    //查询所有新闻的最新发布时间
    @Select("SELECT max(date) FROM news")
    public String getMaxTime();

}
