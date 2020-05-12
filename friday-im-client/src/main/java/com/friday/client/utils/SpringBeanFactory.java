package com.friday.client.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Copyright (C),Damon
 *
 * @Description: spring bean
 * @Author: Damon(npf)
 * @Date: 2020-05-11:11:38
 */
@Component
public final class SpringBeanFactory implements ApplicationContextAware {
    private static ApplicationContext context;

    public static <T> T getBean(Class<T> c) {
        return context.getBean(c);
    }

    public static <T> T getBean(Class<T> c, String name) {
        return context.getBean(name, c);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
