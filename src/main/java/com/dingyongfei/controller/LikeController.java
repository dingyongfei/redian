package com.dingyongfei.controller;

import com.dingyongfei.async.EventModel;
import com.dingyongfei.async.EventProducer;
import com.dingyongfei.async.EventType;
import com.dingyongfei.model.EntityType;
import com.dingyongfei.model.HostHolder;
import com.dingyongfei.model.News;
import com.dingyongfei.service.LikeService;
import com.dingyongfei.service.NewsService;
import com.dingyongfei.util.RedianUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/13
 */

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    NewsService newsService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@Param("newId") int newsId) {
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        // 更新喜欢数
        News news = newsService.getById(newsId);
        long unlikeCount = likeService.getUnlikeCount(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        double rating = likeService.calRating(EntityType.ENTITY_NEWS, newsId, (int) likeCount, (int) unlikeCount);


        newsService.updateLikeCount(newsId, (int) likeCount);
        newsService.updateUnlikeCount(newsId, (int) unlikeCount);
        newsService.updateRating(newsId, rating);
        eventProducer.fireEvent(new EventModel(EventType.LIKE).setEntityOwnerId(news.getUserId()).setActorId(hostHolder.getUser().getId()).setEntityId(newsId));


        return RedianUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@Param("newsId") int newsId) {
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);

        News news = newsService.getById(newsId);
        long unlikeCount = likeService.getUnlikeCount(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS, newsId);
        double rating = likeService.calRating(EntityType.ENTITY_NEWS, newsId, (int) likeCount, (int) unlikeCount);

        newsService.updateLikeCount(newsId, (int) likeCount);
        newsService.updateUnlikeCount(newsId, (int) unlikeCount);
        newsService.updateRating(newsId, rating);
        eventProducer.fireEvent(new EventModel(EventType.DISLIKE).setEntityOwnerId(news.getUserId()).setActorId(hostHolder.getUser().getId()).setEntityId(newsId));

        return RedianUtil.getJSONString(0, String.valueOf(likeCount));
    }

}
