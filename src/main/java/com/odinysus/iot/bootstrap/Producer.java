package com.odinysus.iot.bootstrap;

import com.odinysus.iot.auto.MqttListener;
import com.odinysus.iot.bootstrap.Bean.SubMessage;
import com.odinysus.iot.common.properties.ConnectOptions;
import io.netty.channel.Channel;

import java.util.List;

/**
 * 生产者
 *
 * @author lxr
 * @create 2018-01-04 17:17
 **/
public interface Producer {

    Channel getChannel();

    Producer connect(ConnectOptions connectOptions);

    void  close();

    void setMqttListener(MqttListener mqttListener);

    void pub(String topic, String message, boolean retained, int qos);

    void pub(String topic, String message);

    void pub(String topic, String message, int qos);

    void pub(String topic, String message, boolean retained);

    void sub(SubMessage... subMessages);

    void unsub(List<String> topics);

    void unsub();

    void disConnect();

}
