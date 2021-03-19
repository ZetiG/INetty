package com.demo.inetty.tomcat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Description: 主逻辑处理
 *
 * @author Zeti
 * @date 2021/3/19 下午2:34
 */
public class ZTomcat {

    private int port = 8080;

    private Map<String, ZServlet> servletMapping = new HashMap<>();

    private Properties webxml = new Properties();


    //初始化读取配置文件
    private void init() {
        try {
            //初始化 读取配置
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "netty_tomcat.properties");
            webxml.load(fis);
            for (Object k : webxml.keySet()) {
                String key = k.toString();
                if (key.endsWith("url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");
                    ZServlet servlet = (ZServlet) Class.forName(className).newInstance();
                    servletMapping.put(url, servlet);
                }
            }

        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        doStart();
    }

    public void start(int port) {
        this.port = port;
        doStart();
    }

    //启动Netty
    private void doStart() {
        init();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();
        try {
            server.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    //客户端连接时启动
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel client) {
                            //响应编码器
                            client.pipeline().addLast(new HttpResponseEncoder());
                            //请求解码器
                            client.pipeline().addLast(new HttpRequestDecoder());
                            //自定义处理器
                            client.pipeline().addLast(new BoTomcatHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = server.bind(port).sync();
            System.out.println("ZTomcat 已启动!");
            //监听关闭状态启动
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //关闭线程池
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
    //处理请求
    public class BoTomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if(msg instanceof HttpRequest){
                HttpRequest req= (HttpRequest) msg;
                ZRequest request=new ZRequest(req, ctx);
                ZResponse response=new ZResponse(req, ctx);
                String url=request.getUri();
                if(servletMapping.containsKey(url)){
                    servletMapping.get(url).service(request,response);
                }else{
                    response.write("404");
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }
    }

    public static void main(String[] args) {
        new ZTomcat().start();
    }



}
