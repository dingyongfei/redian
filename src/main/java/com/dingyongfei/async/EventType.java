package com.dingyongfei.async;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/14
 */
public enum EventType {

    LIKE(0),
    COMMENT(1),
    LOGIN(2),

    //additionally
    DISLIKE(4),


    MAIL(3);


    private int value;

    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
