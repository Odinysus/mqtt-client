package com.odinysus.iot.bootstrap.scan;

import com.odinysus.iot.bootstrap.Bean.SendMqttMessage;
import com.odinysus.iot.bootstrap.MqttApi;
import com.odinysus.iot.common.enums.ConfirmStatus;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 扫描未确认信息
 *
 * @author lxr
 * @create 2018-01-06 16:50
 **/


public abstract class ScanRunnable  extends MqttApi implements Runnable {

    private ConcurrentLinkedQueue<SendMqttMessage> queue  = new ConcurrentLinkedQueue<>();


    public  boolean addQueue(SendMqttMessage t){
        return queue.add(t);
    }

    public  boolean addQueues(List<SendMqttMessage> ts){
        return queue.addAll(ts);
    }



    @Override
    public void run() {
        if(!queue.isEmpty()) {
            SendMqttMessage poll;
            List<SendMqttMessage> list = new LinkedList<>();
            for (; (poll = queue.poll()) != null; ) {
                if (poll.getConfirmStatus() != ConfirmStatus.COMPLETE) {
                    list.add(poll);
                    doInfo(poll);
                }
                break;
            }
            addQueues(list);
        }
    }
    public  abstract  void  doInfo( SendMqttMessage poll);


}
