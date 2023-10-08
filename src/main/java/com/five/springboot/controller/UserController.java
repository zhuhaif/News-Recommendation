package com.five.springboot.controller;

import com.five.springboot.bean.User;
import com.five.springboot.mapper.NewsMapper;
import com.five.springboot.mapper.UserMapper;
import com.five.springboot.service.impl.DataPageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
public class UserController {

//    @DeleteMapping
//    @PutMapping
//    @GetMapping

    @Autowired
    UserMapper userMapper;

    @Autowired
    NewsMapper newsMapper;

    @Autowired
    DataPageServiceImpl dataPageServiceImpl;

    //@RequestMapping(value = "/user/login",method = RequestMethod.POST)
    @PostMapping(value = "/user/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Map<String,Object> map, HttpSession session){
        User user = userMapper.getUserByName(username);
        if(user == null){
            //登陆失败
            map.put("msg","用户名错误");
            return  "login";
        }
        else if(user.getPassword().equals(password)){
            //更新登录时间
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            user.setLoginTime(format.format(date));
            userMapper.updateLoginTime(user);
            session.setAttribute("user",user);
            //登陆成功，防止表单重复提交，可以重定向到主页
            return "redirect:/news";
        }else{
            //登陆失败
            map.put("msg","密码错误");
            return  "login";
        }

    }

    @RequestMapping("/user/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "login";
    }

    @PostMapping(value = "/user/comment/{newsId}")
    public String comment(@PathVariable("newsId") String newsId,
                          @RequestParam("sourceNewsId") String sourceNewsId,
                          @RequestParam("comment") String comment,
                          @RequestParam("contentPageNum") int contentPageNum,
                          @RequestParam("authorPageNum") int authorPageNum,
                          @RequestParam("commentPageNum") int commentPageNum,
                          Map<String,Object> map, HttpSession session){
        User user = (User) session.getAttribute("user");
        int commentResult = userMapper.insertComment(newsId,user.getUsername(),comment);
        //System.out.println(commentResult);
        if(commentResult == 1){//评论成功
            commentPageNum = 1;//返回第一页让用户看到自己刚刚的评论
            newsMapper.updateIsComment(user.getUserId(), newsId, 1);
            //更新用户反馈
            String recommendType = (String) session.getAttribute("recommendType");
            switch(recommendType){
                case "热点":
                    newsMapper.updateTopFeedBack(newsId,2);
                    break;
                case "个性":
                    newsMapper.updatePersonalFeedBack(user.getUserId(),newsId,2);
                    break;
                case "内容":
                    //newsMapper.updateSimilarFeedBack(sourceNewsId,newsId,2);
                    break;
                default:
            }
            session.setAttribute("commentResult","发表成功");
        }else {//评论失败
            session.setAttribute("commentResult","发表失败");
        }
        return "redirect:/newsDetails/" + newsId + "?contentPageNum=" + contentPageNum +
                "&authorPageNum=" + authorPageNum + "&commentPageNum=" + commentPageNum;
    }

}
