# FRIDAY-IM
基于netty的IM实现
* Google protobuf 协议
* server端注册信息到zk client端根据负载均衡连接相关服务器
* server处理消息类型，login，ack，history等信息
* server收到消息后，直接send至kafka，kafka转发消息，server采用拉方式获取kafka消息
* redis存储登录token，会话，持久化消息内容
* eurake注册相关业务接口
* client获取token，serverinfo等信息进行登录，消息发送等
* 数据消息采用DES加密

### 基本架构
* JDk-1.8
* Netty 4.1.49.Final
* Spring-Boot 2.2.7.RELEASE
* Spring Cloud Hoxton.SR5
* Netflix-Eureka
* Zookeeper
* Kafka
* Redis
* Mysql
* Protobuf
* MyBatis

### Module
* friday-im-client
* friday-im-common
* friday-im-eurake
* friday-im-route
* friday-im-server

### 改造
可改造成实时消息推送系统，可用于手机消息推送，IOT消息推送等。


