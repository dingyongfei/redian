package com.dingyongfei.controller;

import com.dingyongfei.model.HostHolder;
import com.dingyongfei.model.Message;
import com.dingyongfei.model.User;
import com.dingyongfei.model.ViewObject;
import com.dingyongfei.service.MessageService;
import com.dingyongfei.service.UserService;
import com.dingyongfei.util.RedianUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/12
 */

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String addMessage(Model model,
                             @RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content,
                             HttpServletResponse httpServletResponse) {
        Message message = new Message();
        try {
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setToId(toId);
            message.setFromId(fromId);
            message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
            messageService.addMessage(message);
            httpServletResponse.sendRedirect("/msg/detail/?conversationId=" + (fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId)) + "&sendId=" + String.valueOf(toId));
        } catch (Exception e) {
        }
        return RedianUtil.getJSONString(message.getId());
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @RequestParam("sendId") int sendId,
                                     @RequestParam("conversationId") String conversationId) {
        try {
            int localUserId = hostHolder.getUser().getId();
            messageService.updateUnreadCount(localUserId, conversationId);
            List<ViewObject> messages = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messages.add(vo);
            }

            User sendUser = userService.getUser(sendId);
            model.addAttribute("sendUser", sendUser);
            model.addAttribute("messages", messages);

            model.addAttribute("sendId", sendId);

        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationList(Model model) {
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("unreadCount", messageService.getUnreadCount(localUserId, msg.getConversationId()));
                vo.set("user", user);
                vo.set("targetId", targetId);
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());

        }
        return "letter";
    }
}
