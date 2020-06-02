package com.friday.client.rust.service.impl;

import com.friday.client.rust.service.RustService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-22:14:42
 */
@Component
@Slf4j
public class RustServiceImpl implements RustService {

    @Value("${}")
    private String serverUrl;

    @Autowired
    private RestTemplate client;


    @Override
    public Boolean sendMsf(String msg) {
        HttpEntity<String> entity = new HttpEntity<>(msg);
        ResponseEntity<String> responseEntity = client.exchange(serverUrl + "/test", HttpMethod.POST,entity,String.class);
        return Boolean.valueOf(responseEntity.getBody());
    }
}
