package com.friday.client.conf;

import com.friday.common.netty.ServerChannelManager;
import com.friday.common.netty.impl.ServerChannelManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (C),Damon
 *
 * @Description: bean
 * @Author: Damon(npf)
 * @Date: 2020-05-15:11:24
 */
@Configuration
public class BeanConfig {

    @Bean
    public ServerChannelManager getServerChannelManager(){
        return new ServerChannelManagerImpl();
    }
}
