package com.demo.inetty.handler;

import com.demo.inetty.manager.INettyInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: INetty服务
 *
 * @Date 2019/9/24 16:29
 * @Author Zeti
 */
@Slf4j
public class INettyService {

    /**
     * Netty服务器启动
     */
    private static void init() {
        log.info("正在启动websocket服务器");

        // 主从多线程模型，基于Netty的线程Reactor模型,
        // bossGroup负责接收用户的连接请求，并将accept的连接请求注册到workGroup上
        // workGroup线程负责处理已连接的数据的读写,
        // 还有一个单独的线程池ThreadPool,处理具体的业务逻辑，可复用workGroup
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            //Netty的启动引导类,可根据需要配置，
            //客户端是Bootstrap类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //绑定线程池
            bootstrap.group(bossGroup, workGroup);
            //指定传输通道类型
            bootstrap.channel(NioServerSocketChannel.class);
            //初始化通道
            bootstrap.childHandler(new INettyInitializer());
            //异步绑定端口
            Channel channel = bootstrap.bind(8081).sync().channel();
            log.info("webSocket服务器启动成功：" + channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info("运行出错：" + e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            log.info("websocket服务器已关闭");
        }
    }

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) {
        init();
    }
}
