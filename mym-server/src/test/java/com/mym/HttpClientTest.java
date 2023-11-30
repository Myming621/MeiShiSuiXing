package com.mym;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @author mingbb
 * @create 2023-09-24-15:14
 * <p>
 * HttpClient测试
 * 1、创建HttpClient对象
 * 2、创建Http请求对象
 * 3、调用HttpClient对象的execute方法发送请求
 */
@SpringBootTest
public class HttpClientTest {

    /**
     * 测试通过HttpClient发送GET请求
     */
    @Test
    public void testGet() throws IOException {
        //1.
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //2.
        HttpGet httpGet = new HttpGet("http://localhost:8080/user/shop/status");
        //3.并接受响应结果
        CloseableHttpResponse response = httpClient.execute(httpGet);
        //获取响应结果状态码并输出
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode);
        //获取响应结果数据并输出
        HttpEntity entity = response.getEntity();
        String s = EntityUtils.toString(entity);
        System.out.println(s);

        //关闭资源
        response.close();
        httpClient.close();
    }


    /**
     * 测试通过HttpClient发送POST请求
     */
    @Test
    public void testPost() throws IOException {
        //1.
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //2.
        HttpPost httpPost = new HttpPost("http://localhost:8080/admin/employee/login");
        //构造参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username","mym621");
        jsonObject.put("password","123456");
        StringEntity entity = new StringEntity(jsonObject.toString());
        //设置参数的编码及数据格式
        entity.setContentEncoding("utf-8");
        entity.setContentType("application/json");
        //设置参数
        httpPost.setEntity(entity);
        //3.并接收响应结果
        CloseableHttpResponse response = httpClient.execute(httpPost);

        //获取响应结果状态码并输出
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode);
        //获取响应结果数据并输出
        HttpEntity entity0 = response.getEntity();
        String s = EntityUtils.toString(entity0);
        System.out.println(s);

        //关闭资源
        response.close();
        httpClient.close();
    }
}
