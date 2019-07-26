package com.odinysus.iot.bootstrap.scan;

import com.odinysus.iot.bootstrap.Bean.SendMqttMessage;
import com.odinysus.iot.bootstrap.Producer;
import com.odinysus.iot.bootstrap.handler.DefaultMqttHandler;
import com.odinysus.iot.common.pool.Scheduled;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4J2LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.odinysus.iot.common.enums.ConfirmStatus.*;

/**
 * 扫描消息确认
 *
 * @author lxr
 * @create 2018-01-08 19:22
 **/
public class SacnScheduled extends ScanRunnable {

    static private InternalLogger log = Log4J2LoggerFactory.getInstance(SacnScheduled.class);

    private Producer producer;

    private  ScheduledFuture<?> submit;

    private  int  seconds;

    public SacnScheduled(Producer producer,int seconds) {
        this.producer=producer;
        this.seconds=seconds;
    }

    public  void start(){
        Scheduled scheduled = new ScheduledPool();
        this.submit = scheduled.submit(this);
    }

    public  void close(){
        if(submit!=null && !submit.isCancelled()){
            submit.cancel(true);
        }
    }


    @Override
    public void doInfo(SendMqttMessage poll) {
        if(producer.getChannel().isActive()){
            if(checkTime(poll)){
                poll.setTimestamp(System.currentTimeMillis());
                switch (poll.getConfirmStatus()){
                    case PUB:
                        poll.setDup(true);
                        pubMessage(producer.getChannel(),poll);
                        break;
                    case PUBREC:
                        sendAck(MqttMessageType.PUBREC,true,producer.getChannel(),poll.getMessageId());
                        break;
                    case PUBREL:
                        sendAck(MqttMessageType.PUBREL,true,producer.getChannel(),poll.getMessageId());
                        break;
                }

            }
        }
        else
        {
            log.info("channel is not alived");
            submit.cancel(true);
        }
    }

    private boolean checkTime(SendMqttMessage poll) {
        return System.currentTimeMillis()-poll.getTimestamp()>=seconds*1000;
    }

    private class ScheduledPool implements Scheduled {
        private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        public ScheduledFuture<?> submit(Runnable runnable){
            return scheduledExecutorService.scheduleAtFixedRate(runnable,2,2, TimeUnit.SECONDS);
        }


    }

}
