package com.odinysus.iot.bootstrap.Bean;

import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * 订阅消息
 *
 * @author lxr
 * @create 2018-01-04 19:42
 **/
public class SubMessage {
    private String topic;
    private MqttQoS qos;

    SubMessage(String topic, MqttQoS qos) {
        this.topic = topic;
        this.qos = qos;
    }

    public static SubMessage.SubMessageBuilder builder() {
        return new SubMessage.SubMessageBuilder();
    }

    public String getTopic() {
        return this.topic;
    }

    public MqttQoS getQos() {
        return this.qos;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setQos(MqttQoS qos) {
        this.qos = qos;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SubMessage)) {
            return false;
        } else {
            SubMessage other = (SubMessage)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$topic = this.getTopic();
                Object other$topic = other.getTopic();
                if (this$topic == null) {
                    if (other$topic != null) {
                        return false;
                    }
                } else if (!this$topic.equals(other$topic)) {
                    return false;
                }

                Object this$qos = this.getQos();
                Object other$qos = other.getQos();
                if (this$qos == null) {
                    if (other$qos != null) {
                        return false;
                    }
                } else if (!this$qos.equals(other$qos)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof SubMessage;
    }


    public String toString() {
        return "SubMessage(topic=" + this.getTopic() + ", qos=" + this.getQos() + ")";
    }

    public static class SubMessageBuilder {
        private String topic;
        private MqttQoS qos;

        SubMessageBuilder() {
        }

        public SubMessage.SubMessageBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public SubMessage.SubMessageBuilder qos(MqttQoS qos) {
            this.qos = qos;
            return this;
        }

        public SubMessage build() {
            return new SubMessage(this.topic, this.qos);
        }

        public String toString() {
            return "SubMessage.SubMessageBuilder(topic=" + this.topic + ", qos=" + this.qos + ")";
        }
    }
}
