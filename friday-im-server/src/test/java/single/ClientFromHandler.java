package single;

import com.friday.common.bean.token.Token;
import com.friday.common.protobuf.Message.*;
import com.friday.common.utils.JsonHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Copyright (C),Damon
 *
 * @Description:
 * @Author: Damon(npf)
 * @Date: 2020-05-25:11:25
 */
@Slf4j
public class ClientFromHandler extends SimpleChannelInboundHandler<FridayMessage> {
    private ChannelHandlerContext messageConnectionCtx;

    private String uid = "test2";

    private String[] toUidList = {"test1"};

    private String secret = "test123456";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        messageConnectionCtx = ctx;
        sendLogin(ctx, uid);
    }

    private void sendLogin(ChannelHandlerContext ctx, String uid) {
        String token = new Token(uid, secret).getToken(secret);
        log.info("token{}" + token);
        Login login = Login.newBuilder()
                .setUid(uid)
                .setToken(token)
                .setId(ClientFrom.snowFlake.nextId())
                .build();
        FridayMessage ravenMessage = FridayMessage.newBuilder().setType(FridayMessage.Type.Login).setLogin(login)
                .build();
        ctx.writeAndFlush(ravenMessage);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FridayMessage message)
            throws Exception {
        if (message.getType() == FridayMessage.Type.LoginAck) {
            LoginAck loginAck = message.getLoginAck();
            log.info("login ack:{}", JsonHelper.toJsonString(loginAck));
            if (loginAck.getCode() == Code.SUCCESS) {
                for (String toUid : toUidList) {
                    Thread.sleep(1000);
                    MessageContent content = MessageContent.newBuilder().setUid(uid)
                            .setType(MessageType.TEXT)
                            .setContent("hello world").build();
                    UpDownMessage upDownMessage = UpDownMessage.newBuilder()
                            .setCid(ClientFrom.snowFlake.nextId())
                            .setFromUid(uid)
                            .setToUid(toUid)
                            .setConverType(ConverType.SINGLE)
                            .setContent(content).build();
                    FridayMessage ravenMessage = FridayMessage.newBuilder()
                            .setType(FridayMessage.Type.UpDownMessage)
                            .setUpDownMessage(upDownMessage).build();
                    ctx.writeAndFlush(ravenMessage);
                }
                Thread.sleep(2000);
                ConverReq converReq = ConverReq.newBuilder().setId(ClientFrom.snowFlake.nextId())
                        .setType(OperationType.ALL)
                        .build();
                FridayMessage ravenMessage = FridayMessage.newBuilder().setType(FridayMessage.Type.ConverReq)
                        .setConverReq(converReq).build();
                ctx.writeAndFlush(ravenMessage);
            }
        }
        if (message.getType() == FridayMessage.Type.MessageAck) {
            MessageAck messageAck = message.getMessageAck();
            log.info("receive message ack:{}", JsonHelper.toJsonString(messageAck));
        }
        if (message.getType() == FridayMessage.Type.UpDownMessage) {
            UpDownMessage upDownMessage = message.getUpDownMessage();
            log.info("receive down message:{}", JsonHelper.toJsonString(upDownMessage));
            MessageAck messageAck = MessageAck.newBuilder()
                    .setId(upDownMessage.getRequestId())
                    .setConverId(upDownMessage.getConverId())
                    .setCode(Code.SUCCESS)
                    .setTime(System.currentTimeMillis())
                    .build();
            FridayMessage ravenMessage = FridayMessage.newBuilder().setType(FridayMessage.Type.MessageAck)
                    .setMessageAck(messageAck).build();
            ctx.writeAndFlush(ravenMessage);
        }
        if (message.getType() == FridayMessage.Type.HeartBeat) {
            HeartBeat heartBeat = message.getHeartBeat();
            log.info("receive heartbeat :{}", JsonHelper.toJsonString(heartBeat));
            if (heartBeat.getHeartBeatType() == HeartBeatType.PING) {
                HeartBeat heartBeatAck = HeartBeat.newBuilder()
                        .setId(heartBeat.getId())
                        .setHeartBeatType(HeartBeatType.PONG)
                        .build();
                FridayMessage ravenMessage = FridayMessage.newBuilder().setType(FridayMessage.Type.HeartBeat)
                        .setHeartBeat(heartBeatAck).build();
                ctx.writeAndFlush(ravenMessage);
            }
            for (String toUid : toUidList) {
                Thread.sleep(1000);
                MessageContent content = MessageContent.newBuilder().setUid(uid)
                        .setType(MessageType.TEXT)
                        .setContent("hello world").build();
                UpDownMessage upDownMessage = UpDownMessage.newBuilder()
                        .setCid(ClientFrom.snowFlake.nextId())
                        .setFromUid(uid)
                        .setToUid(toUid)
                        .setConverType(ConverType.SINGLE)
                        .setContent(content).build();
                FridayMessage ravenMessage = FridayMessage.newBuilder().setType(FridayMessage.Type.UpDownMessage)
                        .setUpDownMessage(upDownMessage).build();
                ctx.writeAndFlush(ravenMessage);
            }
            Thread.sleep(2000);
            ConverReq converReq = ConverReq.newBuilder().setId(ClientFrom.snowFlake.nextId())
                    .setType(OperationType.ALL)
                    .build();
            FridayMessage ravenMessage = FridayMessage.newBuilder().setType(FridayMessage.Type.ConverReq)
                    .setConverReq(converReq).build();
            ctx.writeAndFlush(ravenMessage);
        }
        if (message.getType() == FridayMessage.Type.ConverAck) {
            ConverAck converAck = message.getConverAck();
            log.info("receive conver ack message:{}", JsonHelper.toJsonString(converAck));
            Long beginTime = Long.valueOf("1");
            for (ConverInfo converInfo : converAck.getConverListList()) {
                HisMessagesReq hisMessagesReq = HisMessagesReq.newBuilder()
                        .setId(ClientFrom.snowFlake.nextId())
                        .setBeginId(beginTime).setConverId(converInfo.getConverId()).build();
                FridayMessage ravenMessage = FridayMessage.newBuilder().setType(FridayMessage.Type.HisMessagesReq)
                        .setHisMessagesReq(hisMessagesReq).build();
                ctx.writeAndFlush(ravenMessage);
            }
        }
        if (message.getType() == FridayMessage.Type.HisMessagesAck) {
            HisMessagesAck hisMessagesAck = message.getHisMessagesAck();
            log.info("receive history message ack:{}", JsonHelper.toJsonString(hisMessagesAck));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if ("Connection reset by peer".equals(cause.getMessage())) {
            return;
        }
        log.error(cause.getMessage(), cause);
    }
}
