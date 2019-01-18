package com.dingyongfei.service;

import com.dingyongfei.dao.MessageDAO;
import com.dingyongfei.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/12
 */

@Service
public class MessageService {

    @Autowired
    private MessageDAO messageDAO;

    public int addMessage(Message message) {
        return messageDAO.addMessage(message);
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);
    }



    public int getUnreadCount(int userId, String conversationId) {
        return messageDAO.getConversationUnreadCount(userId, conversationId);
    }

    public int updateUnreadCount(int userId, String conversationId) {
        return messageDAO.updateUnreadCount(userId, conversationId);
    }
}
