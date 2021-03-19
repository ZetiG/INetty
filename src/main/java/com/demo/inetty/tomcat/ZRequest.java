package com.demo.inetty.tomcat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Description: 请求接收类封装
 *
 * @author Zeti
 * @date 2021/3/19 下午2:10
 */
@Data
public class ZRequest {

    private HttpRequest httpRequest;

    private ChannelHandlerContext context;


    public ZRequest(HttpRequest httpRequest, ChannelHandlerContext context) {
        this.httpRequest = httpRequest;
        this.context = context;
    }

    public String getUri() {
        return httpRequest.uri();
    }

    public String getMethod() {
        return httpRequest.method().name();
    }

    public String getParameter(String key) {
        List<String> param = getParameters().get(key);
        if (param == null)
            return null;
        else
            return param.get(0);

    }

    public Map<String, List<String>> getParameters() {
        return new QueryStringDecoder(httpRequest.uri()).parameters();
    }

}
