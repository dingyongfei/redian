package com.dingyongfei.dao;

import com.dingyongfei.model.User;
import org.apache.ibatis.annotations.*;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/13
 */
@Mapper
public interface UserDAO {
    String TABLE_NAME = "user";
    String INSET_FIELDS = " name, password, salt, head_url, times ";
    String SELECT_FIELDS = " id, name, password, salt, head_url, times";

    @Insert({"insert into ", TABLE_NAME, "(", INSET_FIELDS,
            ") values (#{name},#{password},#{salt},#{headUrl}, #{times})"})
    int addUser(User user);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    User selectById(int id);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where name=#{name}"})
    User selectByName(String name);

    @Update({"update ", TABLE_NAME, " set password=#{password} where id=#{id}"})
    void updatePassword(User user);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    void deleteById(int id);

    @Update({"update ", TABLE_NAME, " set times=#{times} where id=#{id}"})
    void updateTimes(@Param("id") int id, @Param("times") int times);

    @Update({"update ", TABLE_NAME, "set name=#{name}, password=#{password}, head_url=#{headUrl} where id=#{id}"})
    void updateUserInfo(User user);
}
