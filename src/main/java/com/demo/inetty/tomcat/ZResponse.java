package com.demo.inetty.tomcat;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.Data;

import java.io.UnsupportedEncodingException;

/**
 * Description: 请求响应类封装 (用一句话描述该文件做什么)
 *
 * @author Zeti
 * @date 2021/3/19 下午2:22
 */
@Data
public class ZResponse {

    private HttpRequest httpRequest;

    private ChannelHandlerContext context;

    private String code = "UTF-8";

    public ZResponse(HttpRequest httpRequest, ChannelHandlerContext context) {
        this.httpRequest = httpRequest;
        this.context = context;
    }

    public ZResponse(HttpRequest httpRequest, ChannelHandlerContext context, String code) {
        this.httpRequest = httpRequest;
        this.context = context;
        this.code = code;
    }


    public void write(String out) {
        if (out == null || out.length() == 0)
            return;

        try {

            //设置HTTP及请求头信息
            FullHttpResponse response = new DefaultFullHttpResponse(
                    //设置版本
                    HttpVersion.HTTP_1_1,
                    //设置响应状态码
                    HttpResponseStatus.OK,
                    //设置输出格式
                    Unpooled.wrappedBuffer(out.getBytes(code))
            );

            response.headers().set("Content-Type", "text/html;");
            context.write(response);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } finally {
            context.flush();
            context.close();
        }


    }

}
