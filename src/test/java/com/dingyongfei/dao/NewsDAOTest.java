package com.dingyongfei.dao;

import com.dingyongfei.RedianApplication;
import com.dingyongfei.model.News;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RedianApplication.class)
public class NewsDAOTest {

    @Autowired
    private NewsDAO newsDAO;

    @Test
    public void testSelectByIdAndOffset() {
        List<News> news = newsDAO.selectByUserIdAndOffset(2, 0, 1);
        Assert.assertEquals(1, news.size());
        news = newsDAO.selectByUserIdAndOffset(0, 2, 8);
        Assert.assertEquals(8, news.size());

    }
}