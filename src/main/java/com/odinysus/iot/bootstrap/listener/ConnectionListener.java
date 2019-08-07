package com.odinysus.iot.bootstrap.listener;

import com.odinysus.iot.bootstrap.AbsMqttProducer;
import com.odinysus.iot.bootstrap.MqttProducer;
import com.odinysus.iot.common.properties.ConnectOptions;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;

import java.util.concurrent.TimeUnit;

public class ConnectionListener implements ChannelFutureListener {
    AbsMqttProducer mqttProducer;
    ConnectOptions connectOptions;
    public ConnectionListener(AbsMqttProducer mqttProducer, ConnectOptions connectOptions) {
        this.mqttProducer = mqttProducer;
        this.connectOptions = connectOptions;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            System.out.println("Reconnect");
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(new Runnable() {
                @Override
                public void run() {
                    mqttProducer.connect(connectOptions);
                }
            }, 1L, TimeUnit.SECONDS);
        }
    }
}
