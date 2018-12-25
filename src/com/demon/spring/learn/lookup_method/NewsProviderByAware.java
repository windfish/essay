package com.demon.spring.learn.lookup_method;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class NewsProviderByAware implements ApplicationContextAware {

    private News news;

    public News getNews() {
        news = ac.getBean("news", News.class);
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    private ApplicationContext ac;
    @Override
    public void setApplicationContext(ApplicationContext paramApplicationContext) throws BeansException {
        ac = paramApplicationContext;
    }
    
}
