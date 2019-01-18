package com.dingyongfei.service;

import com.dingyongfei.dao.NewsDAO;
import com.dingyongfei.util.JedisAdapter;
import com.dingyongfei.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/13
 */

@Service
public class LikeService {

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    NewsDAO newsDAO;

    public int getLikeOrDisLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;

    }

    public long updateStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    public double calRating(int entityType, int entityId, int likeCount, int unlikeCount) {

        double ts = 0.0, y = 0.0, f = 0.0, res = 0.0;
        if (likeCount > unlikeCount) {
            y = 1.0;
        } else if (unlikeCount > likeCount) {
            y = -1.0;
        } else {
            y = 0.0;
        }
        SimpleDateFormat epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d0 = epoch.parse("2005-12-08 07:46:43");
            Date d = newsDAO.getById(entityId).getCreatedDate();
            ts = (d.getTime() - d0.getTime()) / 1000.0;
            f = Math.log(Math.max(Math.abs(likeCount - unlikeCount), 1)) / Math.log(1.8) + ts * y / 45000;
            System.out.println(f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String ratingKey = RedisKeyUtil.getRatingKey(entityId, entityType);
        jedisAdapter.hset(ratingKey, String.valueOf(entityId), String.valueOf(f));
        res = jedisAdapter.hget(ratingKey, String.valueOf(entityId));
        return res;
    }

    public long like(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    public long disLike(int userId, int entityType, int entityId) {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }


    public long getUnlikeCount(int userId, int entityType, int entityId) {
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        return jedisAdapter.scard(disLikeKey);
    }



}
