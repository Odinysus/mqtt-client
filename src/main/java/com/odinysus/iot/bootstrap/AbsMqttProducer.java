package com.odinysus.iot.bootstrap;

import com.odinysus.iot.auto.MqttListener;
import com.odinysus.iot.auto.MqttMessageListener;
import com.odinysus.iot.bootstrap.Bean.SendMqttMessage;
import com.odinysus.iot.bootstrap.Bean.SubMessage;
import com.odinysus.iot.bootstrap.cache.Cache;
import com.odinysus.iot.bootstrap.channel.MqttHandlerServiceService;
import com.odinysus.iot.bootstrap.handler.DefaultMqttHandler;
import com.odinysus.iot.bootstrap.listener.ConnectionListener;
import com.odinysus.iot.bootstrap.scan.SacnScheduled;
import com.odinysus.iot.common.enums.ConfirmStatus;
import com.odinysus.iot.common.ip.IpUtils;
import com.odinysus.iot.common.properties.ConnectOptions;
import com.odinysus.iot.common.util.MessageId;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

/**
 * 操作类
 *
 * @author lxr
 * @create 2018-01-04 17:23
 **/
public abstract class AbsMqttProducer extends MqttApi implements  Producer {

    static private InternalLogger logger = Log4J2LoggerFactory.getInstance(AbsMqttProducer.class);

    protected String name;

    protected   Channel channel;

    protected MqttListener mqttListener;

    private  NettyBootstrapClient nettyBootstrapClient ;

    protected SacnScheduled sacnScheduled;

    protected List<MqttTopicSubscription> topics = new ArrayList<>();

    protected ConnectionListener connectionListener;

    private static  final  int _BLACKLOG =   1024;

    private static final  int  CPU =Runtime.getRuntime().availableProcessors();

    private static final  int  SEDU_DAY =10;

    private static final  int TIMEOUT =120;

    private static final  int BUF_SIZE=10*1024*1024;




    private  static final CountDownLatch countDownLatch = new CountDownLatch(1);


    protected   void  connectTo(ConnectOptions connectOptions){
        checkConnectOptions(connectOptions);
        if(this.nettyBootstrapClient ==null){
            this.connectionListener = new ConnectionListener(this, connectOptions);
            this.nettyBootstrapClient = new NettyBootstrapClient(connectOptions, connectionListener);
        }
        this.channel =nettyBootstrapClient.start();
        initPool(connectOptions.getMinPeriod());
        try {
            countDownLatch.await(connectOptions.getConnectTime(), TimeUnit.SECONDS);
            subMessage(channel, topics, 0);
        } catch (InterruptedException e) {
            logger.error("InterruptedException",e);
            nettyBootstrapClient.doubleConnect(); // 重新连接
        }
    }

    @Override
    public void disConnect() {
        sendDisConnect(channel);
    }

    @Override
    public void pubRecMessage(Channel channel, int messageId) {
        SendMqttMessage sendMqttMessage= SendMqttMessage.builder().messageId(messageId)
                .confirmStatus(ConfirmStatus.PUBREC)
                .timestamp(System.currentTimeMillis())
                .build();
        Cache.put(messageId,sendMqttMessage);
        boolean flag;
        do {
            flag = sacnScheduled.addQueue(sendMqttMessage);
        } while (!flag);

        super.pubRecMessage(channel, messageId);
    }

    @Override
    protected void pubMessage(Channel channel, SendMqttMessage mqttMessage) {
        super.pubMessage(channel, mqttMessage);
        if(mqttMessage.getQos()!=0){
            Cache.put(mqttMessage.getMessageId(),mqttMessage);
            boolean flag;
            do {
                flag = sacnScheduled.addQueue(mqttMessage);
            } while (!flag);
        }
    }

    protected void initPool( int seconds){
        this.sacnScheduled =new SacnScheduled(this,seconds);
        sacnScheduled.start();
    }



    @Override
    protected void subMessage(Channel channel, List<MqttTopicSubscription> mqttTopicSubscriptions, int messageId) {
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if(channel.isActive()){
                subMessage(channel, mqttTopicSubscriptions, messageId);
            }
        }, 10, 10, TimeUnit.SECONDS);
        channel.attr(getKey(Integer.toString(messageId))).setIfAbsent(scheduledFuture);
        super.subMessage(channel, mqttTopicSubscriptions, messageId);
    }

    public void unsub(List<String> topics,int messageId) {
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if(channel.isActive()){
                unSubMessage(channel, topics, messageId);
            }
        }, 10, 10, TimeUnit.SECONDS);
        channel.attr(getKey(Integer.toString(messageId))).setIfAbsent(scheduledFuture);
        unSubMessage(channel, topics, messageId);
    }

    @Override
    public void close() {
        if(nettyBootstrapClient!=null){
            nettyBootstrapClient.shutdown();
        }
        if(sacnScheduled!=null){
            sacnScheduled.close();
        }
    }

    public  void connectBack(MqttConnAckMessage mqttConnAckMessage){
        MqttConnAckVariableHeader mqttConnAckVariableHeader = mqttConnAckMessage.variableHeader();
        switch ( mqttConnAckVariableHeader.connectReturnCode()){
            case CONNECTION_ACCEPTED:
                countDownLatch.countDown();
                break;
            case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
                logger.error("login error", new RuntimeException("用户名密码错误"));
                break;
            case CONNECTION_REFUSED_IDENTIFIER_REJECTED:
                logger.error("login error", new RuntimeException("clientId  不允许链接"));
                break;
            case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
                logger.error("login error",  new RuntimeException("服务不可用"));
                break;
            case CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION:
                logger.error("login error",  new RuntimeException("mqtt 版本不可用"));
                break;
            case CONNECTION_REFUSED_NOT_AUTHORIZED:
                logger.error("login error", new RuntimeException("未授权登录"));
                break;
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public class NettyBootstrapClient extends AbstractBootstrapClient {

        /**
         * 断开连接的次数
         */
        private int connectTime = 1;

        private NioEventLoopGroup bossGroup;

        Bootstrap bootstrap=null ;// 启动辅助类

        private ConnectOptions connectOptions;

        private ConnectionListener connectionListener;

        public NettyBootstrapClient(ConnectOptions connectOptions, ConnectionListener connectionListener) {
            this.connectOptions = connectOptions;
            this.connectionListener = connectionListener;
        }


        public void doubleConnect(){
            logger.info("断线重连中..............................................");
            ChannelFuture connect = bootstrap.connect(connectOptions.getServerIp(), connectOptions.getPort());
            connect.addListener((ChannelFutureListener) future -> {
                Thread.sleep(2000);
                if (future.isSuccess()){
                    AbsMqttProducer absMqttProducer = AbsMqttProducer.this;
                    absMqttProducer.channel =future.channel();
                    absMqttProducer.subMessage(future.channel(),topics, MessageId.messageId());
                }
                else
                    doubleConnect();
            });
        }
        @Override
        public Channel start() {
            initEventPool();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, connectOptions.isTcpNodelay())
                    .option(ChannelOption.SO_KEEPALIVE, connectOptions.isKeepalive())
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_SNDBUF, connectOptions.getSndbuf())
                    .option(ChannelOption.SO_RCVBUF, connectOptions.getRevbuf())
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            initHandler(ch.pipeline(),connectOptions,new DefaultMqttHandler(connectOptions,new MqttHandlerServiceService(), AbsMqttProducer.this, mqttListener));
                        }
                    });
            try {
                Channel channel = bootstrap.connect(connectOptions.getServerIp(), connectOptions.getPort()).addListener(connectionListener).sync().channel();
                connectTime = 1;
                return channel;
            } catch (Exception e) {
                logger.info("connect to channel fail ",e);
                try {
                    sleep(10000 * connectTime * connectTime);
                    if (connectTime == 100) {
                        return null;
                    }
                    connectTime +=1;
                    start();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }
        @Override
        public void shutdown() {
            if( bossGroup!=null ){
                try {
                    bossGroup.shutdownGracefully().sync();// 优雅关闭
                } catch (InterruptedException e) {
                    logger.info("客户端关闭资源失败【" + IpUtils.getHost() + ":" + connectOptions.getPort() + "】");
                }
            }
        }

        @Override
        public void initEventPool() {
            bootstrap= new Bootstrap();
            bossGroup = new NioEventLoopGroup(4, new ThreadFactory() {
                private AtomicInteger index = new AtomicInteger(0);
                public Thread newThread(Runnable r) {
                    return new Thread(r, "BOSS_" + index.incrementAndGet());
                }
            });
        }
    }

    public void listen(@NonNull Map<String, Object> beansWithAnnotation, ConnectOptions connectOptions) {
        Optional.of(beansWithAnnotation).ifPresent((Map<String, Object> mqttListener) -> {
            beansWithAnnotation.forEach((name, bean) -> {
                Class<?> clazz = AopUtils.getTargetClass(bean);
                if (!MqttListener.class.isAssignableFrom(bean.getClass())) {
                    throw new IllegalStateException(clazz + " is not instance of " + MqttListener.class.getName());
                }
                MqttMessageListener annotation = clazz.getAnnotation(MqttMessageListener.class);
                String[] topics = annotation.topic();
                MqttListener listener = (MqttListener) bean;
                if (this.name.equalsIgnoreCase(annotation.producer())||"".equals(annotation.producer())) {
                    this.setMqttListener(listener);
                    this.connect(connectOptions);
                    if(StringUtils.isNoneBlank(topics)) {
                        SubMessage[] SubMessages = new SubMessage[topics.length];
                        List<SubMessage> collect = Arrays.stream(topics)
                                .map(topic -> SubMessage.builder()
                                        .qos(annotation.qos())
                                        .topic(topic).build()).collect(Collectors.toList());
                        this.sub(collect.toArray(SubMessages));
                    }
                }
            });
        });
    }

    public NettyBootstrapClient getNettyBootstrapClient() {
        return nettyBootstrapClient;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setMqttListener(MqttListener mqttListener) {
        this.mqttListener = mqttListener;
    }

    private void checkConnectOptions(ConnectOptions connectOptions){
        ConnectOptions.MqttOpntions mqtt=connectOptions.getMqtt();
        Optional.ofNullable(mqtt.getPassword()).ifPresent(s -> mqtt.setHasPassword(true));
        Optional.ofNullable(mqtt.getUserName()).ifPresent(s -> mqtt.setHasUserName(true));
        Optional.ofNullable(mqtt.getWillTopic()).ifPresent(s -> mqtt.setHasWillFlag(true));
    }
}
