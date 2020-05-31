package utils;

import com.friday.common.bean.reqVo.UserReqVo;
import com.friday.common.utils.JsonHelper;
import group.bean.GroupOutParam;
import group.bean.GroupReqParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-25:12:34
 */
@Slf4j
public class HttpTest {
    public static void login(String uid, String secret) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(300 * 1000)
                .setConnectTimeout(300 * 1000)
                .build();
        HttpPost post = new HttpPost("http://localhost:8010/test/login");
        UserReqVo vo = new UserReqVo();
        vo.setUid(uid);
        vo.setSecret(secret);
        vo.setTimeStamp(System.currentTimeMillis());
        vo.setUserName("test");

        post.setConfig(requestConfig);
        post.setHeader("Content-Type", "application/json;charset=utf-8");
        StringEntity postingString = new StringEntity(Objects.requireNonNull(JsonHelper.toJsonString(vo)),
                "utf-8");
        post.setEntity(postingString);
        try {
            HttpResponse entity = httpClient.execute(post);
            log.info("login down msg:{}",entity.getEntity().getContent().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GroupOutParam group(List<String> strings) {
        HttpClient client = HttpClients.createSystem();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(300 * 1000)
                .setConnectTimeout(300 * 1000)
                .build();
        HttpPost post = new HttpPost("http://localhost:8010/test/createGroup");
        GroupReqParam params = new GroupReqParam("test-group","test-group",strings);
        post.setConfig(requestConfig);
        post.setHeader("Content-Type", "application/json;charset=utf-8");
        StringEntity postingString = new StringEntity(Objects.requireNonNull(JsonHelper.toJsonString(params)),
                "utf-8");
        post.setEntity(postingString);
        try {
            HttpResponse entity = client.execute(post);
            String s = EntityUtils.toString(entity.getEntity());
            log.info("login down msg:{}",s);
            GroupOutParam outParam = JsonHelper.readValue(s,GroupOutParam.class);
            return outParam;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
