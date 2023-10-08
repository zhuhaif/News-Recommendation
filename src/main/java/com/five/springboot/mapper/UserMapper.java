package com.five.springboot.mapper;

import com.five.springboot.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

//@Mapper或者@MapperScan将接口扫描装配到容器中
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username=#{username}")
    public User getUserByName(String username);

    @Update("update user set loginTime=#{loginTime} where userId=#{userId}")
    public int updateLoginTime(User user);

    //@Options(useGeneratedKeys = true,keyProperty = "commentId")//设置插入成功时返回commentId
    @Insert("INSERT INTO comment(newsId,username,content) VALUES (#{newsId},#{username},#{content})")
    public int insertComment(String newsId, String username, String content);

}
