package com.dingyongfei.dao;

import com.dingyongfei.model.News;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/13
 */
@Mapper
public interface NewsDAO {
    String TABLE_NAME = "news";
    String INSERT_FIELDS = " title, link, image, like_count, unlike_count, comment_count, created_date, user_id, rating";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{link},#{image},#{likeCount},#{unlikeCount},#{commentCount},#{createdDate},#{userId}, #{rating})"})
    int addNews(News news);

    List<News> selectByUserIdAndOffset(@Param("userId") int userId, @Param("offset") int offset,
                                       @Param("limit") int limit);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    News getById(int id);


    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id = #{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    @Update({"update ", TABLE_NAME, " set like_count =#{likeCount} where id=#{id}"})
    int updateLikeCount(@Param("id") int id, @Param("likeCount") int likeCount);

    @Update({"update ", TABLE_NAME, " set unlike_count=#{unlikeCount} where id=#{id}"})
    int updateUnlikeCount(@Param("id") int id, @Param("unlikeCount") int unlikeCount);


    @Update({"update ", TABLE_NAME, " set rating=#{rating} where id=#{id}"})
    int updateRating(@Param("id") int id, @Param("rating") double rating);
}
