package com.demo.inetty.socket.manager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Description: (用一句话描述该文件做什么)
 *
 * @Date 2019/9/24 15:07
 * @Author Zeti
 */
public class ChannelConnect {

     private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

     private static ConcurrentMap<String, ChannelId> channelMap = new ConcurrentHashMap();

     /**
      * 新建连接存放
      *
      * @param channel
      * @return
      */
     public static boolean addChannel(Channel channel) {
          if (channel == null) {
               return false;
          }
          channelGroup.add(channel);
          ChannelId id = channel.id();
          channelMap.put(id.asShortText(), id);
          return true;
     }

     /**
      * 断开连接删除
      *
      * @param channel
      * @return
      */
     public static boolean removeChannel(Channel channel) {
          if (channel == null) {
               return false;
          }
          ChannelId id = channel.id();
          boolean rmChannel = channelGroup.remove(channel);
          ChannelId channelId = channelMap.remove(id.asShortText());
          return rmChannel && (null != channelId);
     }

     /**
      * 根据通道id查找
      *
      * @param channelId
      * @return
      */
     public static Channel findChannel(String channelId) {
          if (null != channelId) {
               return channelGroup.find(channelMap.get(channelId));
          }
          return null;
     }

     /**
      *
      * @param tws
      */
     public static void send2All(TextWebSocketFrame tws){
          channelGroup.writeAndFlush(tws);
     }

}
