package single;

import com.friday.common.bean.token.Token;
import com.friday.common.protobuf.Message.*;
import com.friday.common.protobuf.Message.FridayMessage.Type;
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
 * @Date: 2020-05-25:11:31
 */
@Slf4j
public class ClientToHandler extends SimpleChannelInboundHandler<FridayMessage> {

    private ChannelHandlerContext messageConnectionCtx;

    private String uid = "test1";
    private String secret = "test123456";


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        messageConnectionCtx = ctx;
        sendLogin(ctx, uid);
    }

    private void sendLogin(ChannelHandlerContext ctx, String uid) {
        String token = new Token(uid, secret).getToken(secret);
        Login login = Login.newBuilder()
                .setUid(uid)
                .setId(ClientTo.snowFlake.nextId())
                .setToken(token)
                .build();
        FridayMessage ravenMessage = FridayMessage.newBuilder().setType(FridayMessage.Type.Login).setLogin(login)
                .build();
        ctx.writeAndFlush(ravenMessage);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FridayMessage message)
            throws Exception {
        if (message.getType() == Type.LoginAck) {
            LoginAck loginAck = message.getLoginAck();
            log.info("login ack:{}", JsonHelper.toJsonString(loginAck));
            if (loginAck.getCode() == Code.SUCCESS) {
                log.info("waiting for incoming messages.");
            }
        } else if (message.getType() == Type.MessageAck) {
            MessageAck messageAck = message.getMessageAck();
            log.info("receive message ack:{}", JsonHelper.toJsonString(messageAck));
        } else if (message.getType() == Type.UpDownMessage) {
            UpDownMessage upDownMessage = message.getUpDownMessage();
            log.info("receive down message:{}", JsonHelper.toJsonString(upDownMessage));
            MessageAck messageAck = MessageAck.newBuilder()
                    .setId(upDownMessage.getRequestId())
                    .setConverId(upDownMessage.getConverId())
                    .setCode(Code.SUCCESS)
                    .setTime(System.currentTimeMillis())
                    .build();
            FridayMessage ravenMessage = FridayMessage.newBuilder().setType(Type.MessageAck)
                    .setMessageAck(messageAck).build();
            ctx.writeAndFlush(ravenMessage);
            MessageContent content = MessageContent.newBuilder().setUid(uid)
                    .setType(MessageType.TEXT)
                    .setContent("hello world").build();
            UpDownMessage upDownMessage1 = UpDownMessage.newBuilder()
                    .setCid(ClientFrom.snowFlake.nextId())
                    .setFromUid(uid)
                    .setToUid(upDownMessage.getFromUid())
                    .setConverType(ConverType.SINGLE)
                    .setContent(content).build();
            ravenMessage = FridayMessage.newBuilder().setType(Type.UpDownMessage)
                    .setUpDownMessage(upDownMessage1).build();
            ctx.writeAndFlush(ravenMessage);

        } else if (message.getType() == Type.HeartBeat) {
            HeartBeat heartBeat = message.getHeartBeat();
            log.info("receive hearbeat :{}", JsonHelper.toJsonString(heartBeat));
            if (heartBeat.getHeartBeatType() == HeartBeatType.PING) {
                HeartBeat heartBeatAck = HeartBeat.newBuilder()
                        .setId(heartBeat.getId())
                        .setHeartBeatType(HeartBeatType.PONG)
                        .build();
                FridayMessage ravenMessage = FridayMessage.newBuilder().setType(Type.HeartBeat)
                        .setHeartBeat(heartBeatAck).build();
                ctx.writeAndFlush(ravenMessage);
            }
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
