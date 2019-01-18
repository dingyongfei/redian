package com.dingyongfei.async;

import java.util.List;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/14
 */
public interface EventHandler {
    void doHandle(EventModel model);

    List<EventType> getSupportEventTypes();
}

