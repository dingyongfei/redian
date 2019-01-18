package com.dingyongfei.dao;

import com.dingyongfei.model.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/13
 */
@Mapper
public interface LoginTicketDAO {
    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = " user_id, expired, status, ticket ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{expired},#{status},#{ticket})"})
    int addTicket(LoginTicket ticket);
    //！！！！！！！！！！！！！！！！！！Dao层增加某条数据时，一般返回一个int，原因有二：添加评论失败后会返回一个0；添加成功后ticket里的id会被重新赋值。

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update ", TABLE_NAME, " set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}
