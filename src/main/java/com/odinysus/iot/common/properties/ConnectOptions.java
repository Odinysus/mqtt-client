package com.odinysus.iot.common.properties;


/**
 * 链接参数配置
 *
 * @author lxr
 * @create 2018-01-04 16:13
 **/
public class ConnectOptions {

    private long connectTime;

    private String serverIp;

    private int port ;

    private boolean keepalive ;

    private boolean reuseaddr ;

    private boolean tcpNodelay ;

    private int backlog ;

    private  int  sndbuf ;

    private int revbuf ;

    private int heart;

    private boolean ssl ;

    private String jksFile;

    private String jksStorePassword;

    private String jksCertificatePassword;

    private  int minPeriod ;

    private int bossThread;

    private int workThread;

    private MqttOpntions mqtt;

    public long getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(long connectTime) {
        this.connectTime = connectTime;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isKeepalive() {
        return keepalive;
    }

    public void setKeepalive(boolean keepalive) {
        this.keepalive = keepalive;
    }

    public boolean isReuseaddr() {
        return reuseaddr;
    }

    public void setReuseaddr(boolean reuseaddr) {
        this.reuseaddr = reuseaddr;
    }

    public boolean isTcpNodelay() {
        return tcpNodelay;
    }

    public void setTcpNodelay(boolean tcpNodelay) {
        this.tcpNodelay = tcpNodelay;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getSndbuf() {
        return sndbuf;
    }

    public void setSndbuf(int sndbuf) {
        this.sndbuf = sndbuf;
    }

    public int getRevbuf() {
        return revbuf;
    }

    public void setRevbuf(int revbuf) {
        this.revbuf = revbuf;
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public String getJksFile() {
        return jksFile;
    }

    public void setJksFile(String jksFile) {
        this.jksFile = jksFile;
    }

    public String getJksStorePassword() {
        return jksStorePassword;
    }

    public void setJksStorePassword(String jksStorePassword) {
        this.jksStorePassword = jksStorePassword;
    }

    public String getJksCertificatePassword() {
        return jksCertificatePassword;
    }

    public void setJksCertificatePassword(String jksCertificatePassword) {
        this.jksCertificatePassword = jksCertificatePassword;
    }

    public int getMinPeriod() {
        return minPeriod;
    }

    public void setMinPeriod(int minPeriod) {
        this.minPeriod = minPeriod;
    }

    public int getBossThread() {
        return bossThread;
    }

    public void setBossThread(int bossThread) {
        this.bossThread = bossThread;
    }

    public int getWorkThread() {
        return workThread;
    }

    public void setWorkThread(int workThread) {
        this.workThread = workThread;
    }

    public MqttOpntions getMqtt() {
        return mqtt;
    }

    public void setMqtt(MqttOpntions mqtt) {
        this.mqtt = mqtt;
    }

    public static class MqttOpntions{

        private  String clientIdentifier;

        private  String willTopic;

        private  String willMessage;

        private  String userName;

        private  String password;

        private  boolean hasUserName;

        private  boolean hasPassword;

        private  boolean hasWillRetain;

        private  int willQos;

        private  boolean hasWillFlag;

        private  boolean hasCleanSession;

        private int KeepAliveTime;

        public String getClientIdentifier() {
            return clientIdentifier;
        }

        public void setClientIdentifier(String clientIdentifier) {
            this.clientIdentifier = clientIdentifier;
        }

        public String getWillTopic() {
            return willTopic;
        }

        public void setWillTopic(String willTopic) {
            this.willTopic = willTopic;
        }

        public String getWillMessage() {
            return willMessage;
        }

        public void setWillMessage(String willMessage) {
            this.willMessage = willMessage;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isHasUserName() {
            return hasUserName;
        }

        public void setHasUserName(boolean hasUserName) {
            this.hasUserName = hasUserName;
        }

        public boolean isHasPassword() {
            return hasPassword;
        }

        public void setHasPassword(boolean hasPassword) {
            this.hasPassword = hasPassword;
        }

        public boolean isHasWillRetain() {
            return hasWillRetain;
        }

        public void setHasWillRetain(boolean hasWillRetain) {
            this.hasWillRetain = hasWillRetain;
        }

        public int getWillQos() {
            return willQos;
        }

        public void setWillQos(int willQos) {
            this.willQos = willQos;
        }

        public boolean isHasWillFlag() {
            return hasWillFlag;
        }

        public void setHasWillFlag(boolean hasWillFlag) {
            this.hasWillFlag = hasWillFlag;
        }

        public boolean isHasCleanSession() {
            return hasCleanSession;
        }

        public void setHasCleanSession(boolean hasCleanSession) {
            this.hasCleanSession = hasCleanSession;
        }

        public int getKeepAliveTime() {
            return KeepAliveTime;
        }

        public void setKeepAliveTime(int keepAliveTime) {
            KeepAliveTime = keepAliveTime;
        }
    }
}
