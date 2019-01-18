package com.dingyongfei.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/13
 */
public class ViewObject {
    private Map<String, Object> objs = new HashMap<String, Object>();

    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}
