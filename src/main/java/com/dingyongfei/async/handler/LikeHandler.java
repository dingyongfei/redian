package com.dingyongfei.async.handler;

import com.dingyongfei.async.EventHandler;
import com.dingyongfei.async.EventModel;
import com.dingyongfei.async.EventType;
import com.dingyongfei.model.Message;
import com.dingyongfei.model.User;
import com.dingyongfei.service.MessageService;
import com.dingyongfei.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/14
 */
@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;


    @Override
    public void doHandle(EventModel model) {


        if (model.getActorId() != model.getEntityOwnerId()) {
            Message message = new Message();
            message.setHasRead(0);   //0表示未读，1表示已读！！！
            message.setConversationId(String.valueOf(3));
            message.setFromId(3);
            message.setToId(model.getEntityOwnerId());
            User user = userService.getUser(model.getActorId());
            if (model.getType().getValue() == 0) {
                message.setContent("用户" + user.getName() + "赞了您的资讯，http://127.0.0.1:8080/news/" + model.getEntityId());
            } else if (model.getType().getValue() == 4) {
                message.setContent("用户" + user.getName() + "踩了您的资讯, http://127.0.0.1:8080/news/" + model.getEntityId());
            }
            message.setCreatedDate(new Date());

            messageService.addMessage(message);
        }

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE, EventType.DISLIKE);
    }

}
