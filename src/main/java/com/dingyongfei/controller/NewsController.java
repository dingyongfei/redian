package com.dingyongfei.controller;

import com.dingyongfei.model.*;
import com.dingyongfei.service.*;
import com.dingyongfei.util.RedianUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/13
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = {"/image"}, method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName,
                         HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new
                    File(RedianUtil.IMAGE_DIR + imageName)), response.getOutputStream());
        } catch (Exception e) {
            logger.error("读取图片错误" + imageName + e.getMessage());
        }
    }

    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            //String fileUrl = newsService.saveImage(file);
            String fileUrl = qiniuService.saveImage(file);
            if (fileUrl == null) {
                return RedianUtil.getJSONString(1, "上传图片失败");
            }
            return RedianUtil.getJSONString(0, fileUrl);
        } catch (Exception e) {
            logger.error("上传图片失败" + e.getMessage());
            return RedianUtil.getJSONString(1, "上传失败");
        }
    }

    @RequestMapping(path = {"/user/change"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String change(@RequestParam("nick") String nick,
                         @RequestParam("pwd") String pwd,
                         @RequestParam("headImageUrl") String headImageUrl) {
        try {
            User user = hostHolder.getUser();
            user.setName(nick);
            user.setPassword(pwd);
            user.setHeadUrl(headImageUrl);
            userService.updateUserInfo(user);

        } catch (Exception e) {
            logger.error("更新用户信息失败" + e.getMessage());
        }
        return "change";
    }

    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {

            Map<String, Object> map = new HashMap<>();
            News news = new News();
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setImage(image);
            news.setRating(0.0);
            //news.setUnlikeCount(0);
            news.setLink(link);
            if (hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                // !!!!!!!!!!!!!设置一个匿名用户
                //这里设置一个系统用户，默认id为3，在点赞功能中，用到了这个系统用户，系统会给相应的用户发送站内信通知被赞详情!!!!!!!!!!!!!!!!!!!
                news.setUserId(3);
            }

            if (!link.contains(".com") || !link.contains("www.")) {
                map.put("msglink", "链接地址不合法");
                return RedianUtil.getJSONString(1, map);
            }
            if (title.contains("fuck") || title == null || title.trim().equals("")) {
                map.put("msgtitle", "必须填写标题");
                return RedianUtil.getJSONString(1, map);
            }

            newsService.addNews(news);
            return RedianUtil.getJSONString(0, "添加资讯成功");
        } catch (Exception e) {
            logger.error("添加资讯失败" + e.getMessage());
            return RedianUtil.getJSONString(1, "发布失败");
        }
    }


    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model) {
        try {
            News news = newsService.getById(newsId);
            if (news != null) {
                int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
                if (localUserId != 0) {
                    model.addAttribute("like", likeService.getLikeOrDisLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
                }

                List<Comment> comments = commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
                List<ViewObject> commentVOs = new ArrayList<>();
                for (Comment comment : comments) {
                    ViewObject commentVO = new ViewObject();
                    commentVO.set("comment", comment);
                    commentVO.set("user", userService.getUser(comment.getUserId()));
                    commentVOs.add(commentVO);
                }
                model.addAttribute("comments", commentVOs);
            }
            String converId = hostHolder.getUser().getId() < news.getUserId() ? String.format("%d_%d", hostHolder.getUser().getId(), news.getUserId()) : String.format("%d_%d", news.getUserId(), hostHolder.getUser().getId());
            model.addAttribute("converId", converId);
            model.addAttribute("news", news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        } catch (Exception e) {
            logger.error("获取资讯明细错误" + e.getMessage());
        }
        return "detail";
    }


    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setEntityId(newsId);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);

            // 更新评论数量，以后用异步实现
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);

        } catch (Exception e) {
            logger.error("提交评论错误" + e.getMessage());
        }
        return "redirect:/news/" + String.valueOf(newsId);
    }
}
