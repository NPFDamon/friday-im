import com.friday.common.bean.reqVo.UserReqVo;
import com.friday.common.utils.JsonHelper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Objects;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-25:12:34
 */
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
            httpClient.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
