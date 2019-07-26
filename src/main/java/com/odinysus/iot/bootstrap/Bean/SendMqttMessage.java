package com.odinysus.iot.bootstrap.Bean;


import java.util.Arrays;
import com.odinysus.iot.common.enums.ConfirmStatus;
/**
 * 消息
 *
 * @author lxr
 * @create 2018-01-04 19:36
 **/
public class SendMqttMessage {

    private String Topic;

    private byte[] payload;

    private int qos;

    private boolean retained;

    private boolean dup;

    private int messageId;


    private long timestamp;

    private volatile ConfirmStatus confirmStatus;

    public SendMqttMessage(String topic, byte[] payload, int qos, boolean retained, boolean dup, int messageId, long timestamp, ConfirmStatus confirmStatus) {
        Topic = topic;
        this.payload = payload;
        this.qos = qos;
        this.retained = retained;
        this.dup = dup;
        this.messageId = messageId;
        this.timestamp = timestamp;
        this.confirmStatus = confirmStatus;
    }
    public static SendMqttMessage.SendMqttMessageBuilder builder() {
        return new SendMqttMessage.SendMqttMessageBuilder();
    }

    public String getTopic() {
        return Topic;
    }

    public void setTopic(String topic) {
        Topic = topic;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetained() {
        return retained;
    }

    public void setRetained(boolean retained) {
        this.retained = retained;
    }

    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ConfirmStatus getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(ConfirmStatus confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public static class SendMqttMessageBuilder {
        private String Topic;
        private byte[] payload;
        private int qos;
        private boolean retained;
        private boolean dup;
        private int messageId;
        private long timestamp;
        private ConfirmStatus confirmStatus;

        SendMqttMessageBuilder() {
        }

        public SendMqttMessage.SendMqttMessageBuilder Topic(final String Topic) {
            this.Topic = Topic;
            return this;
        }

        public SendMqttMessage.SendMqttMessageBuilder payload(final byte[] payload) {
            this.payload = payload;
            return this;
        }

        public SendMqttMessage.SendMqttMessageBuilder qos(final int qos) {
            this.qos = qos;
            return this;
        }

        public SendMqttMessage.SendMqttMessageBuilder retained(final boolean retained) {
            this.retained = retained;
            return this;
        }

        public SendMqttMessage.SendMqttMessageBuilder dup(final boolean dup) {
            this.dup = dup;
            return this;
        }

        public SendMqttMessage.SendMqttMessageBuilder messageId(final int messageId) {
            this.messageId = messageId;
            return this;
        }

        public SendMqttMessage.SendMqttMessageBuilder timestamp(final long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public SendMqttMessage.SendMqttMessageBuilder confirmStatus(final ConfirmStatus confirmStatus) {
            this.confirmStatus = confirmStatus;
            return this;
        }

        public SendMqttMessage build() {
            return new SendMqttMessage(this.Topic, this.payload, this.qos, this.retained, this.dup, this.messageId, this.timestamp, this.confirmStatus);
        }

        public String toString() {
            return "SendMqttMessage.SendMqttMessageBuilder(Topic=" + this.Topic + ", payload=" + Arrays.toString(this.payload) + ", qos=" + this.qos + ", retained=" + this.retained + ", dup=" + this.dup + ", messageId=" + this.messageId + ", timestamp=" + this.timestamp + ", confirmStatus=" + this.confirmStatus + ")";
        }
    }
}
