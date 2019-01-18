package com.dingyongfei.async.handler;

import com.dingyongfei.async.EventHandler;
import com.dingyongfei.async.EventModel;
import com.dingyongfei.async.EventType;
import com.dingyongfei.model.Message;
import com.dingyongfei.service.MessageService;
import com.dingyongfei.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/14
 */
@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MessageService messageService;

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setToId(model.getActorId());
        message.setContent("您上次的登陆IP异常。");
        message.setConversationId(3 < model.getActorId() ? String.format("%d_%d", 3, model.getActorId()) : String.format("%d_%d", model.getActorId(), 3));
        // SYSTEM ACCOUNT
        message.setFromId(3);
        message.setCreatedDate(new Date());
        messageService.addMessage(message);


        //邮件部分
        Map<String, Object> map = new HashMap();
        map.put("username", model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email"), "登陆异常通知",
                "mails/loginException.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
